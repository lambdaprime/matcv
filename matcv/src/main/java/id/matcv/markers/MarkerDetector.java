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

import id.mathcat.LineUtils;
import id.xfunction.logging.XLogger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.opencv.aruco.Aruco;
import org.opencv.aruco.Dictionary;
import org.opencv.core.Mat;

public class MarkerDetector {
    private static final XLogger LOGGER = XLogger.getLogger(MarkerDetector.class);

    public record Result(
            Mat img, LinkedList<MarkerLocation> markers, Optional<MarkerLocation> origin) {}

    public Result detect(Mat img) {
        var origin = Optional.<MarkerLocation>empty();
        var markers = new LinkedList<MarkerLocation>();
        Dictionary dictionary = Aruco.getPredefinedDictionary(MarkerType.getDict());
        List<Mat> corners = new ArrayList<>();
        Mat ids = new Mat();
        Aruco.detectMarkers(img, dictionary, corners, ids);
        for (int i = 0; i < corners.size(); i++) {
            Optional<MarkerType> type = MarkerType.findType((int) ids.get(i, 0)[0]);
            //            System.out.println(type);
            if (type.isEmpty()) {
                LOGGER.warning("Unknown marker type - ignoring");
                continue;
            }
            var m = corners.get(i);
            double[] d;
            d = m.get(0, 0);
            var p1 = new Vector2D(d[0], d[1]);
            d = m.get(0, 1);
            var p2 = new Vector2D(d[0], d[1]);
            d = m.get(0, 2);
            var p3 = new Vector2D(d[0], d[1]);
            d = m.get(0, 3);
            var p4 = new Vector2D(d[0], d[1]);
            var center = LineUtils.midPoint(LineUtils.midPoint(p1, p2), LineUtils.midPoint(p3, p4));
            var mloc =
                    new MarkerLocation(
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
        markers.sort(Comparator.<MarkerLocation, Marker>comparing(ml -> ml.marker()));
        return new Result(img, markers, origin);
    }
}
