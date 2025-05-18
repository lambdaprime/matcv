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
package id.matcv.impl.ejml;

import id.xfunction.Preconditions;
import org.ejml.data.DMatrixRMaj;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class EjmlUtils {

    public DMatrixRMaj mean(DMatrixRMaj mx) {
        // Calculate the number of rows (samples) and columns (features) in the matrix
        int numRows = mx.getNumRows();
        int numCols = mx.getNumCols();

        // Create a new DMatrixRMaj to store the mean for each column
        DMatrixRMaj result = new DMatrixRMaj(1, numCols);

        // Iterate through each column of the input matrix and calculate its average (mean)
        for (int j = 0; j < numCols; j++) {
            double sum = 0.0;

            // Sum up all elements in the current column
            for (int i = 0; i < numRows; i++) {
                sum += mx.get(i, j);
            }

            // Store the mean of the current column in the result matrix
            result.set(0, j, sum / numRows);
        }

        return result;
    }

    public double meanAll(DMatrixRMaj mx) {
        double sum = 0;
        int count = mx.getNumRows();

        for (int i = 0; i < count; i++) {
            for (int j = 0; j < mx.getNumCols(); j++) {
                sum += mx.get(i, j);
            }
        }
        return sum / count;
    }

    /** Substract vector from each row of the matrix */
    public DMatrixRMaj subtract(DMatrixRMaj matrix, DMatrixRMaj vector) {
        Preconditions.equals(matrix.getNumCols(), vector.getNumCols());
        Preconditions.equals(1, vector.getNumRows());
        var out = new DMatrixRMaj(matrix.getNumRows(), matrix.getNumCols());
        for (int r = 0; r < matrix.getNumRows(); r++) {
            for (int c = 0; c < matrix.getNumCols(); c++) {
                out.set(r, c, matrix.get(r, c) - vector.get(0, c));
            }
        }
        return out;
    }
}
