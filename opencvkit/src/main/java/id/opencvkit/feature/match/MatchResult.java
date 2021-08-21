package id.opencvkit.feature.match;

public class MatchResult<T> {

    private T a, b;
    private float distance;
    
    public MatchResult(T a, T b, float distance) {
        this.a = a;
        this.b = b;
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
    
    @Override
    public String toString() {
        return String.format("distance=%s, a='%s', b='%s'", distance,
                a, b);
    }

}
