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

/**
 * N-dimensional slice
 *
 * @author lambdaprime intid@protonmail.com
 */
public record NSlice(Slice... slices) {
    /**
     * @param exprs list of expressions as defined in {@link Slice#of(String)}
     */
    public static NSlice of(String... exprs) {
        return new NSlice(Arrays.stream(exprs).map(Slice::of).toArray(i -> new Slice[i]));
    }

    @Override
    public final String toString() {
        return Arrays.toString(slices);
    }
}
