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

import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

class JsonTypedOutput implements TypedOutput {
    private final Object data;
    private Gson gson;
    private byte[] jsonBytes;

    public JsonTypedOutput(Object data, Gson gson) {
        this.data = data;
        this.gson = gson;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String mimeType() {
        return "application/json; charset=UTF-8";
    }

    @Override
    public long length() {
        try {
            return getJsonBytes().length;
        } catch (UnsupportedEncodingException e) {
            return -1;
        }
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        out.write(getJsonBytes());
    }

    private byte[] getJsonBytes() throws UnsupportedEncodingException {
        if (jsonBytes == null) {
            jsonBytes = gson.toJson(data).getBytes("UTF-8");
        }
        return jsonBytes;
    }
}
