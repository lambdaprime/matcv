/*
 * Copyright 2022 matcv project
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
package id.matcv.grid;

import java.util.function.Predicate;

/** Different cell filters */
public interface CellFilters extends Predicate<Cell> {

    /** Filter cells with odd x and y coordinates */
    CellFilters ODD_CELL_COORDINATES =
            new CellFilters() {
                @Override
                public boolean test(Cell cell) {
                    return (cell.x % 2 != 0 && cell.y % 2 != 0);
                }
            };

    CellFilters EVEN_CELL_COORDINATES =
            new CellFilters() {
                @Override
                public boolean test(Cell cell) {
                    return (cell.x % 2 == 0 && cell.y % 2 == 0);
                }
            };
}
