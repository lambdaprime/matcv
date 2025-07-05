/*
 * Copyright 2025 matcv project
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
package id.matcv.tests.markers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import id.matcv.markers.Marker2dUtils;
import id.matcv.markers.MarkerDetector2d;
import id.matcv.tests.OpenCvTest;
import id.matcv.types.camera.CameraInfoPredefined;
import org.junit.jupiter.api.Test;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class Marker2dUtilsTest extends OpenCvTest {
    @Test
    public void test() {
        var img = Imgcodecs.imread("samples/00000000-rgb.png");
        var markers = new MarkerDetector2d().detect(img).markersSortedByType();
        assertEquals(
                """
                [{\
                 "marker": "ONE",\
                 "imageFile": "Optional.empty",\
                 "center": { "x": 360, "y": 281.75 },\
                 "p1": { "x": 333, "y": 254 },\
                 "p2": { "x": 387, "y": 248 },\
                 "p3": { "x": 387, "y": 308 },\
                 "p4": { "x": 333, "y": 317 },\
                 "heightPixels": 54.3323108288,\
                 "widthPixels": 60,\
                 "vector": { "x": 0, "y": -30.75 } }, \
                { "marker": "TWO",\
                 "imageFile": "Optional.empty",\
                 "center": { "x": 365.5, "y": 104.75 },\
                 "p1": { "x": 343, "y": 79 },\
                 "p2": { "x": 386, "y": 82 },\
                 "p3": { "x": 387, "y": 129 },\
                 "p4": { "x": 346, "y": 129 },\
                 "heightPixels": 43.1045241245,\
                 "widthPixels": 47.0106370942,\
                 "vector": { "x": -1, "y": -24.25 } }, \
                { "marker": "FOUR",\
                 "imageFile": "Optional.empty",\
                 "center": { "x": 221.25, "y": 359.25 },\
                 "p1": { "x": 268, "y": 361 },\
                 "p2": { "x": 197, "y": 376 },\
                 "p3": { "x": 178, "y": 356 },\
                 "p4": { "x": 242, "y": 344 },\
                 "heightPixels": 72.5672102261,\
                 "widthPixels": 27.5862284483,\
                 "vector": { "x": 11.25, "y": 9.25 } }, \
                { "marker": "FIVE",\
                 "imageFile": "Optional.empty",\
                 "center": { "x": 183.5, "y": 182.5 },\
                 "p1": { "x": 154, "y": 155 },\
                 "p2": { "x": 210, "y": 154 },\
                 "p3": { "x": 213, "y": 209 },\
                 "p4": { "x": 157, "y": 212 },\
                 "heightPixels": 56.0089278598,\
                 "widthPixels": 55.0817574157,\
                 "vector": { "x": -1.5, "y": -28 } }, \
                { "marker": "SIX",\
                 "imageFile": "Optional.empty",\
                 "center": { "x": 438.75, "y": 391.25 },\
                 "p1": { "x": 428, "y": 414 },\
                 "p2": { "x": 380, "y": 384 },\
                 "p3": { "x": 447, "y": 371 },\
                 "p4": { "x": 500, "y": 396 },\
                 "heightPixels": 56.6038867923,\
                 "widthPixels": 68.249542123,\
                 "vector": { "x": -34.75, "y": 7.75 } }]""",
                markers.toString());
        var undistorted =
                new Marker2dUtils()
                        .undistort(
                                img,
                                markers,
                                CameraInfoPredefined.REALSENSE_D435i_640_480.getCameraInfo());
        assertEquals(
                """
                [{\
                 "marker": "ONE",\
                 "imageFile": "Optional.empty",\
                 "center": { "x": 359.99983, "y": 281.74987 },\
                 "p1": { "x": 332.75836, "y": 254.0537 },\
                 "p2": { "x": 387.21954, "y": 247.94702 },\
                 "p3": { "x": 386.78687, "y": 308.06393 },\
                 "p4": { "x": 333.23456, "y": 316.93481 },\
                 "heightPixels": 54.8024796465,\
                 "widthPixels": 60.1184698747,\
                 "vector": { "x": -0.01088, "y": -30.74951 } }, \
                {\
                 "marker": "TWO",\
                 "imageFile": "Optional.empty",\
                 "center": { "x": 365.49989, "y": 104.75036 },\
                 "p1": { "x": 343.46127, "y": 79.36335 },\
                 "p2": { "x": 385.59113, "y": 81.62414 },\
                 "p3": { "x": 387.42776, "y": 129.33109 },\
                 "p4": { "x": 345.51941, "y": 128.68288 },\
                 "heightPixels": 42.1904683147,\
                 "widthPixels": 47.7422880425,\
                 "vector": { "x": -0.97369, "y": -24.25662 } }, \
                {\
                 "marker": "FOUR",\
                 "imageFile": "Optional.empty",\
                 "center": { "x": 221.25224, "y": 359.24902 },\
                 "p1": { "x": 267.72113, "y": 361.16769 },\
                 "p2": { "x": 197.3667, "y": 375.80945 },\
                 "p3": { "x": 177.64577, "y": 356.1546 },\
                 "p4": { "x": 242.27536, "y": 343.86432 },\
                 "heightPixels": 71.8618601719,\
                 "widthPixels": 27.8429189926,\
                 "vector": { "x": 11.29168, "y": 9.23956 } }, \
                {\
                 "marker": "FIVE",\
                 "imageFile": "Optional.empty",\
                 "center": { "x": 183.49995, "y": 182.49995 },\
                 "p1": { "x": 153.82858, "y": 154.99596 },\
                 "p2": { "x": 210.15953, "y": 154.00423 },\
                 "p3": { "x": 212.84218, "y": 209.00153 },\
                 "p4": { "x": 157.16951, "y": 211.99809 },\
                 "heightPixels": 56.3396771066,\
                 "widthPixels": 55.0626871609,\
                 "vector": { "x": -1.50589, "y": -27.99986 } }, \
                {\
                 "marker": "SIX",\
                 "imageFile": "Optional.empty",\
                 "center": { "x": 438.75143, "y": 391.24532 },\
                 "p1": { "x": 427.81595, "y": 413.51266 },\
                 "p2": { "x": 380.15625, "y": 384.35318 },\
                 "p3": { "x": 446.78702, "y": 370.72958 },\
                 "p4": { "x": 500.24649, "y": 396.38586 },\
                 "heightPixels": 55.8723761558,\
                 "widthPixels": 68.0092757921,\
                 "vector": { "x": -34.76533, "y": 7.6876 } }]""",
                undistorted.toString());
    }
}
