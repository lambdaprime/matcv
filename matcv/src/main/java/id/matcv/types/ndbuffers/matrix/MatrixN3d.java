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

import id.matcv.types.ndbuffers.NSlice;
import id.matcv.types.ndbuffers.Slice;
import java.nio.DoubleBuffer;

/**
 * Matrix Nx3
 *
 * @author lambdaprime intid@protonmail.com
 */
public class MatrixN3d extends MatrixNd {
    public MatrixN3d(double[] data) {
        super(data.length / 3, 3, data);
    }

    public MatrixN3d(DoubleBuffer data) {
        super(data.capacity() / 3, 3, data);
    }

    public MatrixN3d(Vector3d... vecs) {
        this(new double[vecs.length * 3]);
        var data = duplicate();
        for (var v : vecs) {
            data.put(v.duplicate());
        }
    }

    public Vector3d getVectorView(int row) {
        return new Vector3d(new NSlice(new Slice(row, row + 1, 1), new Slice(0, 3, 1)), data);
    }
}
