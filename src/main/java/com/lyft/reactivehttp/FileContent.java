package com.lyft.reactivehttp;

import java.io.File;

/**
 * Created by zakharov on 12/15/13.
 */
class FileContent implements HttpContent {
    private File file;

    public FileContent(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}
