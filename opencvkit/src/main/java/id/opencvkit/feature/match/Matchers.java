package id.opencvkit.feature.match;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.features2d.FlannBasedMatcher;

import id.opencvkit.MatUtils;
import id.opencvkit.feature.descriptor.FileDescriptor;

public class Matchers {

    private MatUtils matUtils = new MatUtils();
    
    /**
     * For every query descriptor it finds N best matches from a train descriptors
     */
    public List<MatchResult<Path>> matchKnn(List<FileDescriptor> queryDescriptors,
            List<FileDescriptor> trainDescriptors, int count) {
        var querySet = new Mat();
        Core.vconcat(matUtils.toListOfMat(queryDescriptors), querySet);

        var trainSet = new Mat();
        Core.vconcat(matUtils.toListOfMat(trainDescriptors), trainSet);

        System.out.format("querySet %s\n", querySet);
        System.out.format("trainSet %s\n", trainSet);
        
        var matcher = FlannBasedMatcher.create();

        var matches = new ArrayList<MatOfDMatch>();
        matcher.knnMatch(querySet, trainSet, matches, count);
        System.out.println(matches);

        return matches.stream().map(MatOfDMatch::toList).flatMap(List::stream)
                //.filter(match -> match.distance < THRESHOLD)
                .sorted(new DMatchComparator())
                .map(m -> new MatchResult<>(
                        queryDescriptors.get(m.queryIdx).getFile(),
                        trainDescriptors.get(m.trainIdx).getFile(),
                        m.distance))
                .collect(Collectors.toList());

    }

}
