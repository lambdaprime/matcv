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
package id.matcv.markers;

import java.nio.file.Path;
import java.util.Optional;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;

public record MarkerLocation(
        Marker marker,
        Vector2D p1,
        Vector2D p2,
        Vector2D p3,
        Vector2D p4,
        Vector2D center,
        double heightPixels,
        double widthPixels,
        MatOfPoint2f corners,
        Vector2D vector,
        Optional<Path> imageFile) {

    MarkerLocation(
            Vector2D p1,
            Vector2D p2,
            Vector2D p3,
            Vector2D p4,
            Vector2D center,
            double heightPixels,
            double widthPixels,
            Mat corners,
            Vector2D vector,
            Marker marker) {
        this(
                marker,
                p1,
                p2,
                p3,
                p4,
                center,
                heightPixels,
                widthPixels,
                new MatOfPoint2f(corners.reshape(2, 4)),
                vector,
                Optional.empty());
    }
}
