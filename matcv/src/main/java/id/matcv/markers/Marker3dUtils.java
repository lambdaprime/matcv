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

import id.matcv.types.Matrix4d;
import id.matcv.types.Vector3D;
import id.xfunction.logging.XLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;

public class Marker3dUtils {
    private static final XLogger LOGGER = XLogger.getLogger(Marker3dUtils.class);

    public Optional<MarkerLocation3d> findMarkerLocation(
            MarkerType type, List<MarkerLocation3d> markerLocations) {
        return markerLocations.stream().filter(loc -> loc.marker().getType() == type).findFirst();
    }

    public List<MarkerLocation3d> transformAll(
            List<MarkerLocation3d> markerLocations, Matrix4d tx) {
        var inPointsMx =
                new DMatrixRMaj(4, markerLocations.size() * MarkerLocation3d.NUM_OF_POINTS);
        // In order to mul Matrix4d with MatrixX3 we need to covert MatrixX3 to Matrix4X
        // this would require to store points in column major order:
        // p1.x p2.x ...
        // p1.y p2.y ...
        // p1.z p2.z ...
        //    1    1 ...
        for (int i = 0; i < markerLocations.size(); i++) {
            var points = markerLocations.get(i).points();
            for (int j = 0; j < MarkerLocation3d.NUM_OF_POINTS; j++) {
                var d = points.get(j).getData();
                var col = i * MarkerLocation3d.NUM_OF_POINTS + j;
                inPointsMx.set(0, col, d.get(0));
                inPointsMx.set(1, col, d.get(1));
                inPointsMx.set(2, col, d.get(2));
                inPointsMx.set(3, col, 1);
            }
        }
        var ejmlTx = new DMatrixRMaj(tx.getData().array());
        ejmlTx.reshape(4, 4);
        System.out.println(inPointsMx);
        DMatrixRMaj outPointsMx = new DMatrixRMaj();
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
                            new Vector3D(
                                    outPointsMx.get(0, colStart + 0),
                                    outPointsMx.get(1, colStart + 0),
                                    outPointsMx.get(2, colStart + 0)),
                            new Vector3D(
                                    outPointsMx.get(0, colStart + 1),
                                    outPointsMx.get(1, colStart + 1),
                                    outPointsMx.get(2, colStart + 1)),
                            new Vector3D(
                                    outPointsMx.get(0, colStart + 2),
                                    outPointsMx.get(1, colStart + 2),
                                    outPointsMx.get(2, colStart + 2)),
                            new Vector3D(
                                    outPointsMx.get(0, colStart + 3),
                                    outPointsMx.get(1, colStart + 3),
                                    outPointsMx.get(2, colStart + 3)),
                            new Vector3D(
                                    outPointsMx.get(0, colStart + 4),
                                    outPointsMx.get(1, colStart + 4),
                                    outPointsMx.get(2, colStart + 4))));
        }
        return out;
    }
}
