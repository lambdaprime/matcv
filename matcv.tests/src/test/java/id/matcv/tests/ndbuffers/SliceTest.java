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
import static org.junit.jupiter.api.Assertions.assertThrows;

import id.matcv.types.ndbuffers.Slice;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class SliceTest {

    @Test
    public void test_iterate_EmptyRange() {
        var slice = new Slice(10, 10, 1);
        IntStream stream = slice.iterate();
        assertEquals(0, stream.count());
    }

    @Test
    public void test_iterate_SingleElement() {
        var slice = new Slice(0, 5, 1);
        IntStream stream = slice.iterate();
        int[] expectedArray = {0, 1, 2, 3, 4};
        assertArrayEquals(expectedArray, stream.toArray());

        slice = new Slice(0, 10, 10);
        expectedArray = new int[] {0};
        stream = slice.iterate();
        assertArrayEquals(expectedArray, stream.toArray());
    }

    @Test
    public void test_iterate_StepNotOne() {
        var slice = new Slice(10, 20, 3);
        IntStream stream = slice.iterate();
        int[] expectedArray = {10, 13, 16, 19};
        assertArrayEquals(expectedArray, stream.toArray());
    }

    @Test
    public void test_iterate_StopReached() {
        var slice = new Slice(0, 5, 1);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> slice.index(5));
    }

    @Test
    public void test_index_OutOfBounds() {
        var slice = new Slice(10, 20, 2);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> slice.index(-1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> slice.index(15));
    }

    @Test
    public void test_index() {
        Slice slice = new Slice(0, 5, 1);
        assertEquals(0, slice.index(0));
        assertEquals(1, slice.index(1));
        assertEquals(2, slice.index(2));
        assertEquals(3, slice.index(3));
        assertEquals(4, slice.index(4));

        slice = new Slice(0, 5, 2);
        assertEquals(0, slice.index(0));
        assertEquals(2, slice.index(1));
        assertEquals(4, slice.index(2));
    }

    @Test
    public void test_of() {
        var ex = assertThrows(IllegalArgumentException.class, () -> Slice.of("").toString());
        Assertions.assertEquals("Slice expression is empty", ex.getMessage());
        Assertions.assertEquals(
                "Slice[start=0, stop=2147483647, step=1]", Slice.of(":").toString());
        Assertions.assertEquals("Slice[start=1, stop=7, step=2]", Slice.of("1:7:2").toString());
        Assertions.assertEquals(
                "Slice[start=5, stop=2147483647, step=1]", Slice.of("5:").toString());
        Assertions.assertEquals("Slice[start=1, stop=2, step=1]", Slice.of("1:2").toString());
    }
}
