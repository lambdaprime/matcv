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
package id.matcv.types.camera;

import id.ndbuffers.matrix.Matrix3d;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class CameraMatrix extends Matrix3d {

    public CameraMatrix(double[] data) {
        super(data);
    }

    public double cx() {
        return get(0, 2);
    }

    public double cy() {
        return get(1, 2);
    }

    public double fx() {
        return get(0, 0);
    }

    public double fy() {
        return get(1, 1);
    }
}
