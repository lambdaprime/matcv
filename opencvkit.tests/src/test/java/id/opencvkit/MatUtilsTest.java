package id.opencvkit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import id.xfunction.XByte;

public class MatUtilsTest extends OpencvTest {

    private MatUtils utils = new MatUtils(); 
    
    @Test
    public void test_asMat() {
        var out = utils.asMat(XByte.castToByteArray(1,2,3));
        Assertions.assertEquals("[  1,   2,   3]", out.dump());
    }
    
}
