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

import id.matcv.types.camera.CameraInfoPredefined;
import id.xfunction.logging.XLogger;
import java.io.IOException;
import org.opencv.core.Core;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class CameraFactory {
    private static final XLogger LOGGER = XLogger.getLogger(CameraFactory.class);

    public Camera create(CameraConfiguration config) {
        if (config.inputFolder().isPresent()) {
            LOGGER.info("Creating replay camera");
            return new ReplayCamera(config);
        }
        LOGGER.info("Creating RealSense camera");
        return new RealSenseCamera(config);
    }

    public static void main(String[] args) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        XLogger.load("logging-matcv-debug.properties");
        var cameraInfo = CameraInfoPredefined.REALSENSE_D435i_640_480.getCameraInfo();
        var config =
                new CameraConfigurationBuilder()
                        .addRgbFrameConsumer(
                                new RgbToMarker3dTransformer(
                                        cameraInfo,
                                        markers -> LOGGER.info("Markers detected: {0}", markers)))
                        .withIntrinsics(cameraInfo.cameraIntrinsics())
                        // .withInputFolder(Path.of(""))
                        .enableShowFrames(true)
                        .build();
        try (var camera = new CameraFactory().create(config)) {
            camera.start();
            System.out.println("Press Enter to stop...");
            System.in.read();
        }
    }
}
