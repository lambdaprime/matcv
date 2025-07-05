/*
 * Copyright 2022 matcv project
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
package id.matcv.feature.match;

import id.matcv.converters.ConvertersToOpenCv;
import id.matcv.types.FileMat;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.features2d.FlannBasedMatcher;

public class Matchers {

    private ConvertersToOpenCv converters = new ConvertersToOpenCv();

    @FunctionalInterface
    private interface MatchFunc {
        List<MatOfDMatch> match(Mat querySet, Mat trainSet);
    }

    /** For every query descriptor it finds all train descriptors with a given distance */
    public List<MatchResult<Path>> matchRadius(
            List<FileMat> queryDescriptors, List<FileMat> trainDescriptors, int distance) {
        return match(
                queryDescriptors,
                trainDescriptors,
                (querySet, trainSet) -> {
                    var matcher = FlannBasedMatcher.create();

                    var matches = new ArrayList<MatOfDMatch>();
                    matcher.radiusMatch(querySet, trainSet, matches, distance);
                    return matches;
                });
    }

    /** For every query descriptor it finds N best matches from a train descriptors */
    public List<MatchResult<Path>> matchKnn(
            List<FileMat> queryDescriptors, List<FileMat> trainDescriptors, int count) {
        return match(
                queryDescriptors,
                trainDescriptors,
                (querySet, trainSet) -> {
                    var matcher = FlannBasedMatcher.create();

                    var matches = new ArrayList<MatOfDMatch>();
                    matcher.knnMatch(querySet, trainSet, matches, count);
                    return matches;
                });
    }

    private List<MatchResult<Path>> match(
            List<FileMat> queryDescriptors, List<FileMat> trainDescriptors, MatchFunc func) {
        var querySet = new Mat();

        // vconcat takes list of vectors:
        // [1, 1, 1]
        // [2, 2, 2]
        // and concats then to single matrix
        // [1, 1,
        //  2, 2]
        Core.vconcat(converters.mapToListOfMat(queryDescriptors), querySet);

        var trainSet = new Mat();
        Core.vconcat(converters.mapToListOfMat(trainDescriptors), trainSet);

        var matches = func.match(querySet, trainSet);

        return matches.stream()
                .map(MatOfDMatch::toList)
                .flatMap(List::stream)
                .map(
                        m ->
                                new MatchResult<>(
                                        queryDescriptors.get(m.queryIdx).getFile(),
                                        trainDescriptors.get(m.trainIdx).getFile(),
                                        m.distance))
                .collect(Collectors.toSet())
                .stream()
                .sorted(MatchResult.compareByDistance())
                .collect(Collectors.toList());
    }
}
