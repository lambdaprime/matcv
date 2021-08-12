package id.opencvkit;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class MatUtils {

    /**
     * Converts byte array to CV_8U matrix
     */
    public Mat asMat(byte...values) {
        var mat = new Mat(1, values.length, CvType.CV_8U);
        mat.put(0, 0, values);
        return mat;
    }

}
