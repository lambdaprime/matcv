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
import id.matcv.tests.OpencvTest;
import id.matcv.types.camera.CameraIntrinsicsPredefined;
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

public class MarkerDetector3dTest extends OpencvTest {

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
        var intrinsics = CameraIntrinsicsPredefined.REALSENSE_D435i_640_480.getCameraIntrinsics();
        var pc = new PointCloudFromMemorySegmentAccessor(segment, intrinsics, 1000);
        var markers =
                new MarkerDetector3d(intrinsics)
                        .detect(new DataTable2<>(List.of(rgb), List.of(pc)))
                        .col2();
        Assertions.assertEquals(
                """
                [[\
                { "marker": "ONE",\
                 "center": { "x": 0.0350068945, "y": 0.037007221, "z": 0.494 },\
                 "p1": { "x": 0.0126514224, "y": 0.014592209, "z": 0.48 },\
                 "p2": { "x": 0.057800965, "y": 0.0102748411, "z": 0.501 },\
                 "p3": { "x": 0.0589546769, "y": 0.0610268751, "z": 0.511 },\
                 "p4": { "x": 0.0128359223, "y": 0.0653865806, "z": 0.487 } }, \
                { "marker": "TWO",\
                 "center": { "x": 0.0510234998, "y": -0.1398965189, "z": 0.645 },\
                 "p1": { "x": 0.0271613301, "y": -0.1636414472, "z": 0.634 },\
                 "p2": { "x": 0.0750570343, "y": -0.1670880109, "z": 0.66 },\
                 "p3": { "x": 0.0762603551, "y": -0.1161232355, "z": 0.661 },\
                 "p4": { "x": 0.0304399624, "y": -0.1119069606, "z": 0.637 } }, \
                { "marker": "FOUR",\
                 "center": { "x": -0.0716938726, "y": 0.092188408, "z": 0.453 },\
                 "p1": { "x": -0.0367591866, "y": 0.0940956727, "z": 0.455 },\
                 "p2": { "x": -0.0834827505, "y": 0.0977069693, "z": 0.422 },\
                 "p3": { "x": -0.1076987065, "y": 0.0933234511, "z": 0.47 },\
                 "p4": { "x": -0.0620713168, "y": 0.0897460506, "z": 0.502 } }, \
                { "marker": "FIVE",\
                 "center": { "x": -0.1237063193, "y": -0.0494484541, "z": 0.56 },\
                 "p1": { "x": -0.1469832691, "y": -0.0726491367, "z": 0.547 },\
                 "p2": { "x": -0.1005463542, "y": -0.0766435718, "z": 0.57 },\
                 "p3": { "x": -0.0990991908, "y": -0.0253093736, "z": 0.578 },\
                 "p4": { "x": -0.1448057563, "y": -0.0213242351, "z": 0.549 } }, \
                { "marker": "SIX",\
                 "center": { "x": 0.0757872076, "y": 0.097379761, "z": 0.38 },\
                 "p1": { "x": 0.0634857095, "y": 0.102080801, "z": 0.347 },\
                 "p2": { "x": 0.041117602, "y": 0.0969099635, "z": 0.396 },\
                 "p3": { "x": 0.0895672411, "y": 0.0933352035, "z": 0.418 },\
                 "p4": { "x": 0.1113056565, "y": 0.0976025926, "z": 0.369 } }]]""",
                markers.toString());
    }
}
