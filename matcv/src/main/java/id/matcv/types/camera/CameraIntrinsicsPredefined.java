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

public enum CameraIntrinsicsPredefined {
    REALSENSE_D435i_1280_720(
            new DefaultCameraIntrinsics(
                    1280,
                    720,
                    new CameraMatrix(
                            new double[] {
                                909.96856689453125, 0.0, 635.51580810546875,
                                0.0, 909.84716796875, 353.34024047851562,
                                0.0, 0.0, 1.0
                            }))),

    REALSENSE_D435i_640_480(
            new DefaultCameraIntrinsics(
                    640,
                    480,
                    new CameraMatrix(
                            new double[] {
                                606.6457519531,
                                0,
                                317.010559082,
                                0,
                                606.5648193359,
                                235.5601654053,
                                0,
                                0,
                                1
                            }))),

    /**
     * Default camera in <a href="https://www.blender.org/">Blender</a>
     *
     * <p>Camera focal Length set to 50mm.
     *
     * <p>Center point specified for render resolution 1280px/720px
     */
    BLENDER_DEFAULT_1280_720(
            new DefaultCameraIntrinsics(
                    1280,
                    720,
                    new CameraMatrix(
                            new double[] {
                                50.0, 0.0, 640.0,
                                0.0, 50.0, 360.0,
                                0.0, 0.0, 1.0
                            })));

    private CameraIntrinsics cameraIntrinsics;

    private CameraIntrinsicsPredefined(CameraIntrinsics cameraIntrinsics) {
        this.cameraIntrinsics = cameraIntrinsics;
    }

    public CameraIntrinsics getCameraIntrinsics() {
        return cameraIntrinsics;
    }
}
