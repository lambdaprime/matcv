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

import java.util.function.Consumer;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Every time we detect that given submatrix belongs to original matrix we highlight its location on
 * original matrix itself.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class SubmatrixDetector implements Consumer<Mat> {

    private Mat matrix;

    public SubmatrixDetector(Mat matrix) {
        this.matrix = matrix;
    }

    @Override
    public void accept(Mat submatrix) {
        if (!submatrix.isSubmatrix()) return;
        var size = new Size();
        var point = new Point();
        submatrix.locateROI(size, point);
        Imgproc.rectangle(matrix, new Rect(point, submatrix.size()), RgbColors.GREEN);
    }
}
