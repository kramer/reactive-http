package com.lyft.reactivehttp;

import java.io.*;

/**
 * Created by zakharov on 12/15/13.
 */
class FileTypedOutput implements TypedOutput {
    private static final int BUFFER_SIZE = 0x1000;

    private String contentType; // like "image/jpeg"
    private File file;

    public FileTypedOutput(String contentType, File file) {
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

    @Override
    public long length() {
        return file.length();
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        final byte[] buffer = new byte[BUFFER_SIZE];
        int read;

        InputStream input = new BufferedInputStream(new FileInputStream(file));

        try {
            while ((read = input.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            input.close();
        }
    }
}
