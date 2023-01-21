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
package id.matcv.feature.match;

import static org.junit.jupiter.api.Assertions.assertEquals;

import id.matcv.OpenCvKit;
import id.matcv.feature.descriptor.FileDescriptor;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class MatchersTest {

    private Matchers matchers = new Matchers();
    private OpenCvKit openCvKit = new OpenCvKit();

    @Test
    public void test_matchKnn() throws IOException {
        // sqrt((1-5)^2 + (2-6)^2 + (3-7)^2 + (4-8)^2) = 8
        Mat out1 =
                openCvKit.toFlatMatrix(
                        List.of(new Scalar(1), new Scalar(2), new Scalar(3), new Scalar(4)));
        Mat out2 =
                openCvKit.toFlatMatrix(
                        List.of(new Scalar(5), new Scalar(6), new Scalar(7), new Scalar(8)));
        FileDescriptor desc1 = new FileDescriptor(out1, Paths.get("1"));
        FileDescriptor desc2 = new FileDescriptor(out2, Paths.get("2"));
        var matches = matchers.matchKnn(List.of(desc1), List.of(desc2), 1);
        assertEquals(8, matches.get(0).getDistance());
        assertEquals(1, matches.size());
    }

    @Test
    public void test_matchRadius() throws IOException {
        Mat out1 =
                openCvKit.toFlatMatrix(
                        List.of(new Scalar(1), new Scalar(2), new Scalar(3), new Scalar(4)));
        Mat out2 =
                openCvKit.toFlatMatrix(
                        List.of(new Scalar(5), new Scalar(6), new Scalar(7), new Scalar(8)));
        FileDescriptor desc1 = new FileDescriptor(out1, Paths.get("1"));
        FileDescriptor desc2 = new FileDescriptor(out2, Paths.get("2"));
        List<FileDescriptor> set = List.of(desc1, desc2);

        var matches = matchers.matchRadius(set, set, 9);
        assertEquals(0, matches.get(0).getDistance());
        assertEquals(8, matches.get(2).getDistance());
        assertEquals(3, matches.size());
    }
}
