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
package id.matcv.feature.match;

import java.util.Comparator;
import org.opencv.core.DMatch;

/** Compares two DMatch results and returns the one which has smaller distance to the query */
public class DMatchComparator implements Comparator<DMatch> {

    @Override
    public int compare(DMatch arg0, DMatch arg1) {
        return arg0.lessThan(arg1) ? -1 : 1;
    }
}
