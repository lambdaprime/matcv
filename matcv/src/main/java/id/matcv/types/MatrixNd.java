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

import java.nio.DoubleBuffer;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class MatrixNd {

    private int rows;
    private int cols;
    private DoubleBuffer data;

    public MatrixNd(int rows, int cols, double[] data) {
        this(rows, cols, DoubleBuffer.wrap(data));
    }

    public MatrixNd(int rows, int cols, DoubleBuffer data) {
        this.rows = rows;
        this.cols = cols;
        this.data = data.duplicate();
        this.data.limit(rows * cols);
    }

    public double get(int r, int c) {
        // use row-major storage order
        return data.get(c * rows + r);
    }

    public void set(int r, int c, double v) {
        // use row-major storage order
        data.put(c * rows + r, v);
    }

    public DoubleBuffer getData() {
        return data;
    }

    @Override
    public String toString() {
        return String.format("Matrix [rows=%s, cols=%s, data=%s]", rows, cols, data);
    }
}
