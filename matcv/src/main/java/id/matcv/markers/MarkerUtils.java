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

import id.matcv.converters.PointConverters;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class MarkerUtils {

    private static final PointConverters converters = new PointConverters();

    /** Highlight marker on the image */
    public void drawMarker(Mat image, MarkerLocation2d markerLocation) {
        drawCenter(image, markerLocation);
        drawLine(image, markerLocation);
        drawName(image, markerLocation);
    }

    private void drawName(Mat image, MarkerLocation2d markerLocation) {
        Imgproc.putText(
                image,
                markerLocation.marker().toString(),
                converters.toOpenCv(markerLocation.center(), 15, 10),
                Imgproc.FONT_HERSHEY_PLAIN,
                1.,
                new Scalar(0, 255, 0),
                2);
    }

    private void drawCenter(Mat img, MarkerLocation2d loc) {
        Imgproc.circle(img, converters.toOpenCv(loc.center()), 4, new Scalar(0, 255, 0), 3);
    }

    private void drawLine(Mat img, MarkerLocation2d loc) {
        Imgproc.line(
                img,
                converters.toOpenCv(loc.center()),
                converters.toOpenCv(loc.vector().add(loc.center())),
                new Scalar(0, 255, 0));
    }
}
