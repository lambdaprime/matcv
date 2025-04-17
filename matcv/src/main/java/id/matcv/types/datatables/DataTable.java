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

import java.util.ArrayList;
import java.util.List;

/**
 * @author lambdaprime intid@protonmail.com
 */
public interface DataTable {

    int size();

    boolean isEmpty();

    /**
     * Join col1, col2 with column {@link DataTable2#col2()} from the input table based on the
     * primary keys inside column {@link DataTable2#col1()}
     */
    static <C1, C2, C3> DataTable3<C1, C2, C3> innerJoin(
            List<C1> col1, List<C2> col2, DataTable2<Integer, C3> table2) {
        var outCol1 = new ArrayList<C1>();
        var outCol2 = new ArrayList<C2>();
        var outCol3 = new ArrayList<C3>();
        for (int i = 0; i < table2.size(); ++i) {
            var key = table2.col1(i);
            outCol1.add(col1.get(key));
            outCol2.add(col2.get(key));
            outCol3.add(table2.col2(i));
        }
        return new DataTable3<>(outCol1, outCol2, outCol3);
    }

    static <C1, C2, C3> void collapse(List<C1> col1) {
        var ncol1 = new ArrayList<C1>();
        for (int i = 0; i < col1.size(); i++) {
            var f1 = col1.get(i);
            var hasNullField = f1 == null;
            if (!hasNullField) {
                ncol1.add(f1);
            }
        }
        col1.clear();
        col1.addAll(ncol1);
    }

    /**
     * If any of the input columns has null field at index "i" then entire row "i" is removed across
     * all the columns.
     *
     * <p>Row "i" is removed by removing corresponding elements with index "i" across all the
     * columns, even if they contain non null items.
     */
    static <C1, C2, C3> void collapse(List<C1> col1, List<C2> col2, List<C3> col3) {
        var ncol1 = new ArrayList<C1>();
        var ncol2 = new ArrayList<C2>();
        var ncol3 = new ArrayList<C3>();
        for (int i = 0; i < col1.size(); i++) {
            var f1 = col1.get(i);
            var f2 = col2.get(i);
            var f3 = col3.get(i);
            var hasNullField = f1 == null || f2 == null || f3 == null;
            if (!hasNullField) {
                ncol1.add(f1);
                ncol2.add(f2);
                ncol3.add(f3);
            }
        }
        col1.clear();
        col1.addAll(ncol1);
        col2.clear();
        col2.addAll(ncol2);
        col3.clear();
        col3.addAll(ncol3);
    }

    static <C1, C2, C3, C4> void collapse(
            List<C1> col1, List<C2> col2, List<C3> col3, List<C4> col4) {
        var ncol1 = new ArrayList<C1>();
        var ncol2 = new ArrayList<C2>();
        var ncol3 = new ArrayList<C3>();
        var ncol4 = new ArrayList<C4>();
        for (int i = 0; i < col1.size(); i++) {
            var f1 = col1.get(i);
            var f2 = col2.get(i);
            var f3 = col3.get(i);
            var f4 = col4.get(i);
            var hasNullField = f1 == null || f2 == null || f3 == null || f4 == null;
            if (!hasNullField) {
                ncol1.add(f1);
                ncol2.add(f2);
                ncol3.add(f3);
                ncol4.add(f4);
            }
        }
        col1.clear();
        col1.addAll(ncol1);
        col2.clear();
        col2.addAll(ncol2);
        col3.clear();
        col3.addAll(ncol3);
        col4.clear();
        col4.addAll(ncol4);
    }
}
