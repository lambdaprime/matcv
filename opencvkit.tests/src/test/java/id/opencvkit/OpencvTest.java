package id.opencvkit;

import org.opencv.core.Core;

public class OpencvTest {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
}
