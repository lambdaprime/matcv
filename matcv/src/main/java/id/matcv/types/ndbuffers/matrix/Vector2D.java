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
package id.matcv.types.ndbuffers.matrix;

import id.xfunction.XJsonStringBuilder;
import java.nio.DoubleBuffer;

public class Vector2D extends MatrixNd {

    public Vector2D(double x, double y) {
        this(DoubleBuffer.wrap(new double[] {x, y}));
    }

    public Vector2D(DoubleBuffer data) {
        super(1, 2, data);
    }

    public double getX() {
        return get(0, 0);
    }

    public double getY() {
        return get(0, 1);
    }

    public Vector2D subtract(Vector2D other) {
        return new Vector2D(getX() - other.getX(), getY() - other.getY());
    }

    public double distance(Vector2D other) {
        return Math.sqrt(Math.pow(getX() - other.getX(), 2) + Math.pow(getY() - other.getY(), 2));
    }

    public Vector2D add(Vector2D other) {
        return new Vector2D(other.getX() + getX(), other.getY() + getY());
    }

    @Override
    public String toString() {
        XJsonStringBuilder builder = new XJsonStringBuilder();
        builder.append("x", getX());
        builder.append("y", getY());
        return builder.toString();
    }
}
