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

import id.matcv.MatUtils;
import id.matcv.converters.ConvertersToOpenCv;
import id.matcv.types.FileMat;
import id.matcv.types.camera.CameraInfo;
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
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint2f;

/**
 * Detect marked in 2d images
 *
 * @author lambdaprime intid@protonmail.com
 */
public class MarkerDetector2d {
    private static final XLogger LOGGER = XLogger.getLogger(MarkerDetector2d.class);
    private static final MatUtils matUtils = new MatUtils();
    private static final ConvertersToOpenCv converters = new ConvertersToOpenCv();

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

    private record DistortionParams(Mat cameraMat, MatOfDouble distortionMat) {}

    private Optional<DistortionParams> distortionParams = Optional.empty();

    public MarkerDetector2d withUndistortion(CameraInfo cameraInfo) {
        var cameraMat = converters.toMat64F(cameraInfo.cameraIntrinsics().cameraMatrix());
        var distortionMat = converters.toMatOfDouble(cameraInfo.distortionCoefficients());
        distortionParams = Optional.of(new DistortionParams(cameraMat, distortionMat));
        return this;
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
        List<Mat> detectedMarkers = new ArrayList<>();
        Mat ids = new Mat();
        Aruco.detectMarkers(img, dictionary, detectedMarkers, ids);
        for (int i = 0; i < detectedMarkers.size(); i++) {
            Optional<MarkerType> type = MarkerType.findType((int) ids.get(i, 0)[0]);
            if (type.isEmpty()) {
                LOGGER.warning("Unknown marker type - ignoring");
                continue;
            }
            if (!types.contains(type.get())) continue;
            var marker = new Marker(type.get());
            var corners = detectedMarkers.get(i);
            distortionParams.ifPresent(
                    params -> {
                        var points2d = new MatOfPoint2f(corners.reshape(2, 4));
                        matUtils.undistort(
                                points2d,
                                converters.copyToMatOfPoint32F(marker.create3dModel(1)),
                                params.cameraMat,
                                params.distortionMat);
                    });
            var mloc = MarkerLocation2d.create(marker, corners);
            markers.add(mloc);
            if (mloc.marker().isOrigin()) origin = Optional.of(markers.getLast());
        }
        markers.sort(Comparator.<MarkerLocation2d, Marker>comparing(ml -> ml.marker()));

        LOGGER.fine("Number of markers detected: {0}", markers.size());
        return new Result(img, markers, origin);
    }
}
