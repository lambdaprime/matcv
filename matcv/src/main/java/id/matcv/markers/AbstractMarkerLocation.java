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
package id.matcv.markers;

import id.xfunction.Preconditions;
import id.xfunction.XJsonStringBuilder;
import java.nio.file.Path;
import java.util.Optional;
import org.opencv.core.MatOfPoint2f;

/**
 * @author lambdaprime intid@protonmail.com
 */
abstract class AbstractMarkerLocation {
    private Marker marker;
    private Optional<MatOfPoint2f> corners;
    private Optional<Path> imageFile;

    protected AbstractMarkerLocation(
            Marker marker, Optional<MatOfPoint2f> corners, Optional<Path> imageFile) {
        corners.ifPresent(mat -> Preconditions.equals(4, mat.size(0)));
        this.marker = marker;
        this.corners = corners;
        this.imageFile = imageFile;
    }

    public Marker marker() {
        return marker;
    }

    /** Marker corners returned by OpenCV detection algorithm (as-is) */
    public Optional<MatOfPoint2f> corners() {
        return corners;
    }

    public Optional<Path> imageFile() {
        return imageFile;
    }

    protected abstract void toString(XJsonStringBuilder builder);

    @Override
    public String toString() {
        XJsonStringBuilder builder = new XJsonStringBuilder(this);
        builder.append("marker", marker());
        builder.append("imageFile", imageFile);
        toString(builder);
        return builder.toString();
    }
}
