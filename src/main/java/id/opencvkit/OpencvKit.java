package id.opencvkit;

import java.util.LinkedList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
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
     * @return
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

}
