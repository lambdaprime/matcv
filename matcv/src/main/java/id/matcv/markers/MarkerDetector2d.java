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
package id.matcv.markers;

import id.matcv.FileMat;
import id.matcv.Vector2D;
import id.mathcat.LineUtils;
import id.xfunction.XJsonStringBuilder;
import id.xfunction.logging.XLogger;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.opencv.aruco.Aruco;
import org.opencv.aruco.Dictionary;
import org.opencv.core.Mat;

public class MarkerDetector2d {
    private static final XLogger LOGGER = XLogger.getLogger(MarkerDetector2d.class);

    public record Result(
            Mat img,
            List<MarkerLocation2d> markersSortedByType,
            Optional<MarkerLocation2d> origin) {

        @Override
        public String toString() {
            XJsonStringBuilder builder = new XJsonStringBuilder(this);
            builder.append("markers", markersSortedByType);
            builder.append("origin", origin);
            return builder.toString();
        }
    }

    /** Detect all {@link MarkerType} markers */
    public Result detect(FileMat img) {
        return detect(img, EnumSet.allOf(MarkerType.class));
    }

    /** Detect specified {@link MarkerType} markers */
    public Result detect(FileMat img, EnumSet<MarkerType> types) {
        var file = img.getFile();
        LOGGER.info("Detecting markers on image: {0}", file);
        return detect(img, Optional.of(file), types);
    }

    /** Detect all {@link MarkerType} markers */
    public Result detect(Mat img) {
        return detect(img, Optional.empty());
    }

    public Result detect(Mat img, Optional<Path> file) {
        return detect(img, file, EnumSet.allOf(MarkerType.class));
    }

    public Result detect(Mat img, Optional<Path> file, EnumSet<MarkerType> types) {
        var origin = Optional.<MarkerLocation2d>empty();
        var markers = new LinkedList<MarkerLocation2d>();
        Dictionary dictionary = Aruco.getPredefinedDictionary(MarkerType.getDict());
        List<Mat> corners = new ArrayList<>();
        Mat ids = new Mat();
        Aruco.detectMarkers(img, dictionary, corners, ids);
        for (int i = 0; i < corners.size(); i++) {
            Optional<MarkerType> type = MarkerType.findType((int) ids.get(i, 0)[0]);
            if (type.isEmpty()) {
                LOGGER.warning("Unknown marker type - ignoring");
                continue;
            }
            if (!types.contains(type.get())) continue;
            var m = corners.get(i);
            double[] buf;
            buf = m.get(0, 0);
            var p1 = new Vector2D(buf[0], buf[1]);
            buf = m.get(0, 1);
            var p2 = new Vector2D(buf[0], buf[1]);
            buf = m.get(0, 2);
            var p3 = new Vector2D(buf[0], buf[1]);
            buf = m.get(0, 3);
            var p4 = new Vector2D(buf[0], buf[1]);
            var center = LineUtils.midPoint(LineUtils.midPoint(p1, p2), LineUtils.midPoint(p3, p4));
            var mloc =
                    new MarkerLocation2d(
                            p1,
                            p2,
                            p3,
                            p4,
                            center,
                            p1.distance(p2),
                            p2.distance(p3),
                            m,
                            LineUtils.createVector(center, LineUtils.midPoint(p1, p2)),
                            new Marker(type.get()));
            markers.add(mloc);
            if (mloc.marker().isOrigin()) origin = Optional.of(markers.getLast());
        }
        markers.sort(Comparator.<MarkerLocation2d, Marker>comparing(ml -> ml.marker()));

        LOGGER.info("Number of markers detected: {0}", markers.size());
        return new Result(img, markers, origin);
    }
}
