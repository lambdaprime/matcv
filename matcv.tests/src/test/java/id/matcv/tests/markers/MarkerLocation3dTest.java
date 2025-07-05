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
package id.matcv.tests.markers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import id.matcv.markers.Marker;
import id.matcv.markers.MarkerLocation3d;
import id.matcv.markers.MarkerType;
import id.matcv.tests.OpenCvTest;
import id.ndbuffers.NdBuffersFactory;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class MarkerLocation3dTest extends OpenCvTest {

    private static final NdBuffersFactory ndFactory = new NdBuffersFactory();

    @Test
    public void test() {
        var marker3d =
                new MarkerLocation3d(
                        new Marker(MarkerType.ONE),
                        ndFactory.matrixN3d(
                                new double[] {
                                    1, 2, 3,
                                    4, 5, 6,
                                    7, 8, 9,
                                    10, 11, 12,
                                    13, 14, 15
                                }),
                        Optional.empty(),
                        Optional.empty());
        assertEquals(
                """
                { "marker": "ONE",\
                 "imageFile": "Optional.empty",\
                 "center": { "x": 1, "y": 2 },\
                 "p1": { "x": 4, "y": 5 },\
                 "p2": { "x": 7, "y": 8 },\
                 "p3": { "x": 10, "y": 11 },\
                 "p4": { "x": 13, "y": 14 },\
                 "heightPixels": 4.2426406871,\
                 "widthPixels": 4.2426406871,\
                 "vector": { "x": 4.5, "y": 4.5 } }""",
                marker3d.toMarkerLocation2d().toString());
    }
}
