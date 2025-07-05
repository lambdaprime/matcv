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
import id.matcv.converters.MatConverters;
import id.matcv.converters.NdBufferConverters;
import id.matcv.types.camera.CameraInfo;
import id.ndbuffers.NdBuffersFactory;
import id.ndbuffers.matrix.Matrix4d;
import id.xfunction.Preconditions;
import java.util.List;
import java.util.Optional;
import org.opencv.aruco.Aruco;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.Size;

/**
 * Calculates translation and rotation vectors which can later be used to build matrix to transform
 * world coordinates to camera coordinates.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class CameraPoseEstimator {

    private NdBuffersFactory ndFactory = new NdBuffersFactory();
    private MatUtils matUtils = new MatUtils();
    private MatConverters converters = new MatConverters();
    private NdBufferConverters ndConverters = new NdBufferConverters();
    private Mat cameraMat;
    private MatOfDouble distortionMat;

    public CameraPoseEstimator(CameraInfo cameraInfo) {
        cameraMat = converters.toMat64F(cameraInfo.cameraMatrix());
        distortionMat = converters.toMatOfDouble(cameraInfo.distortionCoefficients());
    }

    public void estimate(Mat image, List<MarkerLocation2d> markers) {
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
            var points2d = markerLocation.corners().orElseThrow();
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

    public Optional<Matrix4d> estimate(MarkerLocation2d marker) {
        var markerLenInCm = MarkerType.getMarkerLengthInCm();
        var tvec = new Mat();
        var rvec = new Mat();
        Aruco.estimatePoseSingleMarkers(
                List.of(marker.corners().orElseThrow()),
                markerLenInCm,
                cameraMat,
                distortionMat,
                rvec,
                tvec);
        var rmx = new Mat();
        Calib3d.Rodrigues(rvec, rmx);
        Preconditions.equals(new Size(3, 3), rmx.size());
        var tx =
                ndFactory.matrix4d(
                        new double[] {
                            0, 0, 0, 0,
                            0, 0, 0, 0,
                            0, 0, 0, 0,
                            0, 0, 0, 1,
                        });
        matUtils.debugMat("rmx", rmx);
        ndConverters.mapToMatrix3d(rmx).copyTo(tx, 0, 0);
        matUtils.debugMat("tvec", tvec);
        ndConverters.mapToNdBuffer(tvec, 3, 1).copyTo(tx, 0, 3);
        return Optional.of(tx);
    }
}
