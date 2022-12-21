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
package id.matcv;

import id.xfunction.ResourceUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class MatUtils {

    private ResourceUtils resourceUtils = new ResourceUtils();

    /** Converts byte array to CV_8U matrix */
    public Mat asMat(byte... values) {
        var mat = new Mat(1, values.length, CvType.CV_8U);
        mat.put(0, 0, values);
        return mat;
    }

    public Mat readImageFromResource(String absoluteResourcePath) throws IOException {
        Path out = Files.createTempFile("resource", "");
        resourceUtils.extractResource(absoluteResourcePath, out);
        return Imgcodecs.imread(out.toAbsolutePath().toString());
    }

    public List<Mat> toListOfMat(List<? extends Mat> list) {
        return (List<Mat>) list;
    }
}
