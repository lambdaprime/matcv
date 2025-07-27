/*
 * Copyright 2024 matcv project
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

import id.xfunction.logging.XLogger;
import id.xfunction.util.IdempotentService;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class ReplayCamera extends IdempotentService implements Camera {
    private static final XLogger LOGGER = XLogger.getLogger(ReplayCamera.class);
    private CameraConfiguration config;

    public ReplayCamera() {
        this(new CameraConfigurationBuilder().build());
    }

    public ReplayCamera(CameraConfiguration config) {
        this.config = config;
    }

    /** Setup resources and run the looper */
    @Override
    protected void onStart() {
        config.executor()
                .execute(
                        () -> {
                            try {
                                var iter =
                                        Files.list(config.inputFolder().get()).sorted().iterator();
                                while (getServiceStatus() == Status.STARTED
                                        && !config.executor().isShutdown()
                                        && iter.hasNext()) {
                                    var imgFile = iter.next().toAbsolutePath().toString();
                                    LOGGER.info("Read frame {0}", imgFile);
                                    var colorMx = Imgcodecs.imread(imgFile);
                                    if (config.isShowFramesEnabled()) {
                                        HighGui.imshow("", colorMx);
                                        HighGui.waitKey();
                                    }
                                    var rgbFrame = new RgbImage(colorMx);
                                    config.rgbFrameConsumers().forEach(c -> c.accept(rgbFrame));
                                }

                            } catch (IOException e) {
                                LOGGER.severe("Error reading frames", e);
                            }
                        });
    }

    @Override
    protected void onClose() {
        if (config.isExecutorPrivate()) {
            var executor = config.executor();
            executor.shutdown();
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
