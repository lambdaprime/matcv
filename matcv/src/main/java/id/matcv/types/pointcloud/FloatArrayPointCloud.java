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

import id.ndbuffers.matrix.Vector3d;

/**
 * Point cloud stored inside float array.
 *
 * <p>Array format is: p1.x, p1.y, p1.z, p2.x, p2.y, p2.z, ..., pN.x, pN.y, pN.z
 *
 * @author lambdaprime intid@protonmail.com
 */
public record FloatArrayPointCloud(float[] data) implements PointCloud {

    @Override
    public Vector3d getPoint(int index) {
        var s = index * 3;
        return new Vector3d(s + 0, s + 1, s + 2);
    }

    @Override
    public int size() {
        return data.length / 3;
    }
}
