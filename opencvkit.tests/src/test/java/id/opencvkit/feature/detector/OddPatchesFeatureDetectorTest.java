package id.opencvkit.feature.detector;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.opencv.highgui.HighGui;

import id.opencvkit.MatUtils;
import id.opencvkit.OpencvTest;
import id.opencvkit.SubmatrixDetector;

public class OddPatchesFeatureDetectorTest extends OpencvTest {

    //@Test
    public void demo() throws IOException {
        var utils = new MatUtils();
        var img = utils.readImageFromResource("alita.jpg");
        var detector = new SubmatrixDetector(img);
        new OddPatchesFeatureDetector(16).apply(img).forEach(detector);
        HighGui.imshow("test", img);
        HighGui.waitKey();
    }
}
