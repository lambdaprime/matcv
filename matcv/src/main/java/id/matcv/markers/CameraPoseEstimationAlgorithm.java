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
package id.matcv.markers;

import org.opencv.aruco.Aruco;
import org.opencv.calib3d.Calib3d;

public interface CameraPoseEstimationAlgorithm {

    /**
     * Use {@link Aruco#estimatePoseSingleMarkers(java.util.List, float, org.opencv.core.Mat, org.opencv.core.Mat, org.opencv.core.Mat, org.opencv.core.Mat)
     *
     * <p>This algorithm is similar to #SOLVE_PNP_P3P except it is designed for Aruco markers.
     *
     * <p>NOTE: In latest versions of OpenCV estimatePoseSingleMarkers is replaced with solvePnP
     */
    record EstimatePoseSingleMarker() implements CameraPoseEstimationAlgorithm {}

    /**
     * Use {@link Calib3d#solvePnP(org.opencv.core.MatOfPoint3f, org.opencv.core.MatOfPoint2f,
     * org.opencv.core.Mat, org.opencv.core.MatOfDouble, org.opencv.core.Mat, org.opencv.core.Mat)}
     */
    record SolvePnpP3P() implements CameraPoseEstimationAlgorithm {}

    /**
     * Use {@link Calib3d#solvePnPRefineVVS(org.opencv.core.Mat, org.opencv.core.Mat,
     * org.opencv.core.Mat, org.opencv.core.Mat, org.opencv.core.Mat, org.opencv.core.Mat,
     * org.opencv.core.TermCriteria)}
     */
    record SolvePnpRefineVvs(int iterations, double epsilon)
            implements CameraPoseEstimationAlgorithm {}
}
