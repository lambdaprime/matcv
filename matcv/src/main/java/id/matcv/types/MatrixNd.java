/*
 * Copyright 2025 matcv project
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
package id.matcv.types;

import java.util.Arrays;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class MatrixNd {

    private int rows;
    private int cols;
    private double[] data;

    public MatrixNd(int rows, int cols, double[] data) {
        this.rows = rows;
        this.cols = cols;
        this.data = data;
    }

    public double get(int r, int c) {
        // use row-major storage order
        return data[c * rows + r];
    }

    public double[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return String.format(
                "Matrix [rows=%s, cols=%s, data=%s]", rows, cols, Arrays.toString(data));
    }
}
