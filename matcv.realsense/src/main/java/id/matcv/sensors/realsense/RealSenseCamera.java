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
import id.jrealsense.FormatType;
import id.jrealsense.FrameSet;
import id.jrealsense.Pipeline;
import id.jrealsense.StreamType;
import id.jrealsense.devices.DeviceLocator;
import id.jrealsense.frames.Frame;
import id.jrealsense.utils.FrameUtils;
import id.matcv.types.camera.CameraIntrinsics;
import id.matcv.types.camera.CameraIntrinsicsPredefined;
import id.xfunction.lang.XThread;
import id.xfunction.logging.XLogger;
import id.xfunction.util.IdempotentService;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class RealSenseCamera extends IdempotentService {
    private static final XLogger LOGGER = XLogger.getLogger(RealSenseCamera.class);
    private static final FrameUtils utils = new FrameUtils();

    /** Frames per second */
    private static final int FPS = 30;

    private Consumer<RgbdImage> imageConsumer = rgbd -> {};
    private CameraIntrinsics intrinsics =
            CameraIntrinsicsPredefined.REALSENSE_D435i_640_480.getCameraIntrinsics();
    private boolean isShowFramesEnabled;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private boolean isExecutorPrivate = true;

    public RealSenseCamera withExecutor(ExecutorService executor) {
        this.executor = executor;
        isExecutorPrivate = false;
        return this;
    }

    public RealSenseCamera withImageConsumer(Consumer<RgbdImage> imageConsumer) {
        this.imageConsumer = imageConsumer;
        return this;
    }

    public RealSenseCamera withCameraIntrinsics(CameraIntrinsics intrinsics) {
        this.intrinsics = intrinsics;
        return this;
    }

    public RealSenseCamera withShowFrames(boolean isEnabled) {
        this.isShowFramesEnabled = isEnabled;
        return this;
    }

    /** Setup resources and run the looper */
    @Override
    protected void onStart() {
        executor.execute(
                () -> {
                    // using try-with-resources to properly release all librealsense resources
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
                        config.enableStream(
                                StreamType.RS2_STREAM_DEPTH,
                                0,
                                intrinsics.width(),
                                intrinsics.height(),
                                FormatType.RS2_FORMAT_Z16,
                                FPS);
                        config.enableStream(
                                StreamType.RS2_STREAM_COLOR,
                                0,
                                intrinsics.width(),
                                intrinsics.height(),
                                FormatType.RS2_FORMAT_BGR8,
                                FPS);
                        pipeline.start(config);
                        loop(pipeline);
                    }
                });
    }

    @Override
    protected void onClose() {
        if (isExecutorPrivate) {
            executor.shutdown();
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void proc(Frame<?> colorFrame, Frame<?> depthFrame) {
        LOGGER.fine(
                "Processing pair of frames: color frame {0}, depth frame {1}",
                colorFrame.getFrameNumber(), depthFrame.getFrameNumber());
        var colorMx =
                new Mat(
                        intrinsics.height(),
                        intrinsics.width(),
                        CvType.CV_8UC3,
                        colorFrame.getDataAsByteBuffer());
        if (isShowFramesEnabled) {
            HighGui.imshow("", colorMx);
            HighGui.waitKey();
        }
        var depthMx =
                new Mat(
                        intrinsics.height(),
                        intrinsics.width(),
                        CvType.CV_16UC1,
                        depthFrame.getDataAsByteBuffer());
        imageConsumer.accept(new RgbdImage(colorMx, depthMx));
    }

    /** Loop over the frames in the pipeline */
    private void loop(Pipeline pipeline) {
        while (getServiceStatus() == Status.STARTED && !executor.isShutdown()) {
            FrameSet frameSet = pipeline.waitForFrames();
            if (frameSet.size() == 0) continue;
            frameSet = utils.alignToColorStream(frameSet);
            var colorFrameOpt = frameSet.getColorFrame(FormatType.RS2_FORMAT_BGR8);
            var depthFrameOpt = frameSet.getDepthFrame();
            if (colorFrameOpt.isPresent() && depthFrameOpt.isPresent()) {
                proc(colorFrameOpt.get(), depthFrameOpt.get());
            }
            frameSet.close();
        }
    }

    public static void main(String[] args) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        // XLogger.load("logging-matcv-debug.properties");
        var intrinsics = CameraIntrinsicsPredefined.REALSENSE_D435i_640_480.getCameraIntrinsics();
        try (var camera = new RealSenseCamera()) {
            camera.withImageConsumer(
                            new RgbdToMarker3dTransformer(
                                    intrinsics,
                                    markers -> LOGGER.info("Markers detected: {0}", markers)))
                    .withCameraIntrinsics(intrinsics)
                    .withShowFrames(false)
                    .start();
            System.out.println("Press Enter to stop...");
            System.in.read();
        }
    }
}
