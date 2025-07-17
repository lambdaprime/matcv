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
    private ConvertersToOpenCv converters = new ConvertersToOpenCv();
    private ConvertersToNdBuffers ndConverters = new ConvertersToNdBuffers();
    private Mat cameraMat;
    private MatOfDouble distortionMat;

    public CameraPoseEstimator(CameraInfo cameraInfo) {
        cameraMat = converters.toMat64F(cameraInfo.cameraIntrinsics().cameraMatrix());
        distortionMat = converters.toMatOfDouble(cameraInfo.distortionCoefficients());
    }

    public Optional<Matrix4d> estimate(MarkerLocation2d loc) {
        var tvec = new Mat();
        var rvec = new Mat();
        // see
        // https://docs.opencv.org/3.4/d9/d6a/group__aruco.html#ga896ca24f0c1b4b277b6e59d5fe001dd5
        // "markerLength - the length of the markers' side. The returning translation vectors will
        // be in the same unit. Normally, unit is meters."
        // Since we give marker size in millis, we expect the TX to describe transformation in
        // millis too.
        Aruco.estimatePoseSingleMarkers(
                List.of(loc.corners().orElseThrow()),
                Marker.MARKERS_SIZE_IN_MM / 1000.F,
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
