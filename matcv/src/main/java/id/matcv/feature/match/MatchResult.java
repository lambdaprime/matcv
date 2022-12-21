/*
 * Copyright 2022 matcv project
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
package id.matcv.feature.match;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class MatchResult<T extends Comparable<T>> {

    private T a, b;
    private float distance;

    public MatchResult(T a, T b, float distance) {
        var list = List.of(a, b);
        this.a = Collections.min(list);
        this.b = Collections.max(list);
        this.distance = distance;
    }

    public float getDistance() {
        return distance;
    }

    public T getA() {
        return a;
    }

    public T getB() {
        return b;
    }

    public boolean isIdentical() {
        return a.equals(b);
    }

    public static <T extends Comparable<T>> Comparator<MatchResult<T>> compareByDistance() {
        return (r1, r2) -> Float.compare(r1.distance, r2.distance);
    }

    @Override
    public String toString() {
        return String.format("distance=%s, a='%s', b='%s'", distance, a, b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b, distance);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        MatchResult other = (MatchResult) obj;
        return Objects.equals(a, other.a)
                && Objects.equals(b, other.b)
                && Objects.equals(distance, other.distance);
    }
}
