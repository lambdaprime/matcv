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
package id.matcv.converters;

import id.ndbuffers.matrix.Matrix3d;
import id.ndbuffers.matrix.MatrixN3d;
import id.ndbuffers.matrix.MatrixNd;
import id.ndbuffers.matrix.Vector2d;
import id.xfunction.Preconditions;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.utils.Converters;

/**
 * Addition to standard {@link Converters}.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class ConvertersToOpenCv {
    /** Create new CV_8U matrix */
    public Mat copyToMat(byte... values) {
        var mat = new Mat(1, values.length, CvType.CV_8U);
        mat.put(0, 0, values);
        return mat;
    }

    public Mat copyToMat64F(double[] data, int cols, int rows) {
        return new MatOfDouble(data).reshape(1, new int[] {rows, cols});
    }

    public Mat copyToMat32F(double[] data, int cols, int rows) {
        var r = new Mat();
        new MatOfDouble(data).convertTo(r, CvType.CV_32F);
        return r.reshape(1, new int[] {rows, cols});
    }

    public Mat copyToMat32F(MatrixNd mx) {
        var r = new Mat();
        new MatOfDouble(mx.duplicate().array()).convertTo(r, CvType.CV_32F);
        return r.reshape(mx.getCols(), new int[] {mx.getRows()});
    }

    public Mat copyToMat32F(Vector2d v) {
        return new MatOfPoint2f(toPoint(v));
    }

    /**
     * Certain methods in OpenCV accept {@link List<Mat>} instead of {@link List<? extends Mat>}
     * (example is {@link Core#vconcat(List, Mat)}) This cause a compile time error if you have list
     * of some other types which extend {@link Mat}. To deal with this you may need to use either
     * cast operation or this method.
     */
    public List<Mat> mapToListOfMat(List<? extends Mat> list) {
        return (List<Mat>) list;
    }

    public Mat toMat64F(Matrix3d mx) {
        return copyToMat64F(mx.duplicate().array(), 3, 3);
    }

    public Mat toMat32F(Matrix3d mx) {
        return copyToMat32F(mx.duplicate().array(), 3, 3);
    }

    /**
     * Return {@link MatOfDouble} of type {@link CvType#CV_64FC1} which always 1 dimensional (see <a
     * href="https://github.com/vRallev/OpenCV/blob/60fe17cfd6519390b37888b0db28e6a40ff2eca6/opencv-library/opencv-android/src/main/java/org/opencv/core/MatOfDouble.java">code</a>
     */
    public MatOfDouble toMatOfDouble(MatrixNd mx) {
        return new MatOfDouble(mx.duplicate().array());
    }

    public Point toPoint(Vector2d point) {
        return toPoint(point, 0, 0);
    }

    public Point toPoint(Vector2d point, int offsetX, int offsetY) {
        return new Point(point.getX() + offsetX, point.getY() + offsetY);
    }

    /**
     * Input matrix should be continuous vector with 1 or 2 channels ({@link CvType#CV_32S}, {@link
     * CvType#CV_32SC2})
     */
    public int[] copyToIntArray(Mat matrix) {
        Preconditions.isTrue(matrix.isContinuous(), "Non continous matrix");
        var type = matrix.type();
        Preconditions.isTrue(
                type == CvType.CV_32S || type == CvType.CV_32SC2, "Incompatible matrix type");
        Preconditions.equals(2, matrix.dims(), "Incompatible matrix dimension");
        var buf = new int[matrix.channels() * matrix.size(0)];
        matrix.get(0, 0, buf);
        return buf;
    }

    public MatOfPoint3f copyToMatOfPoint32F(float[] data) {
        Preconditions.equals(0, data.length % 3);
        return new MatOfPoint3f(new MatOfFloat(data).reshape(3, data.length / 3));
    }

    public MatOfPoint3f copyToMatOfPoint32F(MatrixN3d mx) {
        return new MatOfPoint3f(copyToMat32F(mx));
    }
}
