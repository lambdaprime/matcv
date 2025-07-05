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
import id.mathcat.LineUtils;
import id.ndbuffers.matrix.MatrixN2d;
import id.ndbuffers.matrix.Vector2d;
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
public class MarkerLocation2d extends AbstractMarkerLocation {
    private static final MatConverters converters = new MatConverters();
    private MatrixN2d data;
    private double heightPixels;
    private double widthPixels;
    private Vector2d vector;

    private MarkerLocation2d(
            Marker marker,
            double heightPixels,
            double widthPixels,
            Vector2d vector,
            MatrixN2d data,
            Optional<MatOfPoint2f> corners,
            Optional<Path> imageFile) {
        super(marker, corners, imageFile);
        Preconditions.equals(5, data.shape().dims()[0]);
        this.data = data;
        this.heightPixels = heightPixels;
        this.widthPixels = widthPixels;
        this.vector = vector;
    }

    public static MarkerLocation2d create(Marker marker, Mat corners) {
        System.out.println(corners.toString());
        double[] buf;
        buf = corners.get(0, 0);
        var p1 = new Vector2d(buf[0], buf[1]);
        buf = corners.get(0, 1);
        var p2 = new Vector2d(buf[0], buf[1]);
        buf = corners.get(0, 2);
        var p3 = new Vector2d(buf[0], buf[1]);
        buf = corners.get(0, 3);
        var p4 = new Vector2d(buf[0], buf[1]);
        var center = LineUtils.midPoint(LineUtils.midPoint(p1, p2), LineUtils.midPoint(p3, p4));
        return new MarkerLocation2d(
                marker,
                p1.distance(p2),
                p2.distance(p3),
                LineUtils.createVector(center, LineUtils.midPoint(p1, p2)),
                new MatrixN2d(center, p1, p2, p3, p4),
                Optional.of(new MatOfPoint2f(corners.reshape(2, 4))),
                Optional.empty());
    }

    public static MarkerLocation2d create(
            Marker marker,
            MatrixN2d data,
            Optional<MatOfPoint2f> corners,
            Optional<Path> imageFile) {
        Preconditions.equals(5, data.shape().dims()[0]);
        var center = data.getVectorView(0);
        var p1 = data.getVectorView(1);
        var p2 = data.getVectorView(2);
        var p3 = data.getVectorView(3);
        return new MarkerLocation2d(
                marker,
                p1.distance(p2),
                p2.distance(p3),
                LineUtils.createVector(center, LineUtils.midPoint(p1, p2)),
                data,
                corners,
                imageFile);
    }

    public Vector2d vector() {
        return vector;
    }

    /** Height of the marker in pixels */
    public double heightPixels() {
        return heightPixels;
    }

    /** Width of the marker in pixels */
    public double widthPixels() {
        return widthPixels;
    }

    public Vector2d center() {
        return data.getVectorView(0);
    }

    public Vector2d p1() {
        return data.getVectorView(1);
    }

    public Vector2d p2() {
        return data.getVectorView(2);
    }

    public Vector2d p3() {
        return data.getVectorView(3);
    }

    public Vector2d p4() {
        return data.getVectorView(4);
    }

    public MatrixN2d getData() {
        return data;
    }

    /** Center point (first) + all corners in the clockwise order of the marker corners */
    public List<Vector2d> points() {
        return List.of(center(), p1(), p2(), p3(), p4());
    }

    @Override
    protected void toString(XJsonStringBuilder builder) {
        builder.append("center", center());
        builder.append("p1", p1());
        builder.append("p2", p2());
        builder.append("p3", p3());
        builder.append("p4", p4());
        builder.append("heightPixels", heightPixels);
        builder.append("widthPixels", widthPixels);
        builder.append("vector", vector);
    }

    /** Concatenates {@link #corners()} + {@link #center()} */
    public MatOfPoint2f copyPointsToMatOfPoint2f() {
        var points = new MatOfPoint2f();
        Core.vconcat(List.of(corners().orElseThrow(), converters.copyToMat32F(center())), points);
        return points;
    }
}
