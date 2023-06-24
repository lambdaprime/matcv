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

import id.matcv.markers.Marker;
import id.matcv.markers.MarkerType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * See pregenerated markers in <a href="doc-files/markers.odt">markers.odt</a>.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class GenerateMarkerApp {

    private static void usage() {
        System.out.println("Usage: GenerateMarkerApp <OUTPUT_DIR>");
        System.out.println("Generates markers for pose estimation and stores them to OUTPUT_DIR");
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            usage();
            return;
        }
        Path path = Paths.get(args[0]);
        Files.createDirectories(path);
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        var dict = "dict_" + MarkerType.getDict();
        for (int i = 0; i < MarkerType.values().length; i++) {
            var markerType = MarkerType.values()[i];
            var mId = markerType.getId();
            System.out.println("Generating marker " + mId);
            Marker m = new Marker(markerType);
            Mat img = m.createImage();
            Imgcodecs.imwrite(path.resolve(dict + "_" + mId + ".jpg").toString(), img);
        }
    }
}
