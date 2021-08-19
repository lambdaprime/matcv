package id.opencvkit.feature.detector;

import java.util.function.Function;
import java.util.stream.Stream;

import org.opencv.core.Mat;
import org.opencv.core.Size;

import id.opencvkit.grid.Cell;
import id.opencvkit.grid.CellFilters;
import id.opencvkit.grid.SplitWithGrid;

/**
 * Extract patches from the image.
 * 
 * First it applies grid on the image and then extracts all cells from it with
 * odd coordinates.
 * 
 * For 5x5 image and numOfPatches equals 4 it will split the image and 
 * extract them as follows:
 * 
 * <pre>
 * . . . . .
 * . # . # .
 * . # . # .
 * . . . . .
 * </pre>
 * 
 * Where # patches which will be extracted.
 * 
 */
public class OddPatchesFeatureDetector implements Function<Mat, Stream<Mat>> {
    
    private int numOfCells;

    public OddPatchesFeatureDetector(int numOfPatches) {
        this.numOfCells = numOfPatches;
    }

    @Override
    public Stream<Mat> apply(Mat img) {
        var cellLen = (int)Math.sqrt(numOfCells);
        if (cellLen * cellLen != numOfCells) throw new RuntimeException("Cannot create square grid");
        var newLen = cellLen * 2 + 1;
        return Stream.of(img)
            .flatMap(new SplitWithGrid(new Size(newLen, newLen)))
            .filter(CellFilters.ODD_CELL_COORDINATES)
//            .peek(System.out::println)
            .map(Cell::getMat);
    }
}
