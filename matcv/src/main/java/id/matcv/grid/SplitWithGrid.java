package id.matcv.grid;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Stream;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;

/**
 * Applies a grid mask on the image which basically splits image on multiple
 * cells of an equal size.
 */
public class SplitWithGrid implements Function<Mat, Stream<Cell>> {

    private Size gridSize;

    /**
     * @param gridSize defines the size of the grid mask which is how many times image will
     * be split in x and y axis
     */
    public SplitWithGrid(Size gridSize) {
        this.gridSize = gridSize;
    }

    @Override
    public Stream<Cell> apply(Mat matrix) {
        var out = new ArrayList<Cell>();
        var size = new Size(matrix.cols() / gridSize.width,
                matrix.rows() / gridSize.height);
        //System.out.println(size);
        for (int y = 0; y < gridSize.height; y++) {
            for (int x = 0; x < gridSize.width; x++) {
                out.add(new Cell(matrix.submat(new Rect(new Point(x * size.width, y * size.height), size)), x, y));
            }
        }
        return out.stream();
    }
}
