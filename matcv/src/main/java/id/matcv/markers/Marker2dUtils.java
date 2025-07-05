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
import id.matcv.RgbColors;
import id.matcv.converters.MatConverters;
import id.matcv.converters.NdBufferConverters;
import id.matcv.types.camera.CameraInfo;
import id.xfunction.Preconditions;
import java.util.ArrayList;
import java.util.List;
import org.opencv.aruco.Aruco;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class Marker2dUtils {

    private static final NdBufferConverters ndConverters = new NdBufferConverters();
    private static final MatConverters converters = new MatConverters();
    private static final MatUtils matUtils = new MatUtils();

    /** Highlight marker on the image */
    public void drawMarker(Mat image, MarkerLocation2d markerLocation) {
        drawCorners(image, markerLocation);
        drawCenter(image, markerLocation);
        drawLine(image, markerLocation);
        drawName(image, markerLocation);
    }

    private void drawCorners(Mat image, MarkerLocation2d markerLocation) {
        markerLocation
                .corners()
                .orElseThrow()
                .toList()
                .forEach(p -> Imgproc.drawMarker(image, p, RgbColors.YELLOW));
    }

    private void drawName(Mat image, MarkerLocation2d markerLocation) {
        Imgproc.putText(
                image,
                markerLocation.marker().toString(),
                ndConverters.toOpenCv(markerLocation.center(), 15, 10),
                Imgproc.FONT_HERSHEY_PLAIN,
                1.,
                new Scalar(0, 255, 0),
                2);
    }

    private void drawCenter(Mat img, MarkerLocation2d loc) {
        Imgproc.circle(img, ndConverters.toOpenCv(loc.center()), 4, new Scalar(0, 255, 0), 3);
    }

    private void drawLine(Mat img, MarkerLocation2d loc) {
        Imgproc.line(
                img,
                ndConverters.toOpenCv(loc.center()),
                ndConverters.toOpenCv(loc.vector().add(loc.center())),
                new Scalar(0, 255, 0));
    }

    /** Correct distortion of marker points with respect to given camera intrinsics */
    public List<MarkerLocation2d> undistort(
            Mat image, List<MarkerLocation2d> markers, CameraInfo cameraInfo) {
        var cameraMat = converters.toMat64F(cameraInfo.cameraMatrix());
        var distortionMat = converters.toMatOfDouble(cameraInfo.distortionCoefficients());
        matUtils.debugMat("cameraMat", cameraMat);
        matUtils.debugMat("distortionMat", distortionMat);
        Preconditions.equals(
                MarkerType.getDict(),
                Aruco.DICT_7X7_50,
                "SOLVEPNP_IPPE_SQUARE requires square markers. Currently supported are"
                        + " DICT_7X7_50.");
        var out = new ArrayList<MarkerLocation2d>();
        for (int i = 0; i < markers.size(); i++) {
            var markerLocation = markers.get(i);
            var marker = markerLocation.marker();
            var points3d = marker.create3dModel();
            var points2d = markerLocation.corners().orElseThrow();
            matUtils.undistort(points2d, points3d, cameraMat, distortionMat);
            out.add(MarkerLocation2d.create(marker, points2d.reshape(2, 1)));
        }
        return out;
    }
}
