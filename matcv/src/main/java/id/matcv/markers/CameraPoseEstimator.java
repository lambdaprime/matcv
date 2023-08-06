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
package id.matcv.markers;

import id.matcv.MatUtils;
import id.matcv.camera.CameraInfo;
import id.xfunction.Preconditions;
import java.util.List;
import org.opencv.aruco.Aruco;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;

/**
 * Calculates translation and rotation vectors which can later be used to build matrix to transform
 * world coordinates to camera coordinates.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class CameraPoseEstimator {

    private static final MatUtils matUtils = new MatUtils();
    private Mat cameraMat;
    private MatOfDouble distortionMat;

    public CameraPoseEstimator(CameraInfo cameraInfo) {
        cameraMat = cameraInfo.cameraMatrix().toMat64F();
        distortionMat = cameraInfo.distortionCoefficients().toMatOfDouble();
    }

    public void estimate(Mat image, List<MarkerLocation> markers) {
        matUtils.debugMat("cameraMat", cameraMat);
        matUtils.debugMat("distortionMat", distortionMat);
        Preconditions.equals(
                MarkerType.getDict(),
                Aruco.DICT_7X7_50,
                "SOLVEPNP_IPPE_SQUARE requires square markers. Currently supported are"
                        + " DICT_7X7_50.");
        for (int i = 0; i < markers.size(); i++) {
            var rvec = new Mat();
            var tvec = new Mat();
            var markerLocation = markers.get(i);
            var marker = markerLocation.marker();
            var points3d = marker.create3dModel();
            var points2d = markerLocation.corners();
            matUtils.debugMat("points3d", points3d);
            matUtils.debugMat("points2d", points2d);
            Calib3d.solvePnP(
                    points3d,
                    points2d,
                    cameraMat,
                    distortionMat,
                    rvec,
                    tvec,
                    false,
                    Calib3d.SOLVEPNP_IPPE_SQUARE);
            matUtils.debugMat("tvec " + marker, tvec);
            matUtils.debugMat("rvec " + marker, rvec);

            Calib3d.projectPoints(points3d, rvec, tvec, cameraMat, distortionMat, points2d);
            matUtils.debugMat("points2d projected", points2d);
        }
    }
}
