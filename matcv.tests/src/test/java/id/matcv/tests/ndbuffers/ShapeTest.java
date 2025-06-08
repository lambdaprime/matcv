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

import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;

import id.matcv.types.ndbuffers.Shape;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class ShapeTest {
    @Test
    public void test_iterate() {
        assertEquals("[0]", new Shape(1).iterate().map(Arrays::toString).collect(joining("\n")));
        assertEquals(
                """
                [0, 0, 0]
                [0, 0, 1]
                [0, 0, 2]
                [0, 1, 0]
                [0, 1, 1]
                [0, 1, 2]""",
                new Shape(1, 2, 3).iterate().map(Arrays::toString).collect(joining("\n")));
    }

    @Test
    public void test_size() {
        var shape = new Shape(2, 3);
        assertEquals(6, shape.size(0));
        assertEquals(3, shape.size(1));
    }
}
