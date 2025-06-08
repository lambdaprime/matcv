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
package id.matcv.tests;

import id.matcv.converters.PointConverters;
import id.matcv.types.ndbuffers.matrix.Vector2D;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PointConvertersTest {
    @Test
    public void test() {
        var converters = new PointConverters();
        Assertions.assertEquals(207483, converters.toIndex(new Vector2D(123, 324), 640, 480));
        Assertions.assertEquals(
                """
                { "x": 123, "y": 324 }""",
                converters.toPoint2d(207483, 640, 480).toString());
    }
}
