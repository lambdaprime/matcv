package id.matcv;

import java.util.function.Consumer;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Every time we detect that given submatrix belongs to original matrix
 * we highlight its location on original matrix itself.
 */
public class SubmatrixDetector implements Consumer<Mat> {

    private Mat matrix;
    
    public SubmatrixDetector(Mat matrix) {
        this.matrix = matrix;
    }

    @Override
    public void accept(Mat submatrix) {
        if (!submatrix.isSubmatrix()) return;
        var size = new Size();
        var point = new Point();
        submatrix.locateROI(size, point);
//        System.out.println(size);
        Imgproc.rectangle(matrix, new Rect(point, submatrix.size()), RGBColors.GREEN);
    }

}
