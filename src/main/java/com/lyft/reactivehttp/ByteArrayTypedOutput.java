package com.lyft.reactivehttp;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by zakharov on 12/27/13.
 */
public class ByteArrayTypedOutput implements TypedOutput {

    private final String mimeType;
    private byte[] bytes;

    public ByteArrayTypedOutput(String mimeType, byte[] bytes) {
        this.bytes = bytes;
        this.mimeType = mimeType;
    }

    @Override
    public String mimeType() {
        return mimeType;
    }

    @Override
    public long length() {
        return bytes.length;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        out.write(bytes);
    }

    public byte[] getBytes() {
        return bytes;
    }
}
