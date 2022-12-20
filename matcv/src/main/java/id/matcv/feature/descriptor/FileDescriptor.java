package id.matcv.feature.descriptor;

import java.nio.file.Path;

import org.opencv.core.Mat;

/**
 * Extends Mat with additional file information which this Mat
 * descriptor describes.
 */
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
