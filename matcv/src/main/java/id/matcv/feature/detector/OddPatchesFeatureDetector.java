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
package id.matcv.feature.detector;

import id.matcv.grid.Cell;
import id.matcv.grid.CellFilters;
import id.matcv.grid.SplitWithGrid;
import java.util.function.Function;
import java.util.stream.Stream;
import org.opencv.core.Mat;
import org.opencv.core.Size;

/**
 * Extract odd patches from the image.
 *
 * <p>First it applies grid on the image and then extracts all cells from it with odd coordinates.
 *
 * <p>For 5x5 image and numOfPatches equals 4 it will split the image and extract them as follows:
 *
 * <pre>
 * . . . . .
 * . # . # .
 * . # . # .
 * . . . . .
 * </pre>
 *
 * Where # patches which will be extracted.
 */
public class OddPatchesFeatureDetector implements Function<Mat, Stream<Mat>> {

    private int numOfCells;

    public OddPatchesFeatureDetector(int numOfPatches) {
        this.numOfCells = numOfPatches;
    }

    @Override
    public Stream<Mat> apply(Mat img) {
        var cellLen = (int) Math.sqrt(numOfCells);
        if (cellLen * cellLen != numOfCells)
            throw new RuntimeException("Cannot create square grid");
        var newLen = cellLen * 2 + 1;
        return Stream.of(img)
                .flatMap(new SplitWithGrid(new Size(newLen, newLen)))
                .filter(CellFilters.ODD_CELL_COORDINATES)
                //            .peek(System.out::println)
                .map(Cell::getMat);
    }
}
