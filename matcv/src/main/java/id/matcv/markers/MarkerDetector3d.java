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

import id.matcv.converters.PointConverters;
import id.matcv.types.FileMat;
import id.matcv.types.KeyPoints3dTable;
import id.matcv.types.camera.CameraIntrinsics;
import id.matcv.types.datatables.DataTable2;
import id.matcv.types.pointcloud.PointCloud;
import id.xfunction.Preconditions;
import id.xfunction.logging.XLogger;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class MarkerDetector3d {
    private static final XLogger LOGGER = XLogger.getLogger(MarkerDetector3d.class);
    private PointConverters converters = new PointConverters();
    private Marker2dUtils markerUtils = new Marker2dUtils();
    private Marker3dUtils marker3dUtils = new Marker3dUtils();
    private CameraIntrinsics intrinsics;
    private boolean showDetectedMarkers;

    public MarkerDetector3d(CameraIntrinsics intrinsics) {
        this.intrinsics = intrinsics;
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
        return detect(new DataTable2<>(imgs, inputTable.col2()));
    }

    /**
     * Detect all {@link MarkerType} markers
     *
     * @param inputTable RGB image, point cloud
     */
    public DataTable2<KeyPoints3dTable, List<MarkerLocation3d>> detect(
            DataTable2<? extends Mat, PointCloud> inputTable) {
        List<MarkerDetector2d.Result> detectorResults2d =
                runArucoMarkersDetector(inputTable.col1(), showDetectedMarkers);
        LOGGER.fine("detectorResults2d={0}", detectorResults2d);
        var w = intrinsics.width();
        var h = intrinsics.height();
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
                var loc = new MarkerLocation3d(ml.marker(), center, p1, p2, p3, p4);
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

    private List<MarkerDetector2d.Result> runArucoMarkersDetector(
            List<? extends Mat> rgbImages, boolean showDetectedMarkers) {
        List<MarkerDetector2d.Result> results = new ArrayList<MarkerDetector2d.Result>();
        Function<Mat, String> titleExtractor =
                mat ->
                        switch (mat) {
                            case FileMat fmat -> fmat.getFile().getFileName().toString();
                            default -> "";
                        };
        for (var img : rgbImages) {
            var result = new MarkerDetector2d().detect(img);
            results.add(result);
            if (showDetectedMarkers) {
                result.markersSortedByType().forEach(ml -> markerUtils.drawMarker(img, ml));
                HighGui.imshow(titleExtractor.apply(img), img);
                HighGui.waitKey();
            }
        }
        return results;
    }
}
