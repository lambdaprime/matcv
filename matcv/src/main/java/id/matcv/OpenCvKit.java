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

import id.matcv.accessors.FloatMatrixAccessor;
import id.matcv.accessors.Vector2DMatrixAccessor;
import id.ndbuffers.matrix.Vector2d;
import id.xfunction.Preconditions;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class OpenCvKit {

    /** Add an alpha 255 channel to the RGB image */
    public Mat addAlpha(Mat mat) {
        var mv = new LinkedList<Mat>();
        var alpha = new Mat(mat.size(), CvType.CV_8UC1, new Scalar(255));
        Core.split(mat, mv);
        mv.addLast(alpha);
        var ret = new Mat();
        Core.merge(mv, ret);
        return ret;
    }

    /** Return middle point */
    public static Point middlePoint(Point p1, Point p2) {
        return new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
    }

    /**
     * Flatten scalars into one dimensional array.
     *
     * <p>Example:
     *
     * <p>Input: Scalar(1.0, 2.0, 3.0), Scalar(3.0, 4.0, 5.0), Scalar(6.0, 7.0, 8.0) Output: [1, 2,
     * 3, 0, 3, 4, 5, 0, 6, 7, 8, 0]
     *
     * <p>Scalar sizes aligned which means Scalar(1.0, 2.0, 3.0) in memory will take size 4 not 3 =>
     * the output array has additional zeros.
     */
    public Mat toFlatMatrix(List<Scalar> scalars) {
        int scalarLen = scalars.get(0).val.length;
        var mat = new Mat(1, scalars.size() * scalarLen, CvType.CV_32F);
        for (int i = 0; i < scalars.size(); i++) {
            var scalar = scalars.get(i).val;
            for (int j = 0; j < scalarLen; j++) {
                mat.put(0, i * scalarLen + j, scalar[j]);
            }
        }
        return mat;
    }

    /**
     * Draw vector field on the given image.
     *
     * <p>It iterates over image pixels with stepX, stepY steps and for each pixel it draws a vector
     * with shadowX and shadowY.
     *
     * <p>Example: draw 6x6 vector field on 60x60 image:
     *
     * <pre>{@code
     * var shadowX = new float[][] {
     *   {0, 0, 0, 0, 0, 0},
     *   {0, 0, 0, 0, 0, 0},
     *   {0, 1, 1, 1, 0, 0},
     *   {0, 1, 1, 1, 0, 0},
     *   {0, 0, 0, 0, 0, 0},
     *   {0, 0, 0, 0, 0, 0}};
     * var shadowY = new float[][] {
     *   {0, 0, 0, 0, 0, 0},
     *   {0, 0, 0, 0, 0, 0},
     *   {0, -1, 1, -1, 0, 0},
     *   {0, -1, 1, -1, 0, 0},
     *   {0, 0, 0, 0, 0, 0},
     *   {0, 0, 0, 0, 0, 0}};
     * OpencvKit.drawVectorField(image, 10, 10, RgbColors.RED,
     *   (x, y) -> 6 * shadowY[x / 10][y / 10], // scale each vector by 6 (or any other number)
     *   (x, y) -> 6 * shadowX[x / 10][y / 10]);
     * }</pre>
     *
     * @param image 2D matrix
     */
    public void drawVectorField(
            Mat image,
            int stepX,
            int stepY,
            Scalar color,
            FloatMatrixAccessor shadowX,
            FloatMatrixAccessor shadowY) {
        drawVectorField(
                image,
                stepX,
                stepY,
                color,
                Vector2DMatrixAccessor.fromGetter(
                        shadowX.rows(),
                        shadowX.cols(),
                        (x, y) -> new Vector2d(shadowX.get(x, y), shadowY.get(x, y))));
    }

    /**
     * @see #drawVectorField(Mat, int, int, Scalar, FloatMatrixAccessor, FloatMatrixAccessor)
     */
    public void drawVectorField(
            Mat image, int stepX, int stepY, Scalar color, Vector2DMatrixAccessor vectorCalc) {
        Preconditions.isTrue(stepX < image.cols(), "Step exceeds number of rows in the image");
        Preconditions.isTrue(stepY < image.rows(), "Step exceeds number of cols in the image");
        for (int x = 0; x < image.cols(); x += stepX) {
            for (int y = 0; y < image.rows(); y += stepY) {
                var from = new Point(x, y);
                var vec = vectorCalc.get(x, y);
                var to = new Point(x + vec.getX(), y + vec.getY());
                Imgproc.arrowedLine(image, from, to, color);
            }
        }
    }

    /**
     * Calculate new points with respect to weighted average for them.
     *
     * @param windowSize size of the window around the points which will define what surrounding
     *     points will take part in calculation of the average
     * @return list of same number of points as in original list where each point calculated based
     *     on its weighted average
     */
    public List<Point> applyWeightedAverage(
            FloatMatrixAccessor weights, int windowSize, List<Point> points) {
        var len = windowSize / 2;
        var out = new ArrayList<Point>(points.size());
        for (var p : points) {
            double sumWeights = 0;
            double sumCols = 0;
            double sumRows = 0;
            for (int row = Math.max(0, (int) p.y - len);
                    row < Math.min(weights.rows(), p.y + len + 1);
                    row++) {
                for (int col = Math.max(0, (int) p.x - len);
                        col < Math.min(weights.cols(), p.x + len + 1);
                        col++) {
                    var w = weights.get(row, col);
                    sumWeights += w;
                    sumCols += col * w;
                    sumRows += row * w;
                }
            }
            out.add(new Point(sumCols / sumWeights, sumRows / sumWeights));
        }
        return out;
    }
}
