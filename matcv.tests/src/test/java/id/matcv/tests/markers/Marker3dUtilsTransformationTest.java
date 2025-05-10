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
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class Marker3dUtilsTransformationTest {

    record TestCase(MarkerLocation3d from, MarkerLocation3d to, Matrix4d tx) {}

    static Stream<TestCase> dataProvider() {
        return Stream.of(
                new TestCase(
                        new MarkerLocation3d(
                                new Marker(MarkerType.ONE),
                                new Vector3D(0, 0, 0),
                                new Vector3D(2, 3, 1),
                                new Vector3D(2, -3, 1),
                                new Vector3D(-2, -3, 1),
                                new Vector3D(-2, 3, 1)),
                        new MarkerLocation3d(
                                new Marker(MarkerType.ONE),
                                new Vector3D(0, 0, 0),
                                new Vector3D(6, 2, 1),
                                new Vector3D(6, -4, 1),
                                new Vector3D(2, -4, 1),
                                new Vector3D(2, 2, 1)),
                        new Matrix4d(
                                new double[] {
                                    1, 0, 4, 0,
                                    0, 1, -1, 0,
                                    0, 0, 1, 0,
                                    0, 0, 0, 1
                                })),
                // rotate 90 degrees ccw around center of coordinate system (0, 0)
                new TestCase(
                        new MarkerLocation3d(
                                new Marker(MarkerType.ONE),
                                new Vector3D(0, 0, 0),
                                new Vector3D(2, 3, 1),
                                new Vector3D(2, -3, 1),
                                new Vector3D(-2, -3, 1),
                                new Vector3D(-2, 3, 1)),
                        new MarkerLocation3d(
                                new Marker(MarkerType.ONE),
                                new Vector3D(0, 0, 0),
                                new Vector3D(-3, 2, 1),
                                new Vector3D(3, 2, 1),
                                new Vector3D(3, -2, 1),
                                new Vector3D(-3, -2, 1)),
                        new Matrix4d(
                                new double[] {
                                    0, -1, 0, 0,
                                    1, 0, 0, 0,
                                    0, 0, 1, 0,
                                    0, 0, 0, 1
                                })),
                new TestCase(
                        new MarkerLocation3d(
                                new Marker(MarkerType.ONE),
                                new Vector3D(0.0350068936, 0.03700722, 0.4939999878),
                                new Vector3D(0.0126514221, 0.0145922087, 0.4799999893),
                                new Vector3D(0.0578009636, 0.0102748408, 0.5009999871),
                                new Vector3D(0.0589546743, 0.0610268724, 0.5109999776),
                                new Vector3D(0.012835922, 0.065386579, 0.4869999886)),
                        new MarkerLocation3d(
                                new Marker(MarkerType.ONE),
                                new Vector3D(-0.06301, 0.01087, 0.00061),
                                new Vector3D(-0.08809, 0.03562, 0.00062),
                                new Vector3D(-0.0373, 0.03591, 0.00168),
                                new Vector3D(-0.03657, -0.01544, 0.00061),
                                new Vector3D(-0.0879, -0.01596, -0.00049)),
                        new Matrix4d(
                                new double[] {
                                    1.22858, 0.02926, -0.2168, 0, -0.14298, -1.03016, 0.1093, 0,
                                    0.02084, -0.02202, 0.00141, 0, 0, 0, 0, 1
                                })));
    }

    private static String round(String str) {
        return str.replaceAll("(\\.\\d)\\d+,", "$1,")
                .replaceAll("-0.0", "0")
                .replaceAll("0.0", "0");
    }

    @ParameterizedTest
    @MethodSource("dataProvider")
    public void test_calculateTransformationMatrix(TestCase testCase) {
        var utils = new Marker3dUtils();
        var tx = utils.calculateTransformationMatrix(testCase.from, testCase.to);
        Assertions.assertEquals(round(testCase.tx.toString()), round(tx.toString()));
    }

    @ParameterizedTest
    @MethodSource("dataProvider")
    public void test_transformAll(TestCase testCase) {
        var utils = new Marker3dUtils();
        var actual = utils.transformAll(List.of(testCase.from), testCase.tx);
        Assertions.assertEquals(List.of(testCase.to).toString(), actual.toString());
    }
}
