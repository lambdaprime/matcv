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
package id.matcv.types;

import java.nio.DoubleBuffer;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class Vector3D {

    // use array for simple mapping to eigen or EJML
    private DoubleBuffer data;

    public Vector3D(double x, double y, double z) {
        this(DoubleBuffer.wrap(new double[] {x, y, z}));
    }

    public Vector3D(DoubleBuffer data) {
        this.data = data;
    }

    public DoubleBuffer getData() {
        return data;
    }

    public double getX() {
        return data.get(0);
    }

    public double getY() {
        return data.get(1);
    }

    public double getZ() {
        return data.get(2);
    }

    /** Add value to each element of the vector and return resulting new vector */
    public Vector3D add(int n) {
        return new Vector3D(getX() + n, getY() + n, getZ() + n);
    }

    /** Divide value to each element of the vector and return resulting new vector */
    public Vector3D div(int n) {
        return new Vector3D(getX() / n, getY() / n, getZ() / n);
    }

    public double distance(Vector3D other) {
        return Math.sqrt(
                Math.pow(getX() - other.getX(), 2)
                        + Math.pow(getY() - other.getY(), 2)
                        + Math.pow(getZ() - other.getZ(), 2));
    }

    public boolean isFinite() {
        return Double.isFinite(getX()) && Double.isFinite(getY()) && Double.isFinite(getZ());
    }

    @Override
    public String toString() {
        return """
               { "x": %s, "y": %s, "z": %s }"""
                .formatted(
                        MatrixNd.formatter.format(getX()),
                        MatrixNd.formatter.format(getY()),
                        MatrixNd.formatter.format(getZ()));
    }
}
