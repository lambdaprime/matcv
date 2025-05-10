/*
 * Copyright 2024 matcv project
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
package id.matcv.types;

import id.matcv.types.datatables.DataTable;
import id.xfunction.Preconditions;
import java.util.List;

/**
 * Table of all key points detected in a single point cloud
 *
 * @param pointIds list of key points indices inside the point cloud. Indices must be sorted in the
 *     increasing order, to match same order how points are stored inside point clouds (important
 *     for {@link
 *     RegistratorFromKeyPoints#reconstructScene(id.clouds3d.types.datatables.DataTable3)})
 * @author lambdaprime intid@protonmail.com
 */
public record KeyPoints3dTable(List<Integer> pointIds, List<Integer> pointHashes)
        implements DataTable {
    public KeyPoints3dTable {
        Preconditions.equals(
                pointIds.size(),
                pointHashes.size(),
                "Mismatch between pointIds and globalPointIds");
    }

    @Override
    public int size() {
        return pointIds.size();
    }

    @Override
    public boolean isEmpty() {
        return pointIds.isEmpty();
    }

    public int pointIds(int i) {
        return pointIds.get(i);
    }

    public int pointHashes(int i) {
        return pointHashes.get(i);
    }
}
