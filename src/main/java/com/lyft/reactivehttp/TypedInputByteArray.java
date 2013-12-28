package com.lyft.reactivehttp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Created by zakharov on 12/27/13.
 */
public class TypedInputByteArray implements TypedInput {
    private final String mimeType;
    private final byte[] bytes;

    /**
     * Constructs a new typed byte array.  Sets mimeType to {@code application/unknown} if absent.
     *
     * @throws NullPointerException if bytes are null
     */
    public TypedInputByteArray(String mimeType, byte[] bytes) {
        if (mimeType == null) {
            mimeType = "application/unknown";
        }
        if (bytes == null) {
            throw new NullPointerException("bytes");
        }
        this.mimeType = mimeType;
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
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
    public InputStream in() throws IOException {
        return new ByteArrayInputStream(bytes);
    }

}