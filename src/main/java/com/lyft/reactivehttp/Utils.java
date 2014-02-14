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
public class Utils {
    private static final int BUFFER_SIZE = 0x1000;

    static HttpRequest cacheRequest(HttpRequest request) throws IOException {
        TypedOutput body = request.getBody();
        if (body == null) {
            return request;
        }

        String bodyMime = body.mimeType();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        body.writeTo(baos);
        body = new ByteArrayTypedOutput(bodyMime, baos.toByteArray());

        return new HttpRequest(request.getMethod(), request.getUrl(), request.getHeaders(), body);
    }

    static HttpResponse cacheResponse(HttpResponse response) throws IOException {
        TypedInput body = response.getBody();
        InputStream is = body.in();
        try {
            byte[] bodyBytes = Utils.streamToBytes(is);

            return new HttpResponse(response.getStatus(), new TypedInputByteArray(body.mimeType(), bodyBytes));
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    static byte[] streamToBytes(InputStream stream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (stream != null) {
            byte[] buf = new byte[BUFFER_SIZE];
            int r;
            while ((r = stream.read(buf)) != -1) {
                baos.write(buf, 0, r);
            }
        }
        return baos.toByteArray();
    }

    static String inputStreamToString(InputStream stream) throws IOException {
        InputStreamReader isr = new InputStreamReader(stream);

        BufferedReader bufferedReader = new BufferedReader(isr);

        StringBuilder result = new StringBuilder();

        String line;

        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }
}
