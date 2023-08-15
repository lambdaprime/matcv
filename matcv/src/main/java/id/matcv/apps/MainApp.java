/*
 * Copyright 2023 matcv project
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
package id.matcv.apps;

import id.matcv.apps.markers.DetectMarkersApp;
import id.matcv.apps.markers.GenerateMarkerApp;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MainApp {

    private static final String DETECT_MARKERS = "detect-markers";
    private static final String GENERATE_MARKERS = "generate-markers";

    private static void usage() {
        System.out.println(
                """
                **matcv** - Java module which provides different functionality for Computer Vision and
                extensions to [OpenCV](https://github.com/opencv).

                Website: https://github.com/lambdaprime/matcv
                Documentation: http://portal2.atwebpages.com/matcv

                Supported commands: %s
                """
                        .formatted(List.of(DETECT_MARKERS, GENERATE_MARKERS)));
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            usage();
            System.exit(1);
        }
        var cmdArgs = Arrays.stream(args).skip(1).toArray(i -> new String[i]);
        switch (args[0]) {
            case DETECT_MARKERS -> DetectMarkersApp.main(cmdArgs);
            case GENERATE_MARKERS -> GenerateMarkerApp.main(cmdArgs);
            default -> System.err.println(
                    """
                Unknown command %s

                Run command without arguments to see 'Usage' for more information.
                """
                            .formatted(args[0]));
        }
    }
}
