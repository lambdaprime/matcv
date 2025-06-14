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
package id.matcv.types.ndbuffers;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @param dims dimension sizes
 * @author lambdaprime intid@protonmail.com
 */
public class Shape {

    private final int[] dims;
    private final int[] prefixSizes;
    private String toString;

    public Shape(int... dims) {
        if (Arrays.stream(dims).filter(i -> i == 0).findAny().isPresent())
            throw new IllegalArgumentException("0 size dimension");
        this.dims = dims;
        prefixSizes = calcPrefixSizes(dims);
        toString = "shape=" + Arrays.toString(dims);
    }

    public int[] dims() {
        return dims;
    }

    /**
     * Total number of items in all dimensions of the shape starting from startDim.
     *
     * <p>If startDim = 0 it shows total number of items in all dimensions of the shape
     */
    public int size(int startDim) {
        return prefixSizes[startDim];
    }

    /**
     * Iterate over all dimensions of the shape by generating valid indices in the increasing order
     */
    public Stream<int[]> iterate() {
        var max = Arrays.stream(dims).map(i -> i - 1).toArray();
        var stream =
                Stream.iterate(
                        new int[dims.length],
                        indices -> !Arrays.equals(indices, max),
                        indices -> {
                            for (int i = indices.length - 1; i >= 0; i--) {
                                if (indices[i] < dims[i] - 1) {
                                    indices[i]++;
                                    break;
                                } else {
                                    indices[i] = 0;
                                }
                            }
                            return indices;
                        });
        return Stream.concat(stream, Stream.of(max));
    }

    @Override
    public String toString() {
        return toString;
    }

    private static int[] calcPrefixSizes(int[] dims) {
        var prefixSizes = new int[dims.length];
        prefixSizes[prefixSizes.length - 1] = dims[dims.length - 1];
        for (int i = prefixSizes.length - 2; i >= 0; i--) {
            prefixSizes[i] = dims[i] * prefixSizes[i + 1];
        }
        return prefixSizes;
    }
}
