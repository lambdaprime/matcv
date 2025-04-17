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
package id.matcv.types.datatables;

import id.xfunction.Preconditions;
import java.util.List;

/**
 * @author lambdaprime intid@protonmail.com
 */
public record DataTable2<C1, C2>(List<C1> col1, List<C2> col2) implements DataTable {

    public static <C1, C2> DataTable2<C1, C2> empty() {
        return new DataTable2<>(List.of(), List.of());
    }

    public DataTable2 {
        Preconditions.equals(
                col1.size(),
                col2.size(),
                "Mismatch between number of items in column 1 and 2:" + " %s != %s",
                col1.size(),
                col2.size());
    }

    public C1 col1(int i) {
        return col1.get(i);
    }

    public C2 col2(int i) {
        return col2.get(i);
    }

    @Override
    public int size() {
        return col1.size();
    }

    @Override
    public boolean isEmpty() {
        return col1.isEmpty();
    }
}
