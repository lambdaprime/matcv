package id.opencvkit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import id.xfunction.ResourceUtils;

public class MatUtils {

    private ResourceUtils resourceUtils = new ResourceUtils();

    /**
     * Converts byte array to CV_8U matrix
     */
    public Mat asMat(byte...values) {
        var mat = new Mat(1, values.length, CvType.CV_8U);
        mat.put(0, 0, values);
        return mat;
    }

    public Mat readImageFromResource(String absoluteResourcePath) throws IOException {
        Path out = Files.createTempFile("resource", "");
        resourceUtils.extractResource(absoluteResourcePath, out);
        return Imgcodecs.imread(out.toAbsolutePath().toString());
    }
}
