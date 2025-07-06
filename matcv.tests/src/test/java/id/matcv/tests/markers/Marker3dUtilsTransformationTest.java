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
import id.ndbuffers.matrix.Matrix4d;
import id.ndbuffers.matrix.Vector3d;
import id.xfunctiontests.XAsserts;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
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
                                new Vector3d(0, 0, 0),
                                new Vector3d(2, 3, 1),
                                new Vector3d(2, -3, 1),
                                new Vector3d(-2, -3, 1),
                                new Vector3d(-2, 3, 1),
                                Optional.empty()),
                        new MarkerLocation3d(
                                new Marker(MarkerType.ONE),
                                new Vector3d(3, -0.7, 0),
                                new Vector3d(4.9, 2, 0.7),
                                new Vector3d(4.9, -3.4, 0.7),
                                new Vector3d(1.3, -3.4, 1.1),
                                new Vector3d(1.3, 2, 1.1),
                                Optional.empty()),
                        new Matrix4d(
                                new double[] {
                                    0.9, 0, 0.1, 3.0, 0, 0.9, 0, -0.7, -0.1, 0, 0.9, 0, 0, 0, 0, 1
                                })),
                // rotate 90 degrees ccw around center of coordinate system (0, 0)
                new TestCase(
                        new MarkerLocation3d(
                                new Marker(MarkerType.ONE),
                                new Vector3d(0, 0, 0),
                                new Vector3d(2, 3, 1),
                                new Vector3d(2, -3, 1),
                                new Vector3d(-2, -3, 1),
                                new Vector3d(-2, 3, 1),
                                Optional.empty()),
                        new MarkerLocation3d(
                                new Marker(MarkerType.ONE),
                                new Vector3d(0, 0, 0),
                                new Vector3d(-3, 2, 1),
                                new Vector3d(3, 2, 1),
                                new Vector3d(3, -2, 1),
                                new Vector3d(-3, -2, 1),
                                Optional.empty()),
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
                                new Vector3d(0.0350068936, 0.03700722, 0.4939999878),
                                new Vector3d(0.0126514221, 0.0145922087, 0.4799999893),
                                new Vector3d(0.0578009636, 0.0102748408, 0.5009999871),
                                new Vector3d(0.0589546743, 0.0610268724, 0.5109999776),
                                new Vector3d(0.012835922, 0.065386579, 0.4869999886),
                                Optional.empty()),
                        new MarkerLocation3d(
                                new Marker(MarkerType.ONE),
                                new Vector3d(-0.062, 0.010, 0),
                                new Vector3d(-0.036, 0.036, 0),
                                new Vector3d(-0.036, -0.016, 0),
                                new Vector3d(-0.088, -0.016, 0),
                                new Vector3d(-0.088, 0.036, 0),
                                Optional.empty()),
                        new Matrix4d(
                                new double[] {
                                    -1.2742E-02,
                                    -9.8622E-01,
                                    -1.6497E-01,
                                    5.8830E-02,
                                    -8.9366E-01,
                                    8.5245E-02,
                                    -4.4057E-01,
                                    2.5978E-01,
                                    4.4856E-01,
                                    1.4182E-01,
                                    -8.8243E-01,
                                    4.2093E-01,
                                    0,
                                    0,
                                    0,
                                    1.000000000000000
                                })));
    }

    @ParameterizedTest
    @MethodSource("dataProvider")
    public void test_calculateTransformationMatrix(TestCase testCase) {
        var utils = new Marker3dUtils();
        var tx = utils.calculateTransformationMatrix(testCase.from, testCase.to);
        XAsserts.assertSimilar(testCase.tx.duplicate().array(), tx.duplicate().array(), 0.1);
    }

    @ParameterizedTest
    @MethodSource("dataProvider")
    public void test_transformAll(TestCase testCase) {
        var utils = new Marker3dUtils();
        var actual = utils.transformAll(List.of(testCase.from), testCase.tx);
        System.out.println(actual);
        XAsserts.assertSimilar(
                testCase.to.getData().duplicate().array(),
                actual.get(0).getData().duplicate().array(),
                0.01);
    }
}
