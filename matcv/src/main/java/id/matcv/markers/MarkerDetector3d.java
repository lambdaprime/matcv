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

import id.matcv.OpenCvKit;
import id.matcv.converters.ConvertersToNdBuffers;
import id.matcv.types.FileMat;
import id.matcv.types.KeyPoints3dTable;
import id.matcv.types.camera.CameraInfo;
import id.matcv.types.datatables.DataTable2;
import id.matcv.types.pointcloud.PointCloud;
import id.mathcat.NdBuffersMath;
import id.ndbuffers.NdBuffersFactory;
import id.xfunction.Preconditions;
import id.xfunction.logging.XLogger;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class MarkerDetector3d {
    private static final XLogger LOGGER = XLogger.getLogger(MarkerDetector3d.class);
    private NdBuffersFactory ndFactory = new NdBuffersFactory();
    private ConvertersToNdBuffers converters = new ConvertersToNdBuffers();
    private Marker2dUtils markerUtils = new Marker2dUtils();
    private Marker3dUtils marker3dUtils = new Marker3dUtils();
    private OpenCvKit cvKit = new OpenCvKit();
    private NdBuffersMath ndMath = new NdBuffersMath();
    private CameraInfo cameraInfo;
    private boolean showDetectedMarkers;
    private boolean isUndistortion;
    private CameraPoseEstimator cameraPoseEstimator;

    public MarkerDetector3d(CameraInfo cameraInfo) {
        this.cameraInfo = cameraInfo;
        this.cameraPoseEstimator = new CameraPoseEstimator(cameraInfo);
    }

    public MarkerDetector3d withUndistortion() {
        isUndistortion = true;
        return this;
    }

    public MarkerDetector3d withShowDetectedMarkers(boolean enabled) {
        this.showDetectedMarkers = enabled;
        return this;
    }

    /** Detect all {@link MarkerType} markers */
    public DataTable2<KeyPoints3dTable, List<MarkerLocation3d>> detectInFiles(
            DataTable2<Path, PointCloud> inputTable) {
        var imgs =
                inputTable.col1().stream()
                        .map(imgPath -> new FileMat(Imgcodecs.imread(imgPath.toString()), imgPath))
                        .toList();
        return detectInPointCloud(new DataTable2<>(imgs, inputTable.col2()));
    }

    /**
     * Detect all {@link MarkerType} markers
     *
     * @param inputTable RGB image, point cloud
     */
    public DataTable2<KeyPoints3dTable, List<MarkerLocation3d>> detectInPointCloud(
            DataTable2<? extends Mat, PointCloud> inputTable) {
        List<MarkerDetector2d.Result> detectorResults2d =
                runArucoMarkersDetector(inputTable.col1(), showDetectedMarkers);
        LOGGER.fine("detectorResults2d={0}", detectorResults2d);
        var w = cameraInfo.cameraIntrinsics().width();
        var h = cameraInfo.cameraIntrinsics().height();
        var col1 = new ArrayList<KeyPoints3dTable>();
        var col2 = new ArrayList<List<MarkerLocation3d>>();
        for (int i = 0; i < detectorResults2d.size(); i++) {
            var result = detectorResults2d.get(i);
            var pc = inputTable.col2(i);
            var pointHashesMap = new HashMap<Integer, Integer>();
            var locations = new ArrayList<MarkerLocation3d>();
            for (var ml : result.markersSortedByType()) {
                var mlt = ml.marker().type();
                Preconditions.equals(
                        5, ml.points().size(), "Each marker has 5 keypoints (center + 4 corners)");
                var centerId = converters.toIndex(ml.center(), w, h);
                var p1Id = converters.toIndex(ml.p1(), w, h);
                var p2Id = converters.toIndex(ml.p2(), w, h);
                var p3Id = converters.toIndex(ml.p3(), w, h);
                var p4Id = converters.toIndex(ml.p4(), w, h);
                pointHashesMap.put(centerId, mlt.centerHash());
                pointHashesMap.put(p1Id, mlt.p1Hash());
                pointHashesMap.put(p2Id, mlt.p2Hash());
                pointHashesMap.put(p3Id, mlt.p3Hash());
                pointHashesMap.put(p4Id, mlt.p4Hash());
                var center = pc.getPoint(centerId);
                if (center == PointCloud.HOLE) {
                    LOGGER.fine("Marker {0} has hole in point center and will be ignored", mlt);
                    continue;
                }
                var p1 = pc.getPoint(p1Id);
                if (p1 == PointCloud.HOLE) {
                    LOGGER.fine("Marker {0} has hole in point p1 and will be ignored", mlt);
                    continue;
                }
                var p2 = pc.getPoint(p2Id);
                if (p2 == PointCloud.HOLE) {
                    LOGGER.fine("Marker {0} has hole in point p2 and will be ignored", mlt);
                    continue;
                }
                var p3 = pc.getPoint(p3Id);
                if (p3 == PointCloud.HOLE) {
                    LOGGER.fine("Marker {0} has hole in point p3 and will be ignored", mlt);
                    continue;
                }
                var p4 = pc.getPoint(p4Id);
                if (p4 == PointCloud.HOLE) {
                    LOGGER.fine("Marker {0} has hole in point p4 and will be ignored", mlt);
                    continue;
                }
                var loc = new MarkerLocation3d(ml.marker(), center, p1, p2, p3, p4, ml.corners());
                if (marker3dUtils.hasVaildPoints(loc)) locations.add(loc);
                else LOGGER.fine("Marker has invalid points and will be ignored: {0}", loc);
            }
            if (result.img() instanceof FileMat fm)
                LOGGER.fine(
                        "Found {0} keypoints on image {1}", pointHashesMap.size(), fm.getFile());
            var pointHashes =
                    pointHashesMap.entrySet().stream()
                            .sorted(Comparator.comparing(Entry::getKey))
                            .toList();
            col1.add(
                    new KeyPoints3dTable(
                            pointHashes.stream().map(Entry::getKey).toList(),
                            pointHashes.stream().map(Entry::getValue).toList()));
            col2.add(locations);
        }
        var ret = new DataTable2<>(col1, col2);
        LOGGER.fine("detectorResults3d={0}", ret);
        return ret;
    }

    /**
     * Detect all {@link MarkerType} markers
     *
     * @param input RGB image
     */
    public List<List<MarkerLocation3d>> detect(List<? extends Mat> input) {
        List<MarkerDetector2d.Result> detectorResults2d =
                runArucoMarkersDetector(input, showDetectedMarkers);
        LOGGER.fine("detectorResults2d={0}", detectorResults2d);
        var ret = new ArrayList<List<MarkerLocation3d>>();
        for (int i = 0; i < detectorResults2d.size(); i++) {
            var result = detectorResults2d.get(i);
            var locations = new ArrayList<MarkerLocation3d>();
            for (var ml : result.markersSortedByType()) {
                var tx = cameraPoseEstimator.estimate(ml).get();
                var points3d = ndFactory.matrixN3d(5);
                // first point is center point at [0, 0]
                ml.marker().create3dModel(0.001).copyTo(points3d, 1, 0);
                points3d = ndMath.transform(points3d, tx);
                var loc =
                        new MarkerLocation3d(ml.marker(), points3d, ml.corners(), Optional.empty());
                locations.add(loc);
            }
            if (result.img() instanceof FileMat fm)
                LOGGER.fine("Found {0} markers on image {1}", ret.size(), fm.getFile());
            ret.add(locations);
        }
        LOGGER.fine("detectorResults3d={0}", ret);
        return ret;
    }

    private List<MarkerDetector2d.Result> runArucoMarkersDetector(
            List<? extends Mat> rgbImages, boolean showDetectedMarkers) {
        List<MarkerDetector2d.Result> results = new ArrayList<MarkerDetector2d.Result>();
        var detector = new MarkerDetector2d();
        if (isUndistortion) detector = detector.withUndistortion(cameraInfo);
        for (var img : rgbImages) {
            var result = detector.detect(img);
            results.add(result);
            if (showDetectedMarkers) {
                result.markersSortedByType().forEach(ml -> markerUtils.drawMarker(img, ml));
                cvKit.show(img, true);
            }
        }
        return results;
    }
}
