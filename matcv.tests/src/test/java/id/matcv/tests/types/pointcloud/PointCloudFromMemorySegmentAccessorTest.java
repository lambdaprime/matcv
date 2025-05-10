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
package id.matcv.tests.types.pointcloud;

import id.matcv.MatUtils;
import id.matcv.tests.OpencvTest;
import id.matcv.types.camera.CameraIntrinsicsPredefined;
import id.matcv.types.pointcloud.PointCloudFromMemorySegmentAccessor;
import java.lang.foreign.MemorySegment;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class PointCloudFromMemorySegmentAccessorTest extends OpencvTest {

    private static final MatUtils utils = new MatUtils();

    @Test
    public void test() {
        Mat depth =
                Imgcodecs.imread(
                        Paths.get("samples/00000000-depth.png").toAbsolutePath().toString(),
                        Imgcodecs.IMREAD_ANYDEPTH);
        utils.debugShape("depth", depth);
        var segment =
                MemorySegment.ofAddress(depth.dataAddr()).reinterpret(depth.total() * Short.BYTES);
        var intrinsics = CameraIntrinsicsPredefined.REALSENSE_D435i_640_480.getCameraIntrinsics();
        var pc = new PointCloudFromMemorySegmentAccessor(segment, intrinsics, 1000);
        Assertions.assertEquals(
                """
                { "x": -0.2518753144, "y": -0.1871852704, "z": 0.482 }""",
                pc.getPoint(0).toString());
        Assertions.assertEquals(
                """
                { "x": -0.1678995414, "y": -0.2038843713, "z": 0.525 }""",
                pc.getPoint(123).toString());
        Assertions.assertEquals(
                """
                { "x": 0.3639036187, "y": -0.3082019363, "z": 0.797 }""",
                pc.getPoint(1234).toString());
        Assertions.assertEquals(
                """
                { "x": -0.1201192432, "y": -0.1970790384, "z": 0.552 }""",
                pc.getPoint(12345).toString());
    }
}
