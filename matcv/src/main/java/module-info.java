/**
 * <b>matcv</b> - Java module which provides different functionality for Computer Vision and
 * extensions to <a href="https://github.com/opencv">OpenCV</a>.
 *
 * <h1>OpenCV setup</h1>
 *
 * <p>There are two popular distributions of OpenCV library for Java:
 *
 * <ul>
 *   <li>standard - provided for Windows/Android/iOS by <a href="https://opencv.org/">OpenCV</a>
 *       itself and for Linux but Canonical/Debian etc. This also known as non Maven distribution
 *       because you have to download OpenCV library manually and install it to classpath.
 *   <li><a href="https://github.com/openpnp/opencv">OpenPnP</a> - this is Maven compatible
 *       distribution and is very convenient as you just need to declare dependency on OpenCV in
 *       Maven/Gradle build.
 * </ul>
 *
 * <p>The problem with OpenPnP distribution is that it does not include all features of standard
 * distribution. Example is <a
 * href="https://docs.opencv.org/4.2.0/javadoc/org/opencv/aruco/package-summary.html">aruco</a>
 * package and possibly more. For that reason <b>matcv</b> relies on standard distribution:
 *
 * <p>Linux:
 *
 * <pre>{@code
 * apt install libopencv4.2-java
 * }</pre>
 *
 * <p>Before using <b>matcv</b> make sure to load OpenCV native library:
 *
 * <pre>{@code
 * static {
 *     System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
 * }
 * }</pre>
 *
 * <p>And add path to libopencv4.2-java using -Djava.library.path= (for Linux it is usually:
 * -Djava.library.path="/usr/lib/jni")
 *
 * @see <a href="https://github.com/lambdaprime/matcv">Download matcv</a>
 * @see <a href="https://github.com/lambdaprime/matcv">Github</a>
 * @see <a href="https://opencv.org/">OpenCV</a>
 * @author lambdaprime intid@protonmail.com
 */
module id.matcv {
    requires id.xfunction;
    requires java.logging;
    requires transitive opencv;
    requires transitive commons.math3;

    exports id.matcv;
    exports id.matcv.accessors;
    exports id.matcv.grid;
    exports id.matcv.feature.match;
    exports id.matcv.camera;
    exports id.matcv.feature.detector;
    exports id.matcv.apps.slider;
}
