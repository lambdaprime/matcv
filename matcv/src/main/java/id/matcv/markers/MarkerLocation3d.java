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

import id.ndbuffers.NdBuffersFactory;
import id.ndbuffers.Slice;
import id.ndbuffers.matrix.MatrixN3d;
import id.ndbuffers.matrix.Vector3d;
import id.xfunction.Preconditions;
import id.xfunction.XJsonStringBuilder;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.opencv.core.MatOfPoint2f;

/**
 * Points p1, ..., p4 are given in the same order as they present in {@link MarkerLocation2d}
 *
 * @author lambdaprime intid@protonmail.com
 */
public class MarkerLocation3d extends AbstractMarkerLocation {
    public static final int NUM_OF_POINTS = 5;

    private MatrixN3d data;

    public MarkerLocation3d(
            Marker marker,
            MatrixN3d data,
            Optional<MatOfPoint2f> corners,
            Optional<Path> imageFile) {
        super(marker, corners, imageFile);
        Preconditions.equals(5, data.shape().dims()[0]);
        this.data = data;
    }

    public MarkerLocation3d(
            Marker marker,
            Vector3d center,
            Vector3d p1,
            Vector3d p2,
            Vector3d p3,
            Vector3d p4,
            Optional<MatOfPoint2f> corners) {
        this(marker, new MatrixN3d(center, p1, p2, p3, p4), corners, Optional.empty());
    }

    public Vector3d center() {
        return data.getVectorView(0);
    }

    public Vector3d p1() {
        return data.getVectorView(1);
    }

    public Vector3d p2() {
        return data.getVectorView(2);
    }

    public Vector3d p3() {
        return data.getVectorView(3);
    }

    public Vector3d p4() {
        return data.getVectorView(4);
    }

    public MatrixN3d getData() {
        return data;
    }

    /** Center point (first) + all corners in the clockwise order of the marker corners */
    public List<Vector3d> points() {
        return List.of(center(), p1(), p2(), p3(), p4());
    }

    @Override
    protected void toString(XJsonStringBuilder builder) {
        builder.append("center", center());
        builder.append("p1", p1());
        builder.append("p2", p2());
        builder.append("p3", p3());
        builder.append("p4", p4());
    }

    public MarkerLocation2d toMarkerLocation2d() {
        var newData =
                new NdBuffersFactory().matrixN2d(new Slice(0, 5, 1), new Slice(0, 2, 1), data);
        return MarkerLocation2d.create(marker(), newData, corners(), imageFile());
    }
}
