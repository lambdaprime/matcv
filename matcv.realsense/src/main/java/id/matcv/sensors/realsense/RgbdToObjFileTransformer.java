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
package id.matcv.sensors.realsense;

import id.matcv.PointCloudUtils;
import id.matcv.types.camera.CameraIntrinsics;
import id.matcv.types.pointcloud.PointCloudFromMemorySegmentAccessor;
import java.lang.foreign.MemorySegment;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Accept {@link RgbdImage} and save it to Wavefront OBJ file
 *
 * @author lambdaprime intid@protonmail.com
 */
public class RgbdToObjFileTransformer implements Consumer<RgbdImage> {
    private PointCloudUtils utils = new PointCloudUtils();
    private CameraIntrinsics intrinsics;
    private Path objFile;

    public RgbdToObjFileTransformer(CameraIntrinsics intrinsics, Path objFile) {
        this.intrinsics = intrinsics;
        this.objFile = objFile;
    }

    @Override
    public void accept(RgbdImage rgbd) {
        var depthMat = rgbd.depthMat();
        var segment =
                MemorySegment.ofAddress(depthMat.dataAddr())
                        .reinterpret(depthMat.total() * Short.BYTES);
        var pc = new PointCloudFromMemorySegmentAccessor(segment, intrinsics, 1000.);
        utils.exportToObj(objFile, pc);
    }
}
