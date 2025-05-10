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
package id.matcv.types.pointcloud;

import id.matcv.converters.PointConverters;
import id.matcv.types.Vector3D;
import id.matcv.types.camera.CameraIntrinsics;
import id.matcv.types.camera.CameraMatrix;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

/**
 * Point cloud accessor which points to the memory segment with depth image and reads point cloud 3d
 * points directly from it
 *
 * <p>This point cloud does not store any 3d points.
 *
 * <p>Supported format of depth image in OpenCV notation is HEIGHT*WIDTH*CV_16UC1 (RealSense
 * RS2_FORMAT_Z16)
 *
 * <p>Array format is: p1.x, p1.y, p1.z, p2.x, p2.y, p2.z, ..., pN.x, pN.y, pN.z
 *
 * @author lambdaprime intid@protonmail.com
 */
public class PointCloudFromMemorySegmentAccessor implements PointCloud {

    private PointConverters converters = new PointConverters();
    private MemorySegment segment;
    private CameraIntrinsics cameraIntrinsics;
    private CameraMatrix cameraMatrix;
    private double depthScale;

    /**
     * @param segment
     * @param cameraIntrinsics
     * @param depthScale The ratio to scale depth values. Example: In RealSense cameras depth unit
     *     is 1 meter so the depthScale in such case can be set to 1000
     */
    public PointCloudFromMemorySegmentAccessor(
            MemorySegment segment, CameraIntrinsics cameraIntrinsics, double depthScale) {
        this.segment = segment;
        this.cameraIntrinsics = cameraIntrinsics;
        this.depthScale = depthScale;
        this.cameraMatrix = cameraIntrinsics.cameraMatrix();
    }

    @Override
    public Vector3D getPoint(int index) {
        var coords =
                converters.toPoint2d(index, cameraIntrinsics.width(), cameraIntrinsics.height());
        var z = segment.get(ValueLayout.JAVA_SHORT_UNALIGNED, index * Short.BYTES) / depthScale;
        var x = (coords.getX() - cameraMatrix.cx()) * z / cameraMatrix.fx();
        var y = (coords.getY() - cameraMatrix.cy()) * z / cameraMatrix.fy();
        return new Vector3D(x, y, z);
    }
}
