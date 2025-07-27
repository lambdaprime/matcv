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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * @param isShowFramesEnabled display every new frame on the screen and wait until user press any
 *     key
 * @param isSpatialFilterEnabled control RealSense spatial filter
 * @param outputFolder save all frames into a folder
 * @param inputFolder read frames from the folder instead of camera
 * @author lambdaprime intid@protonmail.com
 */
public record CameraConfiguration(
        ExecutorService executor,
        boolean isExecutorPrivate,
        List<Consumer<RgbdImage>> rgbdFrameConsumers,
        List<Consumer<RgbImage>> rgbFrameConsumers,
        CameraIntrinsics intrinsics,
        boolean isShowFramesEnabled,
        boolean isSpatialFilterEnabled,
        Optional<Path> outputFolder,
        Optional<Path> inputFolder) {

    public CameraConfiguration {
        outputFolder.ifPresent(
                folder -> {
                    try {
                        Files.createDirectories(folder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
