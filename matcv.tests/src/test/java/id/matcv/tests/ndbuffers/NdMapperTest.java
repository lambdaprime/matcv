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
package id.matcv.tests.ndbuffers;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import id.matcv.types.ndbuffers.NSlice;
import id.matcv.types.ndbuffers.NdMapper;
import id.matcv.types.ndbuffers.Shape;
import id.matcv.types.ndbuffers.Slice;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class NdMapperTest {

    @Test
    public void test() {
        var mapper1 = new NdMapper(new Shape(17), new NSlice(new Slice(0, 17, 1)));
        assertArrayEquals(
                new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16},
                IntStream.range(0, 17).map(mapper1::map).toArray());

        var shape = new Shape(4, 3);
        // indices:
        // 0 1  2
        // 3 4  5
        // 6 7  8
        // 9 10 11
        var mapper2 =
                new NdMapper(
                        shape,
                        new NSlice(
                                new Slice(0, shape.dims()[0], 2),
                                new Slice(0, shape.dims()[1], shape.dims()[1])));
        assertEquals(0, mapper2.map(0, 0));
        assertEquals(6, mapper2.map(1, 0));
        Assertions.assertThrows(
                ArrayIndexOutOfBoundsException.class, () -> assertEquals(6, mapper2.map(1, 1)));

        var mapper3 =
                new NdMapper(
                        shape, new NSlice(new Slice(1, 3, 1), new Slice(0, shape.dims()[1], 1)));
        assertEquals(3, mapper3.map(0, 0));
        assertEquals(4, mapper3.map(0, 1));
        assertEquals(5, mapper3.map(0, 2));
        assertEquals(6, mapper3.map(1, 0));
        assertEquals(7, mapper3.map(1, 1));
        assertEquals(8, mapper3.map(1, 2));
        Assertions.assertThrows(
                ArrayIndexOutOfBoundsException.class, () -> assertEquals(6, mapper3.map(2, 0)));
    }
}
