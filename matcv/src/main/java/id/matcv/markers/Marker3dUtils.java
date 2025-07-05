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
import id.ndbuffers.matrix.Matrix4d;
import id.ndbuffers.matrix.MatrixN3d;
import id.ndbuffers.matrix.Vector3d;
import id.xfunction.logging.XLogger;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class Marker3dUtils {
    private static final XLogger LOGGER = XLogger.getLogger(Marker3dUtils.class);

    public Optional<MarkerLocation3d> findMarkerLocation(
            MarkerType type, List<MarkerLocation3d> markerLocations) {
        return markerLocations.stream().filter(loc -> loc.marker().getType() == type).findFirst();
    }

    /** Apply transformation matrix to all marker coordinates */
    public List<MarkerLocation3d> transformAll(
            List<MarkerLocation3d> markerLocations, Matrix4d tx) {
        var inPoints =
                DoubleBuffer.allocate(markerLocations.size() * MarkerLocation3d.NUM_OF_POINTS * 3);
        // In order to mul Matrix4d with MatrixX3 we need to covert MatrixX3 to Matrix4X
        // this would require to store points in column major order:
        // p1.x p2.x ...
        // p1.y p2.y ...
        // p1.z p2.z ...
        //    1    1 ...
        for (int i = 0; i < markerLocations.size(); i++) {
            inPoints.put(markerLocations.get(i).getData().duplicate().duplicate());
        }
        var inPointsMx =
                new DMatrixRMaj(4, markerLocations.size() * MarkerLocation3d.NUM_OF_POINTS);
        inPointsMx.fill(1);
        transfer(new MatrixN3d(inPoints), inPointsMx);
        var ejmlTx = new DMatrixRMaj();
        ejmlTx.setData(tx.duplicate().array());
        ejmlTx.reshape(4, 4);
        var outPointsMx = new DMatrixRMaj();
        CommonOps_DDRM.mult(
                ejmlTx,
                inPointsMx,
                outPointsMx); // This does M * V where V is the stack of all points
        var out = new ArrayList<MarkerLocation3d>(markerLocations.size());
        // the answer stored in column major order
        // we need to restore it back to row major and drop last row:
        // p1.x p1.y p1.z
        // p2.x p2.y p2.z
        // ...
        for (int i = 0; i < markerLocations.size(); i++) {
            var loc = markerLocations.get(i);
            var colStart = i * MarkerLocation3d.NUM_OF_POINTS;
            out.add(
                    new MarkerLocation3d(
                            loc.marker(),
                            new Vector3d(
                                    outPointsMx.get(0, colStart + 0),
                                    outPointsMx.get(1, colStart + 0),
                                    outPointsMx.get(2, colStart + 0)),
                            new Vector3d(
                                    outPointsMx.get(0, colStart + 1),
                                    outPointsMx.get(1, colStart + 1),
                                    outPointsMx.get(2, colStart + 1)),
                            new Vector3d(
                                    outPointsMx.get(0, colStart + 2),
                                    outPointsMx.get(1, colStart + 2),
                                    outPointsMx.get(2, colStart + 2)),
                            new Vector3d(
                                    outPointsMx.get(0, colStart + 3),
                                    outPointsMx.get(1, colStart + 3),
                                    outPointsMx.get(2, colStart + 3)),
                            new Vector3d(
                                    outPointsMx.get(0, colStart + 4),
                                    outPointsMx.get(1, colStart + 4),
                                    outPointsMx.get(2, colStart + 4)),
                            loc.corners()));
        }
        return out;
    }

    /** Calculate 4x4 transformation matrix between the markers */
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

    /** Transpose input matrix and transfer its data to output matrix */
    private void transfer(MatrixN3d mx, DMatrixRMaj outMx) {
        var ejmlMx = new DMatrixRMaj();
        ejmlMx.setData(mx.duplicate().array());
        ejmlMx.reshape(mx.getRows(), 3);
        // CommonOps_DDRM.transpose modifies shape of the output matrix
        // so we use wrapper matrix as an output matrix and not the original
        outMx = DMatrixRMaj.wrap(outMx.numRows, outMx.numCols, outMx.data);
        CommonOps_DDRM.transpose(ejmlMx, outMx);
    }
}
