/*
 * Copyright 2022 matcv project
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
package id.matcv;

import id.matcv.converters.MatConverters;
import id.xfunction.XByte;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opencv.core.MatOfInt;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class MatConvertersTest extends OpencvTest {

    private MatConverters converters = new MatConverters();

    @Test
    public void test_asMat() {
        var out = converters.copyToMat(XByte.castToByteArray(1, 2, 3));
        Assertions.assertEquals("[  1,   2,   3]", out.dump());
    }

    @Test
    public void test_toIntArray() {
        Assertions.assertEquals(
                "[1, 2, 3, 4, 5]",
                Arrays.toString(converters.copyToIntArray(new MatOfInt(1, 2, 3, 4, 5))));
    }

    @Test
    public void test_copyToMat32F() {
        Assertions.assertEquals(
                """
                [1, 2;
                 3, 4;
                 5, 6]""",
                converters.copyToMat32F(new double[] {1, 2, 3, 4, 5, 6}, 2, 3).dump());
    }
}
