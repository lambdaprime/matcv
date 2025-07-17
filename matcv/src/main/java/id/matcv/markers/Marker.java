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

import org.opencv.aruco.Aruco;
import org.opencv.aruco.Dictionary;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint3f;

/**
 * @see MarkerType
 */
public record Marker(MarkerType type) implements Comparable<Marker> {
    /** Taken with respect of actual size of the printed markers. */
    public static final int MARKERS_SIZE_IN_MM = 53;

    public Mat createImage() {
        Mat img = new Mat();
        Dictionary dictionary = Aruco.getPredefinedDictionary(MarkerType.getDict());
        Aruco.drawMarker(dictionary, type.getId(), 200, img);
        return img;
    }

    public MarkerType getType() {
        return type;
    }

    public int getSizeInMm() {
        return MARKERS_SIZE_IN_MM;
    }

    public boolean isOrigin() {
        return type == MarkerType.ONE;
    }

    @Override
    public String toString() {
        return type.name();
    }

    /**
     * Returns marker 3D model points.
     *
     * <p>There are 4 points representing all corners in {@link MarkerType} order
     */
    public MatOfPoint3f create3dModel() {
        var ar =
                new float[] {
                    -MARKERS_SIZE_IN_MM / 2.f, MARKERS_SIZE_IN_MM / 2.f, 0,
                    MARKERS_SIZE_IN_MM / 2.f, MARKERS_SIZE_IN_MM / 2.f, 0,
                    MARKERS_SIZE_IN_MM / 2.f, -MARKERS_SIZE_IN_MM / 2.f, 0,
                    -MARKERS_SIZE_IN_MM / 2.f, -MARKERS_SIZE_IN_MM / 2.f, 0
                };
        return new MatOfPoint3f(new MatOfFloat(ar).reshape(3, 4));
    }

    @Override
    public int compareTo(Marker o) {
        if (o == null) return -1;
        return type.compareTo(o.type);
    }
}
