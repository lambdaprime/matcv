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
package id.matcv.tests.impl.ejml;

import static org.junit.jupiter.api.Assertions.assertEquals;

import id.matcv.impl.ejml.KabschAlgorithm;
import java.io.IOException;
import java.util.stream.Stream;
import org.ejml.data.DMatrixRMaj;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class KabschAlgorithmTest {

    record TestCase(DMatrixRMaj from, DMatrixRMaj to, DMatrixRMaj tx) {}

    static Stream<TestCase> dataProvider() {
        return Stream.of(
                new TestCase(
                        DMatrixRMaj.wrap(
                                5,
                                3,
                                new double[] {
                                    0.0350068936, 0.03700722, 0.4939999878,
                                    0.0126514221, 0.0145922087, 0.4799999893,
                                    0.0578009636, 0.0102748408, 0.5009999871,
                                    0.0589546743, 0.0610268724, 0.5109999776,
                                    0.012835922, 0.065386579, 0.4869999886
                                }),
                        DMatrixRMaj.wrap(
                                5,
                                3,
                                new double[] {
                                    -0.062, 0.010, 0,
                                    -0.036, 0.036, 0,
                                    -0.036, -0.016, 0,
                                    -0.088, -0.016, 0,
                                    -0.088, 0.036, 0
                                }),
                        DMatrixRMaj.wrap(
                                4,
                                4,
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
    public void test(TestCase tc) throws IOException {
        assertEquals(
                tc.tx.toString(),
                new KabschAlgorithm().calculateTransformation(tc.from, tc.to).toString());
    }
}
