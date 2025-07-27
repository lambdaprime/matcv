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

import id.matcv.types.camera.CameraIntrinsics;
import id.matcv.types.camera.CameraIntrinsicsPredefined;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class CameraConfigurationBuilder {
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private List<Consumer<RgbdImage>> rgbdFrameConsumers = new ArrayList<>();
    private List<Consumer<RgbImage>> rgbFrameConsumers = new ArrayList<>();
    private CameraIntrinsics intrinsics =
            CameraIntrinsicsPredefined.REALSENSE_D435i_640_480.getCameraIntrinsics();
    private boolean isShowFramesEnabled = false;
    private boolean isSpatialFilterEnabled = false;
    private boolean isExecutorPrivate = true;
    private Optional<Path> outputFolder = Optional.empty();

    public CameraConfigurationBuilder withExecutor(ExecutorService executor) {
        this.executor = executor;
        isExecutorPrivate = false;
        return this;
    }

    public CameraConfigurationBuilder addRgbdFrameConsumer(Consumer<RgbdImage> consumer) {
        rgbdFrameConsumers.add(consumer);
        return this;
    }

    public CameraConfigurationBuilder addRgbFrameConsumer(Consumer<RgbImage> consumer) {
        rgbFrameConsumers.add(consumer);
        return this;
    }

    public CameraConfigurationBuilder withIntrinsics(CameraIntrinsics intrinsics) {
        this.intrinsics = intrinsics;
        return this;
    }

    public CameraConfigurationBuilder enableShowFrames(boolean isEnabled) {
        isShowFramesEnabled = isEnabled;
        return this;
    }

    public CameraConfigurationBuilder enableSpatialFilter(boolean isEnabled) {
        isSpatialFilterEnabled = isEnabled;
        return this;
    }

    public CameraConfigurationBuilder withOutputFolder(Path outputFolder) {
        this.outputFolder = Optional.of(outputFolder);
        return this;
    }

    public CameraConfiguration build() {
        return new CameraConfiguration(
                executor,
                isExecutorPrivate,
                rgbdFrameConsumers,
                rgbFrameConsumers,
                intrinsics,
                isShowFramesEnabled,
                isSpatialFilterEnabled,
                outputFolder);
    }
}
