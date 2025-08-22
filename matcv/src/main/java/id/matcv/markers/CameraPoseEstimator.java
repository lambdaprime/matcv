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
import id.matcv.converters.ConvertersToNdBuffers;
import id.matcv.converters.ConvertersToOpenCv;
import id.matcv.markers.CameraPoseEstimationAlgorithm.SolvePnpP3P;
import id.matcv.markers.CameraPoseEstimationAlgorithm.SolvePnpRefineVvs;
import id.matcv.types.camera.CameraInfo;
import id.ndbuffers.NdBuffersFactory;
import id.ndbuffers.matrix.Matrix4d;
import id.xfunction.Preconditions;
import java.util.List;
import java.util.Optional;
import org.opencv.aruco.Aruco;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.video.KalmanFilter;

/**
 * Calculates translation and rotation vectors which can later be used to build matrix to transform
 * world coordinates to camera coordinates.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class CameraPoseEstimator {

    /** sizeof(rvec) + sizeof(tvec) = 3 + 3 = 6 */
    private static final int KALMAN_STATE_SIZE = 6;

    private NdBuffersFactory ndFactory = new NdBuffersFactory();
    private MatUtils matUtils = new MatUtils();
    private ConvertersToOpenCv openCvConverters = new ConvertersToOpenCv();
    private ConvertersToNdBuffers ndConverters = new ConvertersToNdBuffers();
    private CameraPoseEstimationAlgorithm algorithm =
            new CameraPoseEstimationAlgorithm.EstimatePoseSingleMarker();
    private Optional<KalmanFilter> kalmanFilter = Optional.empty();
    private Mat cameraMat;
    private Mat tvec;
    private Mat rvec;
    private MatOfDouble distortionMat;

    public CameraPoseEstimator(CameraInfo cameraInfo) {
        cameraMat = openCvConverters.toMat64F(cameraInfo.cameraIntrinsics().cameraMatrix());
        distortionMat = openCvConverters.toMatOfDouble(cameraInfo.distortionCoefficients());
    }

    public CameraPoseEstimator withAlgorithm(CameraPoseEstimationAlgorithm algorithm) {
        this.algorithm = algorithm;
        return this;
    }

    public CameraPoseEstimator withKalmanFilter(boolean isEnabled) {
        if (isEnabled) {
            var kf = new KalmanFilter(KALMAN_STATE_SIZE, 6);
            kf.set_transitionMatrix(
                    openCvConverters.copyToMat32FC1(
                            new double[] {
                                1, 0, 0, 0, 0, 0,
                                0, 1, 0, 0, 0, 0,
                                0, 0, 1, 0, 0, 0,
                                0, 0, 0, 1, 0, 0,
                                0, 0, 0, 0, 1, 0,
                                0, 0, 0, 0, 0, 1
                            },
                            6,
                            6));
            matUtils.debugMat("get_transitionMatrix", kf.get_transitionMatrix());
            kf.set_measurementMatrix(
                    openCvConverters.copyToMat32FC1(
                            new double[] {
                                1, 0, 0, 0, 0, 0,
                                0, 1, 0, 0, 0, 0,
                                0, 0, 1, 0, 0, 0,
                                0, 0, 0, 1, 0, 0,
                                0, 0, 0, 0, 1, 0,
                                0, 0, 0, 0, 0, 1
                            },
                            6,
                            6));
            matUtils.debugMat("get_measurementMatrix", kf.get_measurementMatrix());
            kalmanFilter = Optional.of(kf);
        } else {
            kalmanFilter = Optional.empty();
        }
        return this;
    }

    public Optional<Matrix4d> estimate(MarkerLocation2d loc) {
        estimate(loc, algorithm);
        matUtils.debugMat("rvec", rvec);
        matUtils.debugMat("tvec", tvec);

        kalmanFilter.ifPresent(kf -> runKalmanFilter(kf, rvec, tvec));

        var rmx = new Mat();
        Calib3d.Rodrigues(rvec, rmx);
        Preconditions.equals(new Size(3, 3), rmx.size());
        matUtils.debugMat("rmx", rmx);
        var tx =
                ndFactory.matrix4d(
                        new double[] {
                            0, 0, 0, 0,
                            0, 0, 0, 0,
                            0, 0, 0, 0,
                            0, 0, 0, 1,
                        });
        ndConverters.mapToMatrix3d(rmx).copyTo(tx, 0, 0);
        ndConverters.mapToNdBuffer(tvec, 3, 1).copyTo(tx, 0, 3);
        return Optional.of(tx);
    }

    private void estimate(MarkerLocation2d loc, CameraPoseEstimationAlgorithm algorithm) {
        var corners = loc.corners().orElseThrow();
        // see
        // https://docs.opencv.org/3.4/d9/d6a/group__aruco.html#ga896ca24f0c1b4b277b6e59d5fe001dd5
        // "markerLength - the length of the markers' side. The returning translation vectors will
        // be in the same unit. Normally, unit is meters."
        // Since we give marker size in millis, we expect the TX to describe transformation in
        // millis too.
        switch (algorithm) {
            case SolvePnpP3P alg:
                {
                    rvec = new Mat();
                    tvec = new Mat();
                    // convert meters to millis
                    Calib3d.solvePnP(
                            openCvConverters.copyToMatOfPoint32F(loc.marker().create3dModel(0.001)),
                            corners,
                            cameraMat,
                            distortionMat,
                            rvec,
                            tvec,
                            false,
                            Calib3d.SOLVEPNP_P3P);
                    break;
                }
            case SolvePnpRefineVvs alg:
                {
                    if (rvec == null || tvec == null) {
                        rvec = Mat.zeros(3, 1, CvType.CV_64FC1);
                        tvec = Mat.zeros(3, 1, CvType.CV_64FC1);
                        estimate(loc, new CameraPoseEstimationAlgorithm.SolvePnpP3P());
                    }
                    // convert meters to millis
                    Calib3d.solvePnPRefineVVS(
                            openCvConverters.copyToMatOfPoint32F(loc.marker().create3dModel(0.001)),
                            corners,
                            cameraMat,
                            distortionMat,
                            rvec,
                            tvec,
                            new TermCriteria(
                                    TermCriteria.EPS + TermCriteria.COUNT,
                                    alg.iterations(),
                                    alg.epsilon()));

                    break;
                }
            default:
                {
                    tvec = new Mat();
                    rvec = new Mat();
                    Aruco.estimatePoseSingleMarkers(
                            List.of(corners),
                            // convert meters to millis
                            Marker.MARKERS_SIZE_IN_MM / 1000.F,
                            cameraMat,
                            distortionMat,
                            rvec,
                            tvec);
                    break;
                }
        }
    }

    private void runKalmanFilter(KalmanFilter kalmanFilter, Mat rvec, Mat tvec) {
        if (rvec.type() != CvType.CV_64FC3) {
            rvec = rvec.reshape(3);
        }
        if (tvec.type() != CvType.CV_64FC3) {
            tvec = tvec.reshape(3);
        }
        var mx = matUtils.concat(rvec, tvec);
        matUtils.debugMat("mx", mx);
        kalmanFilter.correct(mx);
        mx = kalmanFilter.predict();
        matUtils.debugMat("kalman(mx)", mx);

        rvec.setTo(new Scalar(mx.get(0, 0)[0], mx.get(1, 0)[0], mx.get(2, 0)[0]));
        matUtils.debugMat("kalman(rvec)", rvec);

        tvec.setTo(new Scalar(mx.get(3, 0)[0], mx.get(4, 0)[0], mx.get(5, 0)[0]));
        matUtils.debugMat("kalman(tvec)", tvec);
    }
}
