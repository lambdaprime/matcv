package id.matcv.apps.slider;

import id.matcv.OpencvKit;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

// threadsafe
public class SlidingWindow {

    private final Mat window;
    private final Path outputDir;
    private final String windowName;

    public SlidingWindow(Mat window, Path outputDir, String windowName) throws IOException {
        this.window = window;
        this.outputDir = outputDir;
        this.windowName = windowName;
        if (!Files.isDirectory(outputDir)) {
            Files.createDirectories(outputDir);
        }
    }

    public void slide(String backgroundName, Mat background) {
        for (int y = 0; y < background.rows() - window.height(); y += window.height()) {
            for (int x = 0; x < background.cols() - window.width(); x += window.width()) {
                var b = background.clone();
                OpencvKit.overlay(window, b, x, y);
                var fileName = String.format("%s-%s-%d.%d-%d.%d.png", backgroundName, windowName, x, y,
                    x + window.width(), y + window.height());
                Imgcodecs.imwrite(outputDir.resolve(fileName).toString(), b);
            }
        }
    }

}
