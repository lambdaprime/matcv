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

import id.jrealsense.Config;
import id.jrealsense.Context;
import id.jrealsense.Filter;
import id.jrealsense.FormatType;
import id.jrealsense.FrameSet;
import id.jrealsense.Pipeline;
import id.jrealsense.StreamType;
import id.jrealsense.devices.DeviceLocator;
import id.jrealsense.filters.SpatialFilter;
import id.jrealsense.frames.DepthFrame;
import id.jrealsense.frames.Frame;
import id.jrealsense.utils.FrameUtils;
import id.matcv.types.camera.CameraInfoPredefined;
import id.xfunction.Preconditions;
import id.xfunction.lang.XThread;
import id.xfunction.logging.XLogger;
import id.xfunction.util.IdempotentService;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class RealSenseCamera extends IdempotentService {
    private static final XLogger LOGGER = XLogger.getLogger(RealSenseCamera.class);
    private static final FrameUtils utils = new FrameUtils();

    /** Frames per second */
    private static final int FPS = 30;

    private CameraConfiguration cameraConfig;

    public RealSenseCamera() {
        this(new CameraConfigurationBuilder().build());
    }

    public RealSenseCamera(CameraConfiguration config) {
        this.cameraConfig = config;
    }

    /** Setup resources and run the looper */
    @Override
    protected void onStart() {
        cameraConfig
                .executor()
                .execute(
                        () -> {
                            // using try-with-resources to properly release all librealsense
                            // resources
                            try (var ctx = Context.create();
                                    var pipeline = Pipeline.create(ctx);
                                    var config = Config.create(ctx);
                                    var locator = DeviceLocator.create(ctx)) {
                                if (locator.getAllDevices().isEmpty()) {
                                    System.err.println("No devices found");
                                    return;
                                }
                                var dev = locator.getDevice(0);
                                LOGGER.info("Detected camera device: {0}", dev);
                                LOGGER.info("Reset camera hardware...");
                                dev.reset();
                                XThread.sleep(5000);
                                var intrinsics = cameraConfig.intrinsics();
                                config.enableStream(
                                        StreamType.RS2_STREAM_COLOR,
                                        0,
                                        intrinsics.width(),
                                        intrinsics.height(),
                                        FormatType.RS2_FORMAT_BGR8,
                                        FPS);
                                var rgbdFrameConsumers = cameraConfig.rgbdFrameConsumers();
                                if (!rgbdFrameConsumers.isEmpty()) {
                                    config.enableStream(
                                            StreamType.RS2_STREAM_DEPTH,
                                            0,
                                            intrinsics.width(),
                                            intrinsics.height(),
                                            FormatType.RS2_FORMAT_Z16,
                                            FPS);
                                }
                                pipeline.start(config);
                                var depthFilters = new ArrayList<Filter<DepthFrame, DepthFrame>>();
                                if (cameraConfig.isSpatialFilterEnabled()) {
                                    Preconditions.isTrue(
                                            rgbdFrameConsumers.isEmpty(),
                                            "No RGBD consumers defined");
                                    depthFilters.add(SpatialFilter.create());
                                }
                                loop(pipeline, depthFilters);
                                depthFilters.forEach(Filter::close);
                            }
                        });
    }

    @Override
    protected void onClose() {
        if (cameraConfig.isExecutorPrivate()) {
            var executor = cameraConfig.executor();
            executor.shutdown();
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void proc(Frame<?> colorFrame, Optional<Frame<?>> depthFrame) {
        LOGGER.fine(
                "Processing pair of frames: color frame {0}, depth frame {1}",
                colorFrame.getFrameNumber(), depthFrame.map(Frame::getFrameNumber));
        var colorMx =
                new Mat(
                        cameraConfig.intrinsics().height(),
                        cameraConfig.intrinsics().width(),
                        CvType.CV_8UC3,
                        colorFrame.getDataAsByteBuffer());
        if (cameraConfig.isShowFramesEnabled()) {
            HighGui.imshow("", colorMx);
            HighGui.waitKey();
        }
        cameraConfig
                .outputFolder()
                .ifPresent(
                        output -> {
                            Path p = output.resolve("frame" + System.currentTimeMillis() + ".png");
                            Imgcodecs.imwrite(
                                    p.toString(),
                                    colorMx,
                                    new MatOfInt(Imgcodecs.IMWRITE_PNG_COMPRESSION));
                        });
        var rgbFrame = new RgbImage(colorMx);
        cameraConfig.rgbFrameConsumers().forEach(c -> c.accept(rgbFrame));
        if (depthFrame.isPresent()) {
            var depthMx =
                    new Mat(
                            cameraConfig.intrinsics().height(),
                            cameraConfig.intrinsics().width(),
                            CvType.CV_16UC1,
                            depthFrame.get().getDataAsByteBuffer());
            var rgbdFrame = new RgbdImage(colorMx, depthMx);
            cameraConfig.rgbdFrameConsumers().forEach(c -> c.accept(rgbdFrame));
        }
    }

    /** Loop over the frames in the pipeline */
    private void loop(Pipeline pipeline, List<Filter<DepthFrame, DepthFrame>> depthFilters) {
        while (getServiceStatus() == Status.STARTED && !cameraConfig.executor().isShutdown()) {
            FrameSet frameSet = pipeline.waitForFrames();
            if (frameSet.size() == 0) continue;
            frameSet = utils.alignToColorStream(frameSet);
            var colorFrameOpt = frameSet.getColorFrame(FormatType.RS2_FORMAT_BGR8);
            var depthFrameOpt = Optional.<Frame<?>>empty();
            if (!cameraConfig.rgbdFrameConsumers().isEmpty())
                depthFrameOpt =
                        frameSet.getDepthFrame().map(frame -> applyfilters(depthFilters, frame));
            if (colorFrameOpt.isPresent()) {
                proc(colorFrameOpt.get(), depthFrameOpt);
            }
            frameSet.close();
        }
    }

    private DepthFrame applyfilters(
            List<Filter<DepthFrame, DepthFrame>> filters, DepthFrame frame) {
        for (var filter : filters) frame = filter.process(frame);
        return frame;
    }

    public static void main(String[] args) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        XLogger.load("logging-matcv-debug.properties");
        var cameraInfo = CameraInfoPredefined.REALSENSE_D435i_640_480.getCameraInfo();
        try (var camera =
                new RealSenseCamera(
                        new CameraConfigurationBuilder()
                                .addRgbdFrameConsumer(
                                        new RgbdToMarker3dTransformer(
                                                cameraInfo,
                                                markers ->
                                                        LOGGER.info(
                                                                "Markers detected: {0}", markers)))
                                .withIntrinsics(cameraInfo.cameraIntrinsics())
                                .enableShowFrames(true)
                                .build())) {
            camera.start();
            System.out.println("Press Enter to stop...");
            System.in.read();
        }
    }
}
