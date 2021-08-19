package id.opencvkit.feature.match;

import java.util.Comparator;

import org.opencv.core.DMatch;

/**
 * Compares two DMatch results and returns the one which has smaller
 * distance to the query
 */
public class DMatchComparator implements Comparator<DMatch> {

    @Override
    public int compare(DMatch arg0, DMatch arg1) {
        return arg0.lessThan(arg1)? -1: 1;
    }

}
