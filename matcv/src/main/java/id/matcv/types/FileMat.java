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
package id.matcv.types;

import java.nio.file.Path;
import org.opencv.core.Core;
import org.opencv.core.Mat;

/**
 * Extends {@link Mat} with file information.
 *
 * <p>This Mat keeps reference to a file which it is associated to.
 *
 * <p>Instead of extending Mat it could just be a record of (Mat mat, Path file). But it would mean
 * that, if you have a List of such objects (List&lt;FileMat>) and want to pass them to a function
 * which accepts just List&ltMat> (ex. {@link Core#vconcat(java.util.List, Mat)}), you would need to
 * transform List&lt;FileMat> to List&ltMat> by decoupling all mat() fields into separate
 * List&ltMat>. By extending {@link Mat} on the other hand, this is not required and you can pass
 * List&lt;FileMat> as-is to any functions which accept List&ltMat>.
 */
public class FileMat extends Mat {

    private Path file;

    public FileMat(Mat mat, Path file) {
        mat.assignTo(this);
        this.file = file;
    }

    public Path getFile() {
        return file;
    }
}
