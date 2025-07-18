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
package id.matcv.sensors.realsense;

import id.matcv.markers.Marker;
import id.matcv.markers.MarkerDetector3d;
import id.matcv.markers.MarkerLocation3d;
import id.matcv.types.camera.CameraInfo;
import java.util.List;
import java.util.function.Consumer;

/**
 * Accept {@link RgbdImage} and find all {@link Marker}
 *
 * @author lambdaprime intid@protonmail.com
 */
public class RgbToMarker3dTransformer implements Consumer<RgbImage> {
    private Consumer<List<MarkerLocation3d>> consumer;
    private MarkerDetector3d detector;

    public RgbToMarker3dTransformer(
            CameraInfo cameraInfo, Consumer<List<MarkerLocation3d>> consumer) {
        this.consumer = consumer;
        detector = new MarkerDetector3d(cameraInfo);
    }

    public RgbToMarker3dTransformer withUndistortion(boolean isEnabled) {
        if (isEnabled) detector = detector.withUndistortion();
        return this;
    }

    @Override
    public void accept(RgbImage rgb) {
        var markers = detector.detect(List.of(rgb.colorMat()));
        if (markers.isEmpty()) return;
        consumer.accept(markers.get(0));
    }
}
