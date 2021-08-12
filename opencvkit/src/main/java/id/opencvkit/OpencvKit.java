package id.opencvkit;

import java.util.LinkedList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class OpencvKit {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    /**
     * Add obj to background at position x, y. Transparent pixels
     * will be ignored:
     * 
     *      background[i] = obj[i] == 0? background[i]? obj[i]
     * 
     */
    public static void overlay(Mat obj, Mat background, int x, int y) {
        var invObj = new Mat();
        Core.bitwise_not(obj, invObj);
        var submat = background.submat(new Rect(x, y, obj.width(), obj.height()));
        Core.bitwise_and(submat, invObj, submat);
        Core.bitwise_or(submat, obj, submat);
    }

    /**
     * Resize preserving ratio
     * @param size final size of the longest side
     */
    public static Mat resize(Mat img, Size size) {
        Size s;
        double r = img.size().width / img.size().height;
        if (img.size().height > img.size().width)
            s = new Size(size.width * r, size.height);
        else
            s = new Size(size.width, size.height / r);
        var tmp = new Mat(s, img.type());
        Imgproc.resize(img, tmp, tmp.size());
        Mat tmp2 = Mat.zeros((int)size.height, (int)size.width, img.type());
        tmp.copyTo(tmp2.submat(new Rect(new double[]{
                (size.width - s.width) / 2, (size.height - s.height) / 2, s.width, s.height})));
        return tmp2;
    }

    /**
     * Add an alpha 255 channel to the RGB image
     */
    public static Mat addAlpha(Mat mat) {
        var mv = new LinkedList<Mat>();
        var alpha = new Mat(mat.size(), CvType.CV_8UC1, new Scalar(255));
        Core.split(mat, mv);
        mv.addLast(alpha);
        var ret = new Mat();
        Core.merge(mv, ret);
        System.out.println(ret);
        return ret;
    }

    /**
     * Returns middle point
     */
    public static Point middlePoint(Point p1, Point p2) {
        return new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
    }
    
    /**
     * Flattens scalars into one dimensional array.
     * 
     * Example:
     * 
     * Input: Scalar(1.0, 2.0, 3.0), Scalar(3.0, 4.0, 5.0), Scalar(6.0, 7.0, 8.0)
     * Output: [1, 2, 3, 0, 3, 4, 5, 0, 6, 7, 8, 0]
     * 
     * Scalar sizes aligned which means Scalar(1.0, 2.0, 3.0) in memory
     * will take size 4 not 3 => the output array has additional zeros.
     */
    public static Mat toFlatMatrix(List<Scalar> scalars) {
        int scalarLen = scalars.get(0).val.length;
        var mat = new Mat(1, scalars.size() * scalarLen , CvType.CV_32F);
        for (int i = 0; i < scalars.size(); i++) {
            var scalar = scalars.get(i).val;
            for (int j = 0; j < scalarLen; j++) {
                mat.put(0, i * scalarLen + j, scalar[j]);
            }
        }
        return mat;
    }
}
