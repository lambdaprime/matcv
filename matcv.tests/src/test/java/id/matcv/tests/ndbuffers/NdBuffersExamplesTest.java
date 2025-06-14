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
import id.matcv.types.ndbuffers.NSlice;
import id.matcv.types.ndbuffers.Slice;
import id.matcv.types.ndbuffers.matrix.Matrix3d;
import id.matcv.types.ndbuffers.matrix.Matrix4d;
import id.matcv.types.ndbuffers.matrix.MatrixNd;
import id.matcv.types.ndbuffers.matrix.Vector3d;
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
        var mx4d = new Matrix4d(oneDimensionalArray);
        Assertions.assertEquals(8, mx4d.get(1, 3));
        mx4d.set(-1, 1, 3);
        Assertions.assertEquals(-1, oneDimensionalArray[7]);

        Assertions.assertEquals(
                """
                { "data" : [
                 1, 2, 3, 4,
                 5, 6, 7, -1,
                 9, 10, 11, 12,
                 13, 14, 15, 16
                ] }""",
                mx4d.dump());
        var mx3d = new Matrix3d(Slice.of("1:4"), Slice.of("1:4"), mx4d);
        Assertions.assertEquals(
                """
                { "data" : [
                 6, 7, -1,
                 10, 11, 12,
                 14, 15, 16
                ] }""",
                mx3d.dump());
        var mx2d = new MatrixNd(Slice.of("0:3:2"), Slice.of("0:3:2"), mx3d);
        Assertions.assertEquals(
                """
                { "data" : [
                 6, -1,
                 14, 16
                ] }""",
                mx2d.dump());
        mx2d.set(-16, 1, 1);
        Assertions.assertEquals(
                """
                { "data" : [
                 1, 2, 3, 4,
                 5, 6, 7, -1,
                 9, 10, 11, 12,
                 13, 14, 15, -16
                ] }""",
                mx4d.dump());
        var vector3d = new Vector3d(NSlice.of("1:", ":"), mx3d);
        Assertions.assertEquals(
                """
                { "data" : [
                 10, 11, 12
                ] }""",
                vector3d.dump());

        var opencvMat = Mat.ones(new int[] {4, 4}, CvType.CV_64F);
        var segment =
                MemorySegment.ofAddress(opencvMat.dataAddr())
                        .reinterpret(opencvMat.total() * Double.BYTES);
        mx4d = new Matrix4d(segment.asByteBuffer().order(ByteOrder.nativeOrder()).asDoubleBuffer());
        Assertions.assertEquals(
                """
                { "data" : [
                 1, 1, 1, 1,
                 1, 1, 1, 1,
                 1, 1, 1, 1,
                 1, 1, 1, 1
                ] }""",
                mx4d.dump());
        mx4d.set(-1.5, 1, 3);
        Assertions.assertEquals(
                """
                [1, 1, 1, 1;
                 1, 1, 1, -1.5;
                 1, 1, 1, 1;
                 1, 1, 1, 1]""",
                opencvMat.dump());
    }
}
