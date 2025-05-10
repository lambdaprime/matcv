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

import id.matcv.markers.MarkerDetector3d;
import id.matcv.markers.MarkerLocation3d;
import id.matcv.types.camera.CameraIntrinsics;
import id.matcv.types.datatables.DataTable2;
import id.matcv.types.pointcloud.PointCloudFromMemorySegmentAccessor;
import java.lang.foreign.MemorySegment;
import java.util.List;
import java.util.function.Consumer;

public class RgbdToMarker3dTransformer implements Consumer<RgbdImage> {
    private CameraIntrinsics intrinsics;
    private Consumer<List<MarkerLocation3d>> consumer;

    public RgbdToMarker3dTransformer(
            CameraIntrinsics intrinsics, Consumer<List<MarkerLocation3d>> consumer) {
        this.intrinsics = intrinsics;
        this.consumer = consumer;
    }

    @Override
    public void accept(RgbdImage rgbd) {
        var depthMat = rgbd.depthMat();
        var segment =
                MemorySegment.ofAddress(depthMat.dataAddr())
                        .reinterpret(depthMat.total() * Short.BYTES);
        var pc = new PointCloudFromMemorySegmentAccessor(segment, intrinsics, 1000.);
        var markers =
                new MarkerDetector3d(intrinsics)
                        .detect(new DataTable2<>(List.of(rgbd.colorMat()), List.of(pc)))
                        .col2()
                        .get(0);
        if (markers.isEmpty()) return;
        consumer.accept(markers);
    }
}
