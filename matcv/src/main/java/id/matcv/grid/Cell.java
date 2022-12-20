package id.matcv.grid;

import org.opencv.core.Mat;

public class Cell {

    public Mat mat;
    public int x;
    public int y;

    public Cell(Mat mat, int x, int y) {
        this.mat = mat;
        this.x = x;
        this.y = y;
    }

    public Mat getMat() {
        return mat;
    }
    
    @Override
    public String toString() {
        return String.format("x %d y %d", x, y);
    }
}
