package id.opencvkit.grid;

import java.util.function.Predicate;

/**
 * Different cell filters
 */
public interface CellFilters extends Predicate<Cell>{

    /**
     * Filter cells with odd x and y coordinates
     */
    CellFilters ODD_CELL_COORDINATES = new CellFilters() {
        @Override
        public boolean test(Cell cell) {
            return  (cell.x % 2 != 0 && cell.y % 2 != 0);
        }
    };
    
    CellFilters EVEN_CELL_COORDINATES = new CellFilters() {
        @Override
        public boolean test(Cell cell) {
            return (cell.x % 2 == 0 && cell.y % 2 == 0);
        }
    };
    
}