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

import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Stream;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;

/**
 * Applies a grid mask on the image which basically splits image on multiple cells of an equal size.
 */
public class SplitWithGrid implements Function<Mat, Stream<Cell>> {

    private Size gridSize;

    /**
     * @param gridSize defines the size of the grid mask which is how many times image will be split
     *     in x and y axis
     */
    public SplitWithGrid(Size gridSize) {
        this.gridSize = gridSize;
    }

    @Override
    public Stream<Cell> apply(Mat matrix) {
        var out = new ArrayList<Cell>();
        var size = new Size(matrix.cols() / gridSize.width, matrix.rows() / gridSize.height);
        // System.out.println(size);
        for (int y = 0; y < gridSize.height; y++) {
            for (int x = 0; x < gridSize.width; x++) {
                out.add(
                        new Cell(
                                matrix.submat(
                                        new Rect(new Point(x * size.width, y * size.height), size)),
                                x,
                                y));
            }
        }
        return out.stream();
    }
}
