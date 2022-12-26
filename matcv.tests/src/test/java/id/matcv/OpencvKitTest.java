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

import id.xfunction.ResourceUtils;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class OpencvKitTest extends OpencvTest {
    private static final ResourceUtils utils = new ResourceUtils();

    @Test
    public void test_toFlatMatrix() {
        Mat out =
                OpencvKit.toFlatMatrix(
                        List.of(
                                new Scalar(1.0, 2.0, 3.0),
                                new Scalar(3.0, 4.0, 5.0),
                                new Scalar(6.0, 7.0, 8.0)));
        Assertions.assertEquals("[1, 2, 3, 0, 3, 4, 5, 0, 6, 7, 8, 0]", out.dump());
    }

    @Test
    public void test_drawVectorField() throws IOException {
        var image = Mat.zeros(new int[] {60, 60}, CvType.CV_8UC3);
        var shadowX =
                new float[][] {
                    {0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0},
                    {0, 1, 1, 1, 0, 0},
                    {0, 1, 1, 1, 0, 0},
                    {0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0}
                };
        var shadowY =
                new float[][] {
                    {0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0},
                    {0, -1, 1, -1, 0, 0},
                    {0, -1, 1, -1, 0, 0},
                    {0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0}
                };
        OpencvKit.drawVectorField(
                image,
                10,
                10,
                RgbColors.RED,
                (x, y) -> 6 * shadowY[x / 10][y / 10],
                (x, y) -> 6 * shadowX[x / 10][y / 10]);
        Assertions.assertEquals(utils.readResource("drawVectorField"), image.dump());
    }
}
