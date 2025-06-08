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

import java.util.stream.IntStream;

public record Slice(int start, int stop, int step) {

    public IntStream iterate() {
        var count = new int[1];
        count[0] = start;
        return IntStream.iterate(start, pos -> pos < stop, pos -> pos + step);
    }

    public int index(int i) {
        if (i < 0) throw new ArrayIndexOutOfBoundsException("Index cannot be negative");
        var res = start + i * step;
        if (res >= stop)
            throw new ArrayIndexOutOfBoundsException("stop=%d index=%d".formatted(stop, res));
        return res;
    }
}
