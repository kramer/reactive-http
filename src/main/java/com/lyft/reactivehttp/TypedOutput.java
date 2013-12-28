package com.lyft.reactivehttp;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by zakharov on 12/15/13.
 */
interface TypedOutput {
    String mimeType();

    long length();

    void writeTo(OutputStream out) throws IOException;
}
