/**
 * <b>matcv</b> - Java module which provides different functionality for Computer Vision and
 * extensions to <a href="https://github.com/opencv">OpenCV</a>.
 *
 * @see <a href="https://github.com/lambdaprime/matcv">Download matcv</a>
 * @see <a href="https://github.com/lambdaprime/matcv">Github</a>
 * @author lambdaprime intid@protonmail.com
 */
module id.matcv {
    requires id.xfunction;
    requires java.logging;
    requires opencv;

    exports id.mathcalc;
    exports id.matcv;
    exports id.matcv.accessors;
    exports id.matcv.grid;
    exports id.matcv.feature.match;
    exports id.matcv.feature.descriptor;
    exports id.matcv.camera;
    exports id.matcv.feature.detector;
    exports id.matcv.apps.slider;
}
