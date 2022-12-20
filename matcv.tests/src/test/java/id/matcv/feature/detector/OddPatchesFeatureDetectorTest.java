package id.matcv.feature.detector;

import id.matcv.MatUtils;
import id.matcv.OpencvTest;
import id.matcv.SubmatrixDetector;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.opencv.highgui.HighGui;

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
