/*
 *
 *  * Copyright (C) 2012 Lyft, Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.lyft.reactivehttp;

import java.io.*;

/**
 * @author Alexey Zakharov
 */
public class FileTypedOutput implements TypedOutput {
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
