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
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * @param start index of the first item. Default is 0.
 * @param stop index of the last item of the slice. Default is {@link Integer#MAX_VALUE} which means
 *     slicing continues up to the last item inside current dimension.
 * @param step step size between consecutive items of the slice. Default is 1.
 * @author lambdaprime intid@protonmail.com
 */
public record Slice(int start, int stop, int step) {

    /**
     * @see <a
     *     href="https://numpy.org/doc/stable/user/basics.indexing.html#slicing-and-striding">Slicing
     *     and striding in NumPy</a>
     */
    public static Slice of(String expr) {
        if (expr.indexOf(':') < 0) throw new IllegalArgumentException("Slice expression is empty");
        var tokens =
                Arrays.stream(expr.split(":"))
                        .map(String::trim)
                        .filter(Predicate.not(String::isEmpty))
                        .mapToInt(Integer::parseInt)
                        .toArray();
        var start = 0;
        var stop = Integer.MAX_VALUE;
        var step = 1;
        if (tokens.length == 0) return new Slice(start, stop, step);
        else if (tokens.length == 1) return new Slice(tokens[0], stop, step);
        else if (tokens.length == 2) return new Slice(tokens[0], tokens[1], step);
        else if (tokens.length == 3) return new Slice(tokens[0], tokens[1], tokens[2]);
        else throw new IllegalArgumentException("Not valid slice expression: " + expr);
    }

    /** Iterate over all indices of the slice */
    public IntStream iterate() {
        var count = new int[1];
        count[0] = start;
        return IntStream.iterate(start, pos -> pos < stop, pos -> pos + step);
    }

    /** Index of ith item of the slice */
    public int index(int i) {
        if (i < 0) throw new ArrayIndexOutOfBoundsException("Index cannot be negative");
        var res = start + i * step;
        if (res >= stop)
            throw new ArrayIndexOutOfBoundsException("stop=%d index=%d".formatted(stop, res));
        return res;
    }
}
