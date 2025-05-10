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
package id.matcv.tests.markers;

import id.matcv.markers.Marker;
import id.matcv.markers.Marker3dUtils;
import id.matcv.markers.MarkerLocation3d;
import id.matcv.markers.MarkerType;
import id.matcv.types.Matrix4d;
import id.matcv.types.Vector3D;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class Marker3dUtils_transformAll_Test {

    record TestCase(double[] tx, List<MarkerLocation3d> markersBefore, String expectedMx) {}

    static Stream<TestCase> dataProvider() {
        return Stream.of(
                new TestCase(
                        new double[] {
                            1, 0, 4, 0,
                            0, 1, -1, 0,
                            0, 0, 1, 0,
                            0, 0, 0, 0
                        },
                        List.of(
                                new MarkerLocation3d(
                                        new Marker(MarkerType.ONE),
                                        new Vector3D(0, 0, 0),
                                        new Vector3D(2, 3, 1),
                                        new Vector3D(2, -3, 1),
                                        new Vector3D(-2, -3, 1),
                                        new Vector3D(-2, 3, 1))),
                        """
                        [{ "x": 0, "y": 0, "z": 0 },\
                         { "x": 6, "y": 2, "z": 1 },\
                         { "x": 6, "y": -4, "z": 1 },\
                         { "x": 2, "y": -4, "z": 1 },\
                         { "x": 2, "y": 2, "z": 1 }]"""),
                // rotate 90 degrees ccw around center of coordinate system (0, 0)
                new TestCase(
                        new double[] {
                            0, -1, 0, 0,
                            1, 0, 0, 0,
                            0, 0, 1, 0,
                            0, 0, 0, 1
                        },
                        List.of(
                                new MarkerLocation3d(
                                        new Marker(MarkerType.ONE),
                                        new Vector3D(0, 0, 0),
                                        new Vector3D(2, 3, 1),
                                        new Vector3D(2, -3, 1),
                                        new Vector3D(-2, -3, 1),
                                        new Vector3D(-2, 3, 1))),
                        """
                        [{ "x": 0, "y": 0, "z": 0 },\
                         { "x": -3, "y": 2, "z": 1 },\
                         { "x": 3, "y": 2, "z": 1 },\
                         { "x": 3, "y": -2, "z": 1 },\
                         { "x": -3, "y": -2, "z": 1 }]"""),
                new TestCase(
                        new double[] {
                            0.8840368702,
                            -0.079634703,
                            0.4605834629,
                            -0.3187210838,
                            //
                            -0.014940831,
                            -0.9896906774,
                            -0.1424399333,
                            0.1184466121,
                            //
                            0.4671783212,
                            0.1190406531,
                            -0.8761128575,
                            0.412886626,
                            //
                            0,
                            0,
                            0,
                            1
                        },
                        List.of(
                                new MarkerLocation3d(
                                        new Marker(MarkerType.ONE),
                                        new Vector3D(0.0350068936, 0.03700722, 0.4939999878),
                                        new Vector3D(0.0126514221, 0.0145922087, 0.4799999893),
                                        new Vector3D(0.0578009636, 0.0102748408, 0.5009999871),
                                        new Vector3D(0.0589546743, 0.0610268724, 0.5109999776),
                                        new Vector3D(0.012835922, 0.0653865791, 0.4869999886))),
                        """
                        [{ "x": -0.0631925331, "y": 0.0109325541, "z": 0.0008467105 },\
                         { "x": -0.0876187491, "y": 0.03544465, "z": -0 },\
                         { "x": -0.0376888258, "y": 0.0360516988, "z": 0.0021805766 },\
                         { "x": -0.0361046957, "y": -0.0156187491, "z": -0 },\
                         { "x": -0.0882765551, "y": -0.0158259009, "z": -0 }]"""));
    }

    @ParameterizedTest
    @MethodSource("dataProvider")
    public void test(TestCase testCase) {
        var utils = new Marker3dUtils();
        var tx = new Matrix4d(testCase.tx);
        var markersAfter = utils.transformAll(testCase.markersBefore, tx);
        Assertions.assertEquals(
                testCase.expectedMx,
                markersAfter.stream()
                        .map(MarkerLocation3d::points)
                        .map(Object::toString)
                        .collect(Collectors.joining("\n")));
    }
}
