package id.matcv.apps.slider;

import id.matcv.OpencvKit;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;

public class SliderApp {

    private static final int CROP_START = 425;
    private static final int CROP_END = 1520;
    private static final Size SIZE = new Size(200, 200);

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    static Object[] preproc(Path img) {
        String path = img.toString();
        System.out.println("Processing " + path);
        Mat mat = Imgcodecs.imread(path);
        mat = mat.submat(0, mat.rows(), CROP_START, CROP_END);
        mat = OpencvKit.resize(mat, SIZE);
        String name = img.getFileName().toString().replaceAll("\\.\\w*", "");
        return new Object[] {name, mat};
    }

    public static void main(String[] args) throws IOException {
        var slidingWindow = new SlidingWindow(Imgcodecs.imread("/media/x/pinorobotics/misc/cross/overwatch1555806128372.png"),
            Paths.get("/tmp/lol1"), "overwatch1555806128372");
        Files.walk(Paths.get("/media/x/pinorobotics/misc/background/")).parallel()
            .filter(Files::isRegularFile)
            .map(SliderApp::preproc)
            .forEach(p -> slidingWindow.slide((String)p[0], (Mat)p[1]));
    }

}
