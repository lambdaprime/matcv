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
package id.matcv.converters;

import id.ndbuffers.NdBuffersFactory;
import id.ndbuffers.matrix.Matrix3d;
import id.ndbuffers.matrix.Vector2d;
import id.xfunction.Preconditions;
import java.lang.foreign.MemorySegment;
import java.nio.ByteOrder;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class NdBufferConverters {
    private static final NdBuffersFactory ndFactory = new NdBuffersFactory();

    public Point toOpenCv(Vector2d point) {
        return toOpenCv(point, 0, 0);
    }

    public Point toOpenCv(Vector2d point, int offsetX, int offsetY) {
        return new Point(point.getX() + offsetX, point.getY() + offsetY);
    }

    public int toIndex(Vector2d p, int w, int h) {
        return (int) p.getY() * w + (int) p.getX();
    }

    public Vector2d toPoint2d(int index, int w, int h) {
        return ndFactory.vector2d(index % w, index / w);
    }

    public Matrix3d mapToMatrix3d(Mat opencvMat) {
        Preconditions.equals(CvType.CV_64F, opencvMat.type());
        var segment =
                MemorySegment.ofAddress(opencvMat.dataAddr())
                        .reinterpret(opencvMat.total() * Double.BYTES);
        return new Matrix3d(segment.asByteBuffer().order(ByteOrder.nativeOrder()).asDoubleBuffer());
    }
}
