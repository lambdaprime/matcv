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
package id.matcv.types.camera;

import id.ndbuffers.matrix.MatrixNd;

/**
 * Camera information generated by <a
 * href="http://wiki.ros.org/camera_calibration_parsers#YAML">camera_calibration_parsers</a>
 *
 * @author lambdaprime intid@protonmail.com
 */
public record CameraInfo(
        CameraIntrinsics cameraIntrinsics,
        MatrixNd distortionCoefficients,
        MatrixNd rectificationMatrix,
        MatrixNd projectionMatrix) {

    public static final MatrixNd DEFAULT_DISTORTION =
            new MatrixNd(1, 5, new double[] {0, 0, 0, 0, 0});
    public static final MatrixNd DEFAULT_RECTIFICATION =
            new MatrixNd(
                    3,
                    3,
                    new double[] {
                        1, 0, 0,
                        0, 1, 0,
                        0, 0, 1
                    });

    public CameraInfo(CameraIntrinsics cameraIntrinsics) {
        this(cameraIntrinsics, DEFAULT_DISTORTION, DEFAULT_RECTIFICATION, null);
    }
}
