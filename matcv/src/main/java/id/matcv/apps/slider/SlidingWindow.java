/*
 * Copyright 2022 matcv project
 * 
 * Website: https://github.com/lambdaprime/matcv
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package id.matcv.apps.slider;

import id.matcv.MatUtils;
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
    private MatUtils utils = new MatUtils();

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
                utils.overlay(window, b, x, y);
                var fileName =
                        String.format(
                                "%s-%s-%d.%d-%d.%d.png",
                                backgroundName,
                                windowName,
                                x,
                                y,
                                x + window.width(),
                                y + window.height());
                Imgcodecs.imwrite(outputDir.resolve(fileName).toString(), b);
            }
        }
    }
}
