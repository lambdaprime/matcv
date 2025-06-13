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
package id.matcv.types.ndbuffers;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class NdTo1dMapper {
    private Shape shape;
    private NSlice nslice;

    public NdTo1dMapper(Shape shape, NSlice nslice) {
        if (shape.dims().length != nslice.slices().length)
            throw new IllegalArgumentException(
                    "mismatch between number of slices and shape dimensions");
        this.shape = shape;
        this.nslice = nslice;
    }

    public int map(int... indices) {
        if (shape.dims().length != indices.length) throw new IllegalArgumentException();
        var index = 0;
        for (int i = 0; i < indices.length; i++) {
            var stride = 1;
            if (i + 1 < indices.length) {
                stride = shape.size(i + 1);
            }
            index += nslice.slices()[i].index(indices[i]) * stride;
        }
        return index;
    }
}
