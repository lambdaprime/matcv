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

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class DoubleNdBuffer extends NdBuffer {
    private DoubleBuffer data;

    public DoubleNdBuffer(Shape shape, NSlice nslice, DoubleBuffer data) {
        super(shape, nslice);
        this.data = data.duplicate();
        this.data.limit(shape.size(0));
    }

    public double get(int... indices) {
        return data.get(mapper.map(indices));
    }

    public void set(double v, int... indices) {
        data.put(mapper.map(indices), v);
    }

    /** Create duplicate of internal {@link ByteBuffer} by calling {@link ByteBuffer#duplicate()} */
    public DoubleBuffer duplicate() {
        return data.duplicate();
    }
}
