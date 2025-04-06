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
package id.mathcat.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import id.matcv.Vector2D;
import id.mathcat.LineUtils;
import org.junit.jupiter.api.Test;

public class LineUtilsTest {

    @Test
    public void test() {
        assertEquals(
                """
                { "x": 2, "y": -2 }""",
                LineUtils.createVector(new Vector2D(3, 5), new Vector2D(5, 3)).toString());
    }
}
