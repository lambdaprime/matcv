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

import id.matcv.types.ndbuffers.matrix.Vector3d;

/**
 * @author lambdaprime intid@protonmail.com
 */
public interface PointCloud {

    /**
     * Depth cameras may produce so called "holes" when data not being available for a particular
     * pixel.
     *
     * <p>The "hole" in such cases is associated with depth value equal to 0.
     */
    Vector3d HOLE = new Vector3d(0, 0, 0);

    Vector3d getPoint(int index);

    /** Total number of points */
    int size();
}
