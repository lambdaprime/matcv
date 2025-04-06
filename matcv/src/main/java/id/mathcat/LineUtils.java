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
package id.mathcat;

import id.matcv.Vector2D;

public class LineUtils {

    public static Vector2D midPoint(Vector2D p1, Vector2D p2) {
        return new Vector2D((p1.getX() + p2.getX()) / 2, (p1.getY() + p2.getY()) / 2);
    }

    /**
     * Creates a vector with the direction and magnitude of the difference between toPoint and
     * fromPoint
     */
    public static Vector2D createVector(Vector2D fromPoint, Vector2D toPoint) {
        return toPoint.subtract(fromPoint);
    }
}
