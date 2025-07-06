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
package id.mathcat;

import id.ndbuffers.NdBuffersFactory;
import id.ndbuffers.matrix.Matrix4d;
import id.ndbuffers.matrix.MatrixN3d;
import id.ndbuffers.matrix.Vector2d;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class NdBuffersMath {
    private static final NdBuffersFactory ndFactory = new NdBuffersFactory();

    public Vector2d midPoint(Vector2d p1, Vector2d p2) {
        return new Vector2d((p1.getX() + p2.getX()) / 2, (p1.getY() + p2.getY()) / 2);
    }

    /**
     * Creates a vector with the direction and magnitude of the difference between toPoint and
     * fromPoint
     */
    public Vector2d createVector(Vector2d fromPoint, Vector2d toPoint) {
        return toPoint.subtract(fromPoint);
    }

    /** Apply rigid transformation matrix to set of 3D points */
    public MatrixN3d transform(MatrixN3d mx, Matrix4d tx) {
        // In order to mul Matrix4d with MatrixX3 we need to covert MatrixX3 to Matrix4X
        // this would require to store points in column major order:
        // p1.x p2.x ...
        // p1.y p2.y ...
        // p1.z p2.z ...
        //    1    1 ...
        var inPointsMx = new DMatrixRMaj(4, mx.getRows());
        inPointsMx.fill(1);
        transfer(mx, inPointsMx);
        var ejmlTx = new DMatrixRMaj();
        ejmlTx.setData(tx.duplicate().array());
        ejmlTx.reshape(4, 4);
        var outPointsMx = new DMatrixRMaj();
        CommonOps_DDRM.mult(
                ejmlTx,
                inPointsMx,
                outPointsMx); // This does M * V where V is the stack of all points
        // the answer stored in column major order
        // we need to restore it back to row major and drop last row:
        // p1.x p1.y p1.z
        // p2.x p2.y p2.z
        // ...
        outPointsMx.reshape(outPointsMx.getNumRows() - 1, outPointsMx.getNumCols(), false);
        outPointsMx = CommonOps_DDRM.transpose(outPointsMx, null);
        return ndFactory.matrixN3d(outPointsMx.data);
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
