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

import id.mathcalc.Vector2f;

/**
 * Provides abstract access to the 2D data without exposing the details where and how it is stored.
 *
 * <p>It helps to write generic algorithms which can process data from different sources.
 *
 * @author lambdaprime intid@protonmail.com
 */
public interface Vector2f2DAccessor {
    Vector2f get(int row, int col);

    int rows();

    int cols();

    @FunctionalInterface
    public interface Getter {
        Vector2f get(int row, int col);
    }

    public static Vector2f2DAccessor fromGetter(int rows, int cols, Getter getter) {
        return new Vector2f2DAccessor() {
            @Override
            public Vector2f get(int row, int col) {
                return getter.get(row, col);
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
