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

import id.matcv.MatUtils;
import id.matcv.markers.MarkerDetector3d;
import id.matcv.tests.OpenCvTest;
import id.matcv.types.camera.CameraInfoPredefined;
import id.matcv.types.datatables.DataTable2;
import id.matcv.types.pointcloud.PointCloudFromMemorySegmentAccessor;
import java.lang.foreign.MemorySegment;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class MarkerDetector3dTest extends OpenCvTest {

    private static final MatUtils utils = new MatUtils();

    @Test
    public void test() {
        Mat rgb =
                Imgcodecs.imread(Paths.get("samples/00000000-rgb.png").toAbsolutePath().toString());
        utils.debugShape("rgb", rgb);
        Mat depth =
                Imgcodecs.imread(
                        Paths.get("samples/00000000-depth.png").toAbsolutePath().toString(),
                        Imgcodecs.IMREAD_ANYDEPTH);
        utils.debugShape("depth", depth);
        utils.debugMat("depth", depth, new Rect(0, 0, 10, 10));
        var segment =
                MemorySegment.ofAddress(depth.dataAddr()).reinterpret(depth.total() * Short.BYTES);
        var cameraInfo = CameraInfoPredefined.REALSENSE_D435i_640_480.getCameraInfo();
        var pc =
                new PointCloudFromMemorySegmentAccessor(
                        segment, cameraInfo.cameraIntrinsics(), 1000);
        var markers =
                new MarkerDetector3d(cameraInfo)
                        .detect(new DataTable2<>(List.of(rgb), List.of(pc)))
                        .col2();
        Assertions.assertEquals(
                """
[[\
{ "marker": "ONE",\
 "imageFile": "Optional.empty",\
 "center": { "x": 0.03501, "y": 0.03701, "z": 0.494 },\
 "p1": { "x": 0.01265, "y": 0.01459, "z": 0.48 },\
 "p2": { "x": 0.0578, "y": 0.01027, "z": 0.501 },\
 "p3": { "x": 0.05895, "y": 0.06103, "z": 0.511 },\
 "p4": { "x": 0.01284, "y": 0.06539, "z": 0.487 } }, \
{ "marker": "TWO",\
 "imageFile": "Optional.empty",\
 "center": { "x": 0.05102, "y": -0.1399, "z": 0.645 },\
 "p1": { "x": 0.02716, "y": -0.16364, "z": 0.634 },\
 "p2": { "x": 0.07506, "y": -0.16709, "z": 0.66 },\
 "p3": { "x": 0.07626, "y": -0.11612, "z": 0.661 },\
 "p4": { "x": 0.03044, "y": -0.11191, "z": 0.637 } }, \
{ "marker": "FOUR",\
 "imageFile": "Optional.empty",\
 "center": { "x": -0.07169, "y": 0.09219, "z": 0.453 },\
 "p1": { "x": -0.03676, "y": 0.0941, "z": 0.455 },\
 "p2": { "x": -0.08348, "y": 0.09771, "z": 0.422 },\
 "p3": { "x": -0.1077, "y": 0.09332, "z": 0.47 },\
 "p4": { "x": -0.06207, "y": 0.08975, "z": 0.502 } }, \
{ "marker": "FIVE",\
 "imageFile": "Optional.empty",\
 "center": { "x": -0.12371, "y": -0.04945, "z": 0.56 },\
 "p1": { "x": -0.14698, "y": -0.07265, "z": 0.547 },\
 "p2": { "x": -0.10055, "y": -0.07664, "z": 0.57 },\
 "p3": { "x": -0.0991, "y": -0.02531, "z": 0.578 },\
 "p4": { "x": -0.14481, "y": -0.02132, "z": 0.549 } }, \
{ "marker": "SIX",\
 "imageFile": "Optional.empty",\
 "center": { "x": 0.07579, "y": 0.09738, "z": 0.38 },\
 "p1": { "x": 0.06349, "y": 0.10208, "z": 0.347 },\
 "p2": { "x": 0.04112, "y": 0.09691, "z": 0.396 },\
 "p3": { "x": 0.08957, "y": 0.09334, "z": 0.418 },\
 "p4": { "x": 0.11131, "y": 0.0976, "z": 0.369 } }]]""",
                markers.toString());
    }
}
