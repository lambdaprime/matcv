/**
 * <b>matcv</b> - Java module for Computer Vision which provides additional functionality and
 * extensions to <a href="https://github.com/opencv">OpenCV</a>.
 *
 * <h2>OpenCV setup</h2>
 *
 * <p>There are two popular distributions of OpenCV library for Java:
 *
 * <ul>
 *   <li>standard - provided for Windows/Android/iOS by <a href="https://opencv.org/">OpenCV</a>
 *       itself and for Linux by Canonical/Debian etc. This also known as non Maven distribution
 *       because you have to install OpenCV library manually and add it to the classpath.
 *   <li><a href="https://github.com/openpnp/opencv">OpenPnP</a> - this is Maven compatible
 *       distribution and is very convenient as you just need to declare dependency on OpenCV in
 *       Maven/Gradle build.
 * </ul>
 *
 * <p>The problem with OpenPnP distribution is that it does not include all features of standard
 * distribution. Example is <a
 * href="https://docs.opencv.org/4.2.0/javadoc/org/opencv/aruco/package-summary.html">aruco</a>
 * package and possibly more. For that reason <b>matcv</b> relies on standard distribution.
 *
 * <p>To install standard distribution of OpenCV in Linux:
 *
 * <pre>{@code
 * apt install libopencv-java
 * }</pre>
 *
 * <p>Note: Standard OpenCV distribution has different Java requirements:
 *
 * <ul>
 *   <li>opencv-460.jar requires Java 21+
 *   <li>opencv-454.jar requires Java 11+
 *   <li>...
 * </ul>
 *
 * <h2>Usage</h2>
 *
 * <p>1. Before using <b>matcv</b> make sure to load OpenCV native library:
 *
 * <pre>{@code
 * static {
 *     System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
 * }
 * }</pre>
 *
 * <p>2. Add path to libopencv-java using -Djava.library.path= (for Linux it is usually:
 * -Djava.library.path="/usr/lib/jni")
 *
 * <p>3. Add opencv.jar to module-path using -mp (for Linux it is usually: -mp
 * /usr/share/java/opencv.jar
 *
 * <p>Tested with OpenCV version 4.6.0
 *
 * @see <a
 *     href="https://github.com/lambdaprime/matcv/blob/main/matcv/release/CHANGELOG.md">Releases</a>
 * @see <a href="https://github.com/lambdaprime/matcv">Github</a>
 * @see <a href="https://opencv.org/">OpenCV</a>
 * @author lambdaprime intid@protonmail.com
 */
module matcv {
    requires id.xfunction;
    requires java.logging;
    requires transitive opencv;
    requires ejml.ddense;
    requires ejml.core;
    requires transitive ndbuffers;

    exports id.matcv;
    exports id.matcv.accessors;
    exports id.matcv.grid;
    exports id.matcv.feature.match;
    exports id.matcv.feature.detector;
    exports id.matcv.apps.slider;
    exports id.matcv.markers;
    exports id.matcv.types;
    exports id.matcv.types.camera;
    exports id.matcv.types.datatables;
    exports id.matcv.types.pointcloud;
}
