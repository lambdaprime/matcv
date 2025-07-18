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

import id.matcv.impl.ejml.KabschAlgorithm;
import id.mathcat.NdBuffersMath;
import id.ndbuffers.NdBuffersFactory;
import id.ndbuffers.Slice;
import id.ndbuffers.matrix.Matrix4d;
import id.xfunction.logging.XLogger;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.ejml.data.DMatrixRMaj;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class Marker3dUtils {
    private static final XLogger LOGGER = XLogger.getLogger(Marker3dUtils.class);
    private static final NdBuffersFactory ndFactory = new NdBuffersFactory();
    private static final NdBuffersMath ndMath = new NdBuffersMath();

    public Optional<MarkerLocation3d> findMarkerLocation(
            MarkerType type, List<MarkerLocation3d> markerLocations) {
        return markerLocations.stream().filter(loc -> loc.marker().getType() == type).findFirst();
    }

    /** Apply transformation matrix to all marker coordinates */
    public List<MarkerLocation3d> transformAll(
            List<MarkerLocation3d> markerLocations, Matrix4d tx) {
        // read all points across all markers into single big matrix
        var inPoints =
                DoubleBuffer.allocate(markerLocations.size() * MarkerLocation3d.NUM_OF_POINTS * 3);
        for (int i = 0; i < markerLocations.size(); i++) {
            inPoints.put(markerLocations.get(i).getData().duplicate());
        }
        inPoints.rewind();
        var mx = ndMath.transform(ndFactory.matrixN3d(inPoints), tx);
        var out = new ArrayList<MarkerLocation3d>(markerLocations.size());
        for (int i = 0; i < markerLocations.size(); i++) {
            var loc = markerLocations.get(i);
            var colStart = i * MarkerLocation3d.NUM_OF_POINTS;
            var data = ndFactory.matrixN3d(5);
            ndFactory
                    .matrixN3d(new Slice(colStart, colStart + 5, 1), new Slice(0, 3, 1), mx)
                    .copyTo(data, 0, 0);
            out.add(new MarkerLocation3d(loc.marker(), data, loc.corners(), loc.imageFile()));
        }
        return out;
    }

    /**
     * Calculate 4x4 transformation matrix between the markers
     *
     * <p>OpenCV has estimateRigidTransform for doing this but it is <a
     * href="https://answers.opencv.org/question/203570/is-estimaterigidtransform-removed-from-latest-opencv/">not
     * part of bindings</a>
     */
    public Matrix4d calculateTransformationMatrix(MarkerLocation3d from, MarkerLocation3d to) {
        return new Matrix4d(
                new KabschAlgorithm()
                        .calculateTransformation(
                                DMatrixRMaj.wrap(5, 3, from.getData().duplicate().array()),
                                DMatrixRMaj.wrap(5, 3, to.getData().duplicate().array()))
                        .getData());
    }

    /**
     * Calculate camera pose transformation matrix based on the current marker locations detected by
     * the camera (for example using {@link MarkerDetector3d}) and known fixed marker location given
     * in the world coordinates.
     *
     * <p>Camera pose can only be calculated when marker locations contain correspondent fixed
     * marker. For example if fixed marker has type {@link MarkerType#ONE} then same marker should
     * be present inside the input detected markers. The camera pose is calculated based on the
     * relative positions between points of such two markers.
     */
    public Optional<Matrix4d> findCameraPoseTx(
            List<MarkerLocation3d> markerLocations, MarkerLocation3d fixedMarkerPose) {
        var markerType = fixedMarkerPose.marker().type();
        var marker = findMarkerLocation(markerType, markerLocations).orElse(null);
        if (marker == null) {
            LOGGER.fine("Could not find marker {0}", markerType);
            return Optional.empty();
        }
        var tx = calculateTransformationMatrix(marker, fixedMarkerPose);
        LOGGER.fine("camera pose={0}", tx);
        return Optional.of(tx);
    }

    /** Frames received from depth cameras may contain noise pixels */
    public boolean hasVaildPoints(MarkerLocation3d marker) {
        var a = marker.p1().distance(marker.p2());
        var b = marker.p2().distance(marker.p3());
        var c = marker.p3().distance(marker.p4());
        var d = marker.p4().distance(marker.p1());
        var mse = (a + b + c + d) / 4.;
        return mse < 0.09;
    }
}
