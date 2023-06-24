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
package id.matcv.camera;

import id.matcv.converters.MatConverters;
import java.util.Arrays;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;

/**
 * Camera information generated by <a
 * href="http://wiki.ros.org/camera_calibration_parsers#YAML">camera_calibration_parsers</a>
 *
 * @author lambdaprime intid@protonmail.com
 */
public record CameraInfo(
        Matrix cameraMatrix,
        Matrix distortionCoefficients,
        Matrix rectificationMatrix,
        Matrix projectionMatrix) {

    public record Matrix(int rows, int cols, double[] data) {

        private static final MatConverters converters = new MatConverters();

        @Override
        public String toString() {
            return String.format(
                    "Matrix [rows=%s, cols=%s, data=%s]", rows, cols, Arrays.toString(data));
        }

        public Mat toMat64F() {
            return converters.copyToMat64F(data, cols, rows);
        }

        public Mat toMat32F() {
            return converters.copyToMat32F(data, cols, rows);
        }

        /**
         * Return {@link MatOfDouble} of type {@link CvType#CV_64FC1} which always 1 dimensional
         * (see <a
         * href="https://github.com/vRallev/OpenCV/blob/60fe17cfd6519390b37888b0db28e6a40ff2eca6/opencv-library/opencv-android/src/main/java/org/opencv/core/MatOfDouble.java">code</a>
         */
        public MatOfDouble toMatOfDouble() {
            return new MatOfDouble(data);
        }
    }
}
