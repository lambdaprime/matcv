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

import id.matcv.converters.MatConverters;
import id.matcv.types.ndbuffers.matrix.Vector2d;
import id.xfunction.Preconditions;
import id.xfunction.XJsonStringBuilder;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;

/**
 * Points p1, ..., p4 are given in the clockwise order of the marker corners.
 *
 * @author lambdaprime intid@protonmail.com
 */
public record MarkerLocation2d(
        Marker marker,
        Vector2d center,
        Vector2d p1,
        Vector2d p2,
        Vector2d p3,
        Vector2d p4,
        double heightPixels,
        double widthPixels,
        MatOfPoint2f corners,
        Vector2d vector,
        Optional<Path> imageFile) {
    private static final MatConverters converters = new MatConverters();

    public MarkerLocation2d {
        Preconditions.equals(4, corners.size(0));
    }

    MarkerLocation2d(
            Vector2d p1,
            Vector2d p2,
            Vector2d p3,
            Vector2d p4,
            Vector2d center,
            double heightPixels,
            double widthPixels,
            Mat corners,
            Vector2d vector,
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

    /** Height of the marker in pixels */
    public double heightPixels() {
        return heightPixels;
    }

    /** Width of the marker in pixels */
    public double widthPixels() {
        return widthPixels;
    }

    /** Marker corners returned by OpenCV detection algorithm (as-is) */
    public MatOfPoint2f corners() {
        return corners;
    }

    /** Center point (first) + all corners */
    public List<Vector2d> points() {
        return List.of(center, p1, p2, p3, p4);
    }

    @Override
    public String toString() {
        XJsonStringBuilder builder = new XJsonStringBuilder(this);
        builder.append("marker", marker);
        builder.append("center", center);
        builder.append("p1", p1);
        builder.append("p2", p2);
        builder.append("p3", p3);
        builder.append("p4", p4);
        builder.append("heightPixels", heightPixels);
        builder.append("widthPixels", widthPixels);
        builder.append("vector", vector);
        builder.append("imageFile", imageFile);
        return builder.toString();
    }

    /** Concatenates {@link #corners()} + {@link #center()} */
    public MatOfPoint2f copyPointsToMatOfPoint2f() {
        var points = new MatOfPoint2f();
        Core.vconcat(List.of(corners, converters.copyToMat32F(center)), points);
        return points;
    }
}
