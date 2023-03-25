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
package id.mathcalc;

import id.xfunction.XJson;
import java.util.Objects;
import java.util.Optional;

/**
 * Vector which contains three doubles.
 *
 * <p>Vector itself is immutable all public fields are final.
 *
 * <p>All operations are immutable and return new copy of the vector.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class Vector3d {

    private Optional<String> cachedToString = Optional.empty();
    public final double n1, n2, n3;

    public Vector3d() {
        this.n1 = 0;
        this.n2 = 0;
        this.n3 = 0;
    }

    public Vector3d(Vector3d vec) {
        this.n1 = vec.n1;
        this.n2 = vec.n2;
        this.n3 = vec.n3;
    }

    public Vector3d(double n1, double n2, double n3) {
        this.n1 = n1;
        this.n2 = n2;
        this.n3 = n3;
    }

    /** L2 norm, equals to the square root of the dot product of vector with itself. */
    public double norm() {
        return Math.sqrt(n1 * n1 + n2 * n2 + n3 * n3);
    }

    /** Normalizes the vector, i.e. divides it by its own norm */
    public Vector3d normalize() {
        var n = norm();
        return new Vector3d(n1 / n, n2 / n, n3 / n);
    }

    /** Multiply vector with the scalar */
    public Vector3d mul(double scalar) {
        return new Vector3d(n1 * scalar, n2 * scalar, n3 * scalar);
    }

    /** Multiply vector with the scalar */
    public Vector3d sub(Vector3d vector) {
        return new Vector3d(n1 - vector.n1, n2 * vector.n2, n3 * vector.n3);
    }

    @Override
    public int hashCode() {
        return Objects.hash(n1, n2, n3);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Vector3d other = (Vector3d) obj;
        return n1 == other.n1 && n2 == other.n2 && n3 == other.n3;
    }

    @Override
    public String toString() {
        if (cachedToString.isEmpty()) {
            cachedToString = Optional.of(XJson.asString("values", new double[] {n1, n2, n3}));
        }
        return cachedToString.get();
    }
}
