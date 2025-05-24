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

import id.matcv.types.MatrixN3d;
import id.matcv.types.Vector3D;
import id.xfunction.XJsonStringBuilder;
import java.util.List;

/**
 * Points p1, ..., p4 are given in the same order as they present in {@link MarkerLocation2d}
 *
 * @author lambdaprime intid@protonmail.com
 */
public class MarkerLocation3d {
    public static final int NUM_OF_POINTS = 5;

    private Marker marker;
    private MatrixN3d data;

    public MarkerLocation3d(Marker marker, MatrixN3d data) {
        this.marker = marker;
        this.data = data;
    }

    public MarkerLocation3d(
            Marker marker, Vector3D center, Vector3D p1, Vector3D p2, Vector3D p3, Vector3D p4) {
        this.marker = marker;
        this.data = new MatrixN3d(center, p1, p2, p3, p4);
    }

    public MarkerLocation3d(Marker marker, List<Vector3D> points) {
        this(marker, points.get(0), points.get(1), points.get(2), points.get(3), points.get(4));
    }

    public Marker marker() {
        return marker;
    }

    public Vector3D center() {
        return data.getVector(0);
    }

    public Vector3D p1() {
        return data.getVector(1);
    }

    public Vector3D p2() {
        return data.getVector(2);
    }

    public Vector3D p3() {
        return data.getVector(3);
    }

    public Vector3D p4() {
        return data.getVector(4);
    }

    public MatrixN3d getData() {
        return data;
    }

    /** Center point (first) + all corners */
    public List<Vector3D> points() {
        return List.of(center(), p1(), p2(), p3(), p4());
    }

    @Override
    public String toString() {
        XJsonStringBuilder builder = new XJsonStringBuilder(this);
        builder.append("marker", marker);
        builder.append("center", center());
        builder.append("p1", p1());
        builder.append("p2", p2());
        builder.append("p3", p3());
        builder.append("p4", p4());
        return builder.toString();
    }
}
