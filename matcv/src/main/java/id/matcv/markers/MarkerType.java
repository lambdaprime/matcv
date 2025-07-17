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
import java.util.Set;
import org.opencv.aruco.Aruco;
import org.opencv.aruco.Dictionary;

/**
 * All these markers are from the dictionary of 50 unique markers with 7x7 square bits on each. See
 * {@link #getDict()}.
 *
 * <p>Each marker contains 5 key points which locations can be identified on the images. These key
 * points are the center point of the marker and the corners p1, ..., p4 which are given in the
 * clockwise order. The first corner is the top left corner, followed by the top right, bottom right
 * and bottom left.
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

    private static final int NUMBER_OF_KEY_POINTS = 5;
    private static Map<Integer, MarkerType> map = new HashMap<>();
    private int id;
    private int centerHash;
    private int p1Hash;
    private int p2Hash;
    private int p3Hash;
    private int p4Hash;
    private Set<Integer> hashes;

    private MarkerType(int id) {
        this.id = id;
        this.centerHash = id * NUMBER_OF_KEY_POINTS + 0;
        this.p1Hash = id * NUMBER_OF_KEY_POINTS + 1;
        this.p2Hash = id * NUMBER_OF_KEY_POINTS + 2;
        this.p3Hash = id * NUMBER_OF_KEY_POINTS + 3;
        this.p4Hash = id * NUMBER_OF_KEY_POINTS + 4;
        this.hashes = Set.of(centerHash, p1Hash, p2Hash, p3Hash, p4Hash);
    }

    /** Dictionary {@link Aruco#DICT_7X7_50} */
    public static int getDict() {
        return Aruco.DICT_7X7_50;
    }

    /**
     * Number of bits per each dimension of the marker (markerSize x markerSize bits).
     *
     * @see <a
     *     href="https://docs.opencv.org/3.4/d9/d6a/group__aruco.html#gab0dd0832bceb1131946e4be9f26317ba">OpenCV
     *     documentation</a>
     */
    public static float getMarkerSize() {
        Dictionary dictionary = Aruco.getPredefinedDictionary(MarkerType.getDict());
        return dictionary.get_markerSize();
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

    /**
     * Hash code which uniquely identifies center point of this marker. It allows to match points of
     * same marker across different images.
     */
    public int centerHash() {
        return centerHash;
    }

    /**
     * Hash code which uniquely identifies p1 point of this marker. It allows to match points of
     * same marker across different images.
     */
    public int p1Hash() {
        return p1Hash;
    }

    /**
     * Hash code which uniquely identifies p2 point of this marker. It allows to match points of
     * same marker across different images.
     */
    public int p2Hash() {
        return p2Hash;
    }

    /**
     * Hash code which uniquely identifies p3 point of this marker. It allows to match points of
     * same marker across different images.
     */
    public int p3Hash() {
        return p3Hash;
    }

    /**
     * Hash code which uniquely identifies p4 point of this marker. It allows to match points of
     * same marker across different images.
     */
    public int p4Hash() {
        return p4Hash;
    }

    /** Check if point with given hash belongs to this marker */
    public boolean hasPoint(int hash) {
        return hashes.contains(hash);
    }

    public static Optional<MarkerType> findMarkerByPointHash(int hash) {
        for (var m : MarkerType.values()) {
            if (m.hasPoint(hash)) return Optional.of(m);
        }
        return Optional.empty();
    }
}
