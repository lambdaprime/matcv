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
        return String.format("distance=%s, a='%s', b='%s'", distance,
                a, b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b, distance);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MatchResult other = (MatchResult) obj;
        return Objects.equals(a, other.a) &&
                Objects.equals(b, other.b) &&
                Objects.equals(distance, other.distance);
    }

    
}
