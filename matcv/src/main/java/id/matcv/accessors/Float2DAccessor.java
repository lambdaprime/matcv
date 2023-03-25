/*
 * Copyright 2023 matcv project
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
package id.matcv.accessors;

import id.xfunction.Preconditions;

/**
 * Provides abstract access to the 2D data without exposing the details where and how it is stored.
 *
 * <p>It helps to write generic algorithms which can process data from different sources.
 *
 * @author lambdaprime intid@protonmail.com
 */
public interface Float2DAccessor {
    float get(int row, int col);

    float get(double row, double col);

    int rows();

    int cols();

    public static Float2DAccessor fromArray(float[] a, int rows, int cols) {
        if (cols * rows < a.length)
            throw new IllegalArgumentException("Number of rows and cols exceeds array length");
        return new Float2DAccessor() {
            @Override
            public float get(int row, int col) {
                return a[row * cols + col];
            }

            @Override
            public int cols() {
                return cols;
            }

            @Override
            public int rows() {
                return rows;
            }

            @Override
            public float get(double row, double col) {
                Preconditions.isTrue(row - (long) row == 0, "Row index is not a whole number");
                Preconditions.isTrue(col - (long) col == 0, "Col index is not a whole number");
                return get((int) row, (int) col);
            }
        };
    }

    /**
     * @param shape shape[0] = number of rows, shape[1] = number of columns
     */
    public static Float2DAccessor fromArray(float[] a, int[] shape) {
        return fromArray(a, shape[0], shape[1]);
    }

    public static Float2DAccessor fromArray(float[][] a) {
        return new Float2DAccessor() {
            @Override
            public float get(int row, int col) {
                return a[row][col];
            }

            @Override
            public float get(double row, double col) {
                Preconditions.isTrue(row - (long) row == 0, "Row index is not a whole number");
                Preconditions.isTrue(col - (long) col == 0, "Col index is not a whole number");
                return get((int) row, (int) col);
            }

            @Override
            public int cols() {
                return a[0].length;
            }

            @Override
            public int rows() {
                return a.length;
            }
        };
    }

    @FunctionalInterface
    public interface Getter {
        float get(int row, int col);
    }

    public static Float2DAccessor fromGetter(int rows, int cols, Getter getter) {
        return new Float2DAccessor() {
            @Override
            public float get(int row, int col) {
                return getter.get(row, col);
            }

            @Override
            public float get(double row, double col) {
                Preconditions.isTrue(row - (long) row == 0, "Row index is not a whole number");
                Preconditions.isTrue(col - (long) col == 0, "Col index is not a whole number");
                return get((int) row, (int) col);
            }

            @Override
            public int cols() {
                return cols;
            }

            @Override
            public int rows() {
                return rows;
            }
        };
    }
}
