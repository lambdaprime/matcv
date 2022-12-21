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
package id.matcv.grid;

import org.opencv.core.Mat;

public class Cell {

    public Mat mat;
    public int x;
    public int y;

    public Cell(Mat mat, int x, int y) {
        this.mat = mat;
        this.x = x;
        this.y = y;
    }

    public Mat getMat() {
        return mat;
    }

    @Override
    public String toString() {
        return String.format("x %d y %d", x, y);
    }
}
