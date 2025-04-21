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

public enum CameraInfoPredefined {
    REALSENSE_D435i_1280_720(
            new CameraInfo(
                    CameraIntrinsicsPredefined.REALSENSE_D435i_1280_720.getCameraIntrinsics()
                            .cameraMatrix())),

    REALSENSE_D435i_640_480(
            new CameraInfo(
                    CameraIntrinsicsPredefined.REALSENSE_D435i_640_480.getCameraIntrinsics()
                            .cameraMatrix())),

    /**
     * Default camera in <a href="https://www.blender.org/">Blender</a>
     *
     * <p>Camera focal Length set to 50mm.
     *
     * <p>Center point specified for render resolution 1280px/720px
     */
    BLENDER_DEFAULT_1280_720(
            new CameraInfo(
                    CameraIntrinsicsPredefined.BLENDER_DEFAULT_1280_720
                            .getCameraIntrinsics()
                            .cameraMatrix()));

    private CameraInfo cameraInfo;

    private CameraInfoPredefined(CameraInfo cameraInfo) {
        this.cameraInfo = cameraInfo;
    }

    public CameraInfo getCameraInfo() {
        return cameraInfo;
    }
}
