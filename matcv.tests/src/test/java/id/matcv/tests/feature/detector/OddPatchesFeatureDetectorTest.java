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
package id.matcv.tests.feature.detector;

import id.matcv.MatUtils;
import id.matcv.SubmatrixDetector;
import id.matcv.feature.detector.OddPatchesFeatureDetector;
import id.matcv.tests.OpenCvTest;
import java.io.IOException;
import org.opencv.highgui.HighGui;

public class OddPatchesFeatureDetectorTest extends OpenCvTest {

    // @Test
    public void demo() throws IOException {
        var utils = new MatUtils();
        var img = utils.readImageFromResource("alita.jpg");
        var detector = new SubmatrixDetector(img);
        new OddPatchesFeatureDetector(16).apply(img).forEach(detector);
        HighGui.imshow("test", img);
        HighGui.waitKey();
    }
}
