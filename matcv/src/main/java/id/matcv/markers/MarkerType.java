/*
 * Copyright 2023 matcv project
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
package id.matcv.markers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.opencv.aruco.Aruco;

/**
 * All these markers are from the dictionary of 50 unique markers with 7x7 square bits on each. See
 * {@link #getDict()}.
 *
 * @author lambdaprime intid@protonmail.com
 */
public enum MarkerType {
    /** Origin marker */
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6);

    private int id;
    private static Map<Integer, MarkerType> map = new HashMap<>();

    private MarkerType(int id) {
        this.id = id;
    }

    public static int getDict() {
        return Aruco.DICT_7X7_50;
    }

    static {
        for (MarkerType type : MarkerType.values()) map.put(type.getId(), type);
    }

    public static Optional<MarkerType> findType(int id) {
        return Optional.ofNullable(map.get(id));
    }

    public int getId() {
        return id;
    }
}
