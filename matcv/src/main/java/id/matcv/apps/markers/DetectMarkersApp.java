/*
 * Copyright 2023 matcv project
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
package id.matcv.apps.markers;

import id.matcv.camera.CameraInfo;
import id.matcv.markers.CameraPoseEstimator;
import id.matcv.markers.MarkerDetector;
import id.matcv.markers.MarkerUtils;
import id.xfunction.nio.file.FilePredicates;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.opencv.core.Core;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

public class DetectMarkersApp {

    private static void usage() {
        System.out.println(
                """
                Usage: %s <INPUT_PATH>

                Detects all markers present on the images. Markers are highlighted and their location is printed to console.
                By default CameraInfo.BLENDER_DEFAULT is used.
                """
                        .formatted(DetectMarkersApp.class.getSimpleName()));
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            usage();
            return;
        }
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Files.walk(Paths.get(args[0]), 1)
                .filter(FilePredicates.match(".*\\.(png|jpg)"))
                .sorted()
                .forEach(DetectMarkersApp::detect);
        HighGui.destroyAllWindows();
        // for some reason destroyAllWindows is not enough so we forcefully terminate JVM
        System.exit(0);
    }

    private static void detect(Path imageFile) {
        var img = Imgcodecs.imread(imageFile.toString());
        var markers = new MarkerDetector().detect(img).markers();
        System.err.format("%s markers detected on image %s\n", markers.size(), imageFile);
        if (markers.isEmpty()) return;
        markers.forEach(
                loc -> {
                    System.out.println(loc);
                    new MarkerUtils().drawMarker(img, loc);
                });

        var poseEstimator = new CameraPoseEstimator(CameraInfo.BLENDER_DEFAULT);
        poseEstimator.estimate(img, markers);

        HighGui.imshow(imageFile.toString(), img);
        HighGui.waitKey();
    }
}
