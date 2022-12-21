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

import java.util.LinkedList;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

public class OpencvKit {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    /** Add an alpha 255 channel to the RGB image */
    public static Mat addAlpha(Mat mat) {
        var mv = new LinkedList<Mat>();
        var alpha = new Mat(mat.size(), CvType.CV_8UC1, new Scalar(255));
        Core.split(mat, mv);
        mv.addLast(alpha);
        var ret = new Mat();
        Core.merge(mv, ret);
        System.out.println(ret);
        return ret;
    }

    /** Returns middle point */
    public static Point middlePoint(Point p1, Point p2) {
        return new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
    }

    /**
     * Flattens scalars into one dimensional array.
     *
     * <p>Example:
     *
     * <p>Input: Scalar(1.0, 2.0, 3.0), Scalar(3.0, 4.0, 5.0), Scalar(6.0, 7.0, 8.0) Output: [1, 2,
     * 3, 0, 3, 4, 5, 0, 6, 7, 8, 0]
     *
     * <p>Scalar sizes aligned which means Scalar(1.0, 2.0, 3.0) in memory will take size 4 not 3 =>
     * the output array has additional zeros.
     */
    public static Mat toFlatMatrix(List<Scalar> scalars) {
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
}
