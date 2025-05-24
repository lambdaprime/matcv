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
package id.matcv.impl.ejml;

import id.xfunction.Preconditions;
import id.xfunction.logging.XLogger;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.dense.row.factory.DecompositionFactory_DDRM;

/**
 * Java Kabsch algorithm implementation based on EJML
 *
 * @see <a href="https://en.wikipedia.org/wiki/Kabsch_algorithm">Kabsch algorithm</a>
 * @see <a
 *     href="https://github.com/nghiaho12/rigid_transform_3D/blob/master/matlab/example.m">Original
 *     implementation in Octave</a>
 * @author lambdaprime intid@protonmail.com
 */
public class KabschAlgorithm {
    private static final XLogger LOGGER = XLogger.getLogger(KabschAlgorithm.class);
    private static final EjmlUtils utils = new EjmlUtils();

    public DMatrixRMaj calculateTransformation(DMatrixRMaj from, DMatrixRMaj to) {
        int dim = 3;
        Preconditions.equals(dim, from.getNumCols());
        Preconditions.equals(dim, to.getNumCols());

        var centroidFrom = utils.mean(from);
        var centroidTo = utils.mean(to);
        LOGGER.fine("centroidFrom={0}", centroidFrom);
        LOGGER.fine("centroidTo={0}", centroidTo);

        var centeredFrom = utils.subtract(from, centroidFrom);
        var centeredTo = utils.subtract(to, centroidTo);
        LOGGER.fine("centeredFrom={0}", centeredFrom);
        LOGGER.fine("centeredTo={0}", centeredTo);

        var H = new DMatrixRMaj();
        CommonOps_DDRM.multTransA(centeredFrom, centeredTo, H);
        LOGGER.fine("H={0}", H);

        var svd = DecompositionFactory_DDRM.svd(H.numRows, H.numCols, true, true, false);
        if (!svd.decompose(H)) throw new RuntimeException("Decomposition failed");
        var U = svd.getU(null, false);
        var S = svd.getW(null);
        var V = svd.getV(null, false);
        LOGGER.fine("U={0}", U);
        LOGGER.fine("S={0}", S);
        LOGGER.fine("V={0}", V);

        var R = new DMatrixRMaj();
        CommonOps_DDRM.multTransB(V, U, R);

        if (CommonOps_DDRM.det(R) < 0) {
            LOGGER.fine("Correcting for reflection");
            S = CommonOps_DDRM.identity(dim);
            S.set(dim - 1, dim - 1, -1);
            var VS = new DMatrixRMaj();
            CommonOps_DDRM.mult(V, S, VS);
            CommonOps_DDRM.multTransB(VS, U, R);
        }
        LOGGER.fine("R={0}", R);

        CommonOps_DDRM.elementPower(centeredFrom, 2, centeredFrom);
        CommonOps_DDRM.elementPower(centeredTo, 2, centeredTo);
        var scale = Math.sqrt(utils.meanAll(centeredTo) / utils.meanAll(centeredFrom));
        LOGGER.fine("scale={0}", scale);

        var t = new DMatrixRMaj(centroidTo);
        CommonOps_DDRM.transpose(t);
        CommonOps_DDRM.multAddTransB(-scale, R, centroidFrom, t);
        LOGGER.fine("t={0}", t);

        var TX = new DMatrixRMaj(4, 4);
        CommonOps_DDRM.insert(R, TX, 0, 0);
        CommonOps_DDRM.insert(t, TX, 0, 3);
        TX.set(3, 3, 1);

        return TX;
    }
}
