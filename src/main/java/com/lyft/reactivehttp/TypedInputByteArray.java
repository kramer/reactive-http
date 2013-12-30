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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Alexey Zakharov
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