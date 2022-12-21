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
        var slidingWindow =
                new SlidingWindow(
                        Imgcodecs.imread(
                                "/media/x/pinorobotics/misc/cross/overwatch1555806128372.png"),
                        Paths.get("/tmp/lol1"),
                        "overwatch1555806128372");
        Files.walk(Paths.get("/media/x/pinorobotics/misc/background/"))
                .parallel()
                .filter(Files::isRegularFile)
                .map(SliderApp::preproc)
                .forEach(p -> slidingWindow.slide((String) p[0], (Mat) p[1]));
    }
}
