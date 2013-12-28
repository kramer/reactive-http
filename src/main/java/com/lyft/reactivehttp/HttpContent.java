package com.lyft.reactivehttp;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by zakharov on 12/15/13.
 */
interface HttpContent {
    String mimeType();

    long getLength() throws IOException;

    void writeTo(OutputStream out) throws IOException;
}
