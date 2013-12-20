package com.lyft.reactivehttp;

import java.io.File;

/**
 * Created by zakharov on 12/15/13.
 */
class FileContent implements HttpContent {

    private String contentType; // like "image/jpeg"
    private File file;

    public FileContent(String contentType, File file) {
        this.contentType = contentType;
        this.file = file;
    }

    public File getFile() {
        return file;
    }


    @Override
    public String mimeType() {
        return contentType;
    }
}
