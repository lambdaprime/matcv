package id.matcv.feature.match;

import id.matcv.MatUtils;
import id.matcv.feature.descriptor.FileDescriptor;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.features2d.FlannBasedMatcher;

public class Matchers {

    private MatUtils matUtils = new MatUtils();
    
    public interface MatchFunc {
        List<MatOfDMatch> match(Mat querySet, Mat trainSet);
    }
    
    /**
     * For every query descriptor it finds all train descriptors with a given distance
     */
    public List<MatchResult<Path>> matchRadius(List<FileDescriptor> queryDescriptors,
            List<FileDescriptor> trainDescriptors, int distance) {
        return match(queryDescriptors, trainDescriptors, (querySet, trainSet) -> {
            var matcher = FlannBasedMatcher.create();

            var matches = new ArrayList<MatOfDMatch>();
            matcher.radiusMatch(querySet, trainSet, matches, distance);
            return matches;
        });        
    }
    
    /**
     * For every query descriptor it finds N best matches from a train descriptors
     */
    public List<MatchResult<Path>> matchKnn(List<FileDescriptor> queryDescriptors,
            List<FileDescriptor> trainDescriptors, int count) {
        return match(queryDescriptors, trainDescriptors, (querySet, trainSet) -> {
            var matcher = FlannBasedMatcher.create();

            var matches = new ArrayList<MatOfDMatch>();
            matcher.knnMatch(querySet, trainSet, matches, count);
            return matches;
        });
    }

    private List<MatchResult<Path>> match(List<FileDescriptor> queryDescriptors,
            List<FileDescriptor> trainDescriptors, MatchFunc func) {
        var querySet = new Mat();
        Core.vconcat(matUtils.toListOfMat(queryDescriptors), querySet);

        var trainSet = new Mat();
        Core.vconcat(matUtils.toListOfMat(trainDescriptors), trainSet);

//        System.out.format("querySet %s\n", querySet);
//        System.out.format("trainSet %s\n", trainSet);
        
        var matches = func.match(querySet, trainSet);
//        System.out.println(matches);

        return matches.stream().map(MatOfDMatch::toList).flatMap(List::stream)
                .map(m -> new MatchResult<>(
                        queryDescriptors.get(m.queryIdx).getFile(),
                        trainDescriptors.get(m.trainIdx).getFile(),
                        m.distance))
                .collect(Collectors.toSet()).stream()
                .sorted(MatchResult.compareByDistance())
                .collect(Collectors.toList());
    }
}
