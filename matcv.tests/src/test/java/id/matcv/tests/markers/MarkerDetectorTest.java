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
package id.matcv.tests.markers;

import id.matcv.markers.MarkerDetector;
import id.matcv.tests.OpencvTest;
import id.xfunction.ResourceUtils;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class MarkerDetectorTest extends OpencvTest {

    private static final ResourceUtils utils = new ResourceUtils();

    @Test
    public void test() {
        Mat img = Imgcodecs.imread(Paths.get("samples/1.png").toAbsolutePath().toString());
        var result = new MarkerDetector().detect(img);
        Assertions.assertEquals(
                utils.readResource("markers"), result.markersSortedByType().toString());
    }
}
