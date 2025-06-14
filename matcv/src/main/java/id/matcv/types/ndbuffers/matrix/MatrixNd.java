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
package id.matcv.types.ndbuffers.matrix;

import id.matcv.types.ndbuffers.DoubleNdBuffer;
import id.matcv.types.ndbuffers.NSlice;
import id.matcv.types.ndbuffers.Shape;
import id.matcv.types.ndbuffers.Slice;
import java.nio.DoubleBuffer;

/**
 * Matrix NxN
 *
 * @author lambdaprime intid@protonmail.com
 */
public class MatrixNd extends DoubleNdBuffer {

    public MatrixNd(int rows, int cols, double[] data) {
        this(rows, cols, DoubleBuffer.wrap(data));
    }

    public MatrixNd(int rows, int cols, DoubleBuffer data) {
        super(
                new Shape(rows, cols),
                new NSlice(new Slice(0, rows, 1), new Slice(0, cols, 1)),
                data);
    }

    public int getRows() {
        return shape.dims()[0];
    }

    public int getCols() {
        return shape.dims()[1];
    }

    public String dump() {
        var rows = getRows();
        var cols = getCols();
        var buf = new StringBuilder();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (c == 0) buf.append(" ");
                var num = get(r, c);
                var str = formatter.format(num);
                if (str.equals("-0")) str = "0";
                buf.append(str);
                if (c < cols - 1) buf.append(", ");
            }
            if (r < rows - 1) buf.append(",\n");
        }
        return "{ \"data\" : [\n" + buf.toString() + "\n] }";
    }
}
