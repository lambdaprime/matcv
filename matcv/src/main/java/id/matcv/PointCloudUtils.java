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
package id.matcv;

import id.matcv.types.camera.CameraIntrinsics;
import id.matcv.types.pointcloud.PointCloud;
import id.matcv.types.pointcloud.PointCloudFromMemorySegmentAccessor;
import id.xfunction.Preconditions;
import java.lang.foreign.MemorySegment;
import java.nio.file.Path;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class PointCloudUtils {
    private static final MatUtils utils = new MatUtils();

    /**
     * @param depthImg depth image file in {@link CvType#CV_16UC1} format
     */
    public PointCloud readPointCloud(Path depthImg, CameraIntrinsics intrinsics) {
        Mat depth = Imgcodecs.imread(depthImg.toString(), Imgcodecs.IMREAD_ANYDEPTH);
        utils.debugShape("depth", depth);
        Preconditions.isTrue(depth.type() == CvType.CV_16UC1, "Depth image type is not CV_16UC1");
        var segment =
                MemorySegment.ofAddress(depth.dataAddr()).reinterpret(depth.total() * Short.BYTES);
        return new PointCloudFromMemorySegmentAccessor(segment, intrinsics, 1000);
    }
}
