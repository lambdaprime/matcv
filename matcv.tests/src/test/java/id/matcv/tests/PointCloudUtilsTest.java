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

import id.matcv.PointCloudUtils;
import id.matcv.types.camera.CameraIntrinsicsPredefined;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PointCloudUtilsTest extends OpencvTest {
    @Test
    public void test() {
        var pc =
                new PointCloudUtils()
                        .readPointCloud(
                                Paths.get("samples/00000000-depth.png").toAbsolutePath(),
                                CameraIntrinsicsPredefined.REALSENSE_D435i_640_480
                                        .getCameraIntrinsics());
        Assertions.assertEquals(
                """
                { "x": -0.25188, "y": -0.18719, "z": 0.482 }""",
                pc.getPoint(0).toString());
        Assertions.assertEquals(
                """
                { "x": -0.1679, "y": -0.20388, "z": 0.525 }""",
                pc.getPoint(123).toString());
        Assertions.assertEquals(
                """
                { "x": 0.3639, "y": -0.3082, "z": 0.797 }""",
                pc.getPoint(1234).toString());
        Assertions.assertEquals(
                """
                { "x": -0.12012, "y": -0.19708, "z": 0.552 }""",
                pc.getPoint(12345).toString());
    }
}
