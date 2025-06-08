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
package id.matcv.tests.ndbuffers;

import id.matcv.tests.OpencvTest;
import id.matcv.types.ndbuffers.matrix.Matrix4d;
import java.lang.foreign.MemorySegment;
import java.nio.ByteOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class NdBuffersExamplesTest extends OpencvTest {

    @Test
    public void test() {
        var oneDimensionalArray =
                new double[] {
                    1, 2, 3, 4,
                    5, 6, 7, 8,
                    9, 10, 11, 12,
                    13, 14, 15, 16
                };
        var mx = new Matrix4d(oneDimensionalArray);
        Assertions.assertEquals(8, mx.get(1, 3));
        mx.set(-1, 1, 3);
        Assertions.assertEquals(-1, oneDimensionalArray[7]);
        var opencvMat = Mat.ones(new int[] {4, 4}, CvType.CV_64F);
        var segment =
                MemorySegment.ofAddress(opencvMat.dataAddr())
                        .reinterpret(opencvMat.total() * Double.BYTES);
        mx = new Matrix4d(segment.asByteBuffer().order(ByteOrder.nativeOrder()).asDoubleBuffer());
        Assertions.assertEquals(
                """
                { "data" : [
                 1, 1, 1, 1,
                 1, 1, 1, 1,
                 1, 1, 1, 1,
                 1, 1, 1, 1
                ] }""",
                mx.toString());
        mx.set(-1.5, 1, 3);
        Assertions.assertEquals(
                """
                [1, 1, 1, 1;
                 1, 1, 1, -1.5;
                 1, 1, 1, 1;
                 1, 1, 1, 1]""",
                opencvMat.dump());
    }
}
