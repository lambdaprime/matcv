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
package id.matcv.tests.markers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import id.matcv.markers.CameraPoseEstimationAlgorithm;
import id.matcv.markers.CameraPoseEstimator;
import id.matcv.markers.MarkerDetector2d;
import id.matcv.tests.OpenCvTest;
import id.matcv.types.camera.CameraInfoPredefined;
import id.ndbuffers.NdBuffersJsonUtils;
import id.ndbuffers.matrix.Matrix4d;
import java.util.Objects;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class CameraPoseEstimatorTest extends OpenCvTest {
    private static final NdBuffersJsonUtils jsonUtils = new NdBuffersJsonUtils();

    record TestCase(
            CameraPoseEstimationAlgorithm algorithm,
            boolean hasKalmanFilter,
            int maxConvergeCount,
            String expected) {}

    static Stream<TestCase> dataProvider() {
        return Stream.of(
                new TestCase(
                        new CameraPoseEstimationAlgorithm.EstimatePoseSingleMarker(),
                        false,
                        1,
                        """
                        { "data" : [
                         [0.88749, -0.01146, 0.46068, 0.03665],
                         [-0.08406, -0.98695, 0.13737, 0.03895],
                         [0.45309, -0.16064, -0.87687, 0.50958],
                         [0, 0, 0, 1]
                        ] }"""),
                new TestCase(
                        new CameraPoseEstimationAlgorithm.SolvePnpRefineVvs(1, 0.0000000001),
                        false,
                        1,
                        """
                        { "data" : [
                         [0.88749, -0.01146, 0.46068, 0.03665],
                         [-0.08406, -0.98695, 0.13737, 0.03895],
                         [0.45309, -0.16064, -0.87687, 0.50958],
                         [0, 0, 0, 1]
                        ] }"""),
                new TestCase(
                        new CameraPoseEstimationAlgorithm.SolvePnpP3P(),
                        true,
                        18,
                        """
                        { "data" : [
                         [0.88749, -0.01146, 0.46068, 0.03665],
                         [-0.08406, -0.98695, 0.13737, 0.03895],
                         [0.45309, -0.16064, -0.87687, 0.50958],
                         [0, 0, 0, 1]
                        ] }"""),
                new TestCase(
                        new CameraPoseEstimationAlgorithm.EstimatePoseSingleMarker(),
                        true,
                        18,
                        """
                        { "data" : [
                         [0.88749, -0.01146, 0.46068, 0.03665],
                         [-0.08406, -0.98695, 0.13737, 0.03895],
                         [0.45309, -0.16064, -0.87687, 0.50958],
                         [0, 0, 0, 1]
                        ] }"""));
    }

    @ParameterizedTest
    @MethodSource("dataProvider")
    public void test(TestCase tc) {
        var img = Imgcodecs.imread("samples/00000000-rgb.png");
        var markers = new MarkerDetector2d().detect(img).markersSortedByType();

        System.out.println(markers);
        var estimator =
                new CameraPoseEstimator(
                                CameraInfoPredefined.REALSENSE_D435i_640_480.getCameraInfo())
                        .withKalmanFilter(tc.hasKalmanFilter);

        Matrix4d tx = null;
        var i = tc.maxConvergeCount;
        while (i > 0) {
            tx = estimator.estimate(markers.get(0)).get();
            if (Objects.equals(tc.expected, jsonUtils.dumpAsJson(tx))) break;
            var actual = jsonUtils.dumpAsJson(tx);
            System.out.println(actual);
            i--;
        }
        if (i == 0) assertEquals(tc.expected, jsonUtils.dumpAsJson(tx));
    }
}
