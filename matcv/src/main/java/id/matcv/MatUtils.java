/*
 * Copyright 2022 matcv project
 * 
 * Website: https://github.com/lambdaprime/matcv
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package id.matcv;

import id.xfunction.Preconditions;
import id.xfunction.ResourceUtils;
import id.xfunction.logging.XLogger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/** Collections of functions around OpenCV {@link Mat} */
public class MatUtils {

    private static final Logger LOGGER = XLogger.getLogger(MatUtils.class);
    private ResourceUtils resourceUtils = new ResourceUtils();

    /** Create new CV_8U matrix */
    public Mat newMat(byte... values) {
        var mat = new Mat(1, values.length, CvType.CV_8U);
        mat.put(0, 0, values);
        return mat;
    }

    public Mat readImageFromResource(String absoluteResourcePath) throws IOException {
        Path out = Files.createTempFile("resource", "");
        resourceUtils.extractResource(absoluteResourcePath, out);
        return Imgcodecs.imread(out.toAbsolutePath().toString());
    }

    /**
     * Certain methos in OpenCV accept List&ltMat> instead of List&lt? extends ;Mat> (example is
     * {@link Core#vconcat(List, Mat)}) This cause a compile time error if you have list of some
     * other types which extend {@link Mat}. To deal with this you may need to use either cast
     * operation or this method.
     */
    public List<Mat> toListOfMat(List<? extends Mat> list) {
        return (List<Mat>) list;
    }

    /**
     * Input matrix should be continuous vector with 1 or 2 channels ({@link CvType#CV_32S}, {@link
     * CvType#CV_32SC2})
     */
    public int[] toIntArray(Mat matrix) {
        Preconditions.isTrue(matrix.isContinuous(), "Non continous matrix");
        var type = matrix.type();
        Preconditions.isTrue(
                type == CvType.CV_32S || type == CvType.CV_32SC2, "Incompatible matrix type");
        Preconditions.equals(2, matrix.dims(), "Incompatible matrix dimension");
        var buf = new int[matrix.channels() * matrix.size(0)];
        matrix.get(0, 0, buf);
        return buf;
    }

    /** Extension of {@link Core#findNonZero(Mat, Mat)} which returns list of {@link Point} */
    public List<Point> findNonZeroPoints(Mat matrix2d) {
        Preconditions.equals(2, matrix2d.dims(), "Incompatible matrix dimension");
        var points = new MatOfPoint();
        Core.findNonZero(matrix2d, points);
        return points.toList();
    }

    /**
     * Find local peaks in the matrix which are greater or equals threshold value.
     *
     * <p>Example:
     *
     * <pre>{@code
     * var mat = new MatOfFloat(
     *     1, 0, 3, 0, 1,
     *     1, 0, 3, 4, 5,
     *     1, 7, 3, 0, 5,
     *     1, 0, 3, 0, 5).reshape(1, 4);
     * }</pre>
     *
     * <p>Will find following points: {4.0, 1.0}, {1.0, 2.0}, {4.0, 2.0}, {4.0, 3.0}
     */
    public List<Point> findPeaks(Mat matrix, double threshold) {
        var mask1 = new Mat();
        Core.compare(matrix, new Scalar(threshold), mask1, Core.CMP_GE);
        var mask2 = new Mat();
        // highlights everything except peaks
        Imgproc.dilate(matrix, mask2, new Mat());
        // extracts what was not highlighted
        Core.compare(matrix, mask2, mask2, Core.CMP_GE);
        Core.bitwise_and(mask1, mask2, mask1);
        return findNonZeroPoints(mask1);
    }

    /**
     * Log given slice of the matrix (ROI) including its description and shape.
     *
     * <p>It works only when DEBUG ({@link Level.FINER} logging level is enabled, otherwise it does
     * nothing.
     */
    public void debugMat(String description, Mat matrix, Rect slice) {
        if (!LOGGER.isLoggable(Level.FINER)) return;
        LOGGER.fine(description + " shape " + matrix.toString());
        LOGGER.fine(description + " slice " + slice + ":\n" + matrix.submat(slice).dump());
    }

    /**
     * Add obj to background at position x, y. Transparent pixels will be ignored:
     *
     * <p>background[i] = obj[i] == 0? background[i]? obj[i]
     */
    public void overlay(Mat obj, Mat background, int x, int y) {
        var invObj = new Mat();
        Core.bitwise_not(obj, invObj);
        var submat = background.submat(new Rect(x, y, obj.width(), obj.height()));
        Core.bitwise_and(submat, invObj, submat);
        Core.bitwise_or(submat, obj, submat);
    }

    /**
     * Resize the image.
     *
     * <p>If the image cannot be resized preserving the ratio (ratio between width/height of
     * original image is not the same as in the requested rectangle) then image is resized based on
     * its longest side and will be positioned in the middle of the requested rectangle so that its
     * longest side will match the same side in the requested rectangle and empty areas around its
     * shortest sides will be filled black.
     *
     * @param size requested final size of the image
     */
    public Mat resize(Mat img, Size size) {
        Size s;
        double r = img.size().width / img.size().height;
        if (img.size().height > img.size().width) s = new Size(size.width * r, size.height);
        else s = new Size(size.width, size.height / r);
        var tmp = new Mat(s, img.type());
        Imgproc.resize(img, tmp, tmp.size());
        Mat tmp2 = Mat.zeros((int) size.height, (int) size.width, img.type());
        tmp.copyTo(
                tmp2.submat(
                        new Rect(
                                new double[] {
                                    (size.width - s.width) / 2,
                                    (size.height - s.height) / 2,
                                    s.width,
                                    s.height
                                })));
        return tmp2;
    }
}
