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
 * Vector which contains two floats.
 *
 * <p>Vector itself is immutable all public fields are final.
 *
 * <p>All operations are immutable and return new copy of the vector.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class Vector2f {

    private Optional<String> cachedToString = Optional.empty();
    public final float n1, n2;

    public Vector2f(Vector2f vec) {
        this.n1 = vec.n1;
        this.n2 = vec.n2;
    }

    public Vector2f(float n1, float n2) {
        this.n1 = n1;
        this.n2 = n2;
    }

    /** L2 norm, equals to the square root of the dot product of vector with itself. */
    public float norm() {
        return (float) Math.sqrt(n1 * n1 + n2 * n2);
    }

    /** Normalizes the vector, i.e. divides it by its own norm */
    public Vector2f normalize() {
        var n = norm();
        return new Vector2f(n1 / n, n2 / n);
    }

    /** Multiply vector with the scalar */
    public Vector2f mul(float scalar) {
        return new Vector2f(n1 * scalar, n2 * scalar);
    }

    /** Multiply vector with the scalar */
    public Vector2f sub(Vector2f vector) {
        return new Vector2f(n1 - vector.n1, n2 * vector.n2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(n1, n2);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Vector2f other = (Vector2f) obj;
        return n1 == other.n1 && n2 == other.n2;
    }

    @Override
    public String toString() {
        if (cachedToString.isEmpty()) {
            cachedToString = Optional.of(XJson.asString("values", new float[] {n1, n2}));
        }
        return cachedToString.get();
    }
}
