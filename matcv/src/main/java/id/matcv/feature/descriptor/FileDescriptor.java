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
package id.matcv.feature.descriptor;

import java.nio.file.Path;
import org.opencv.core.Mat;

/** Extends Mat with additional file information which this Mat descriptor describes. */
public class FileDescriptor extends Mat {

    private Path file;

    public FileDescriptor(Mat descriptor, Path file) {
        descriptor.assignTo(this);
        this.file = file;
    }

    public Path getFile() {
        return file;
    }
}
