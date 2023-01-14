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

import id.xfunction.XByte;
import java.io.IOException;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Size;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class MatUtilsTest extends OpencvTest {

    private MatUtils utils = new MatUtils();

    @Test
    public void test_asMat() {
        var out = utils.newMat(XByte.castToByteArray(1, 2, 3));
        Assertions.assertEquals("[  1,   2,   3]", out.dump());
    }

    @Test
    public void test_toIntArray() {
        Assertions.assertEquals(
                "[1, 2, 3, 4, 5]", Arrays.toString(utils.toIntArray(new MatOfInt(1, 2, 3, 4, 5))));
    }

    @Test
    public void test_findNonZeroPoints() {
        Assertions.assertEquals(
                "[{0.0, 0.0}, {0.0, 2.0}, {0.0, 4.0}]",
                utils.findNonZeroPoints(new MatOfInt(1, 0, 3, 0, 5)).toString());
    }

    @Test
    public void test_findPeaks() {
        var mat =
                new MatOfFloat(1, 0, 3, 0, 1, 1, 0, 3, 4, 5, 1, 7, 3, 0, 5, 1, 0, 3, 0, 5)
                        .reshape(1, 4);
        System.out.println(mat);
        System.out.println(mat.dump());
        Assertions.assertEquals(
                "[{4.0, 1.0}, {1.0, 2.0}, {4.0, 2.0}, {4.0, 3.0}]",
                utils.findPeaks(mat, 5).toString());
    }

    @Test
    public void test_resize() throws IOException {
        var utils = new MatUtils();
        var img = utils.readImageFromResource("alita.jpg");
        img = utils.resize(img, new Size(140, 60));
    }
}
