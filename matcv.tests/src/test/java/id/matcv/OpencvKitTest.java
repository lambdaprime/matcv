package id.matcv;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class OpencvKitTest {

    @Test
    public void test_toFlatMatrix() {
        Mat out = OpencvKit.toFlatMatrix(List.of(
                new Scalar(1.0, 2.0, 3.0),
                new Scalar(3.0, 4.0, 5.0),
                new Scalar(6.0, 7.0, 8.0)));
        Assertions.assertEquals("[1, 2, 3, 0, 3, 4, 5, 0, 6, 7, 8, 0]", out.dump());
    }
}
