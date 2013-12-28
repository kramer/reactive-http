package com.lyft.reactivehttp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zakharov on 12/27/13.
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

        request.body(body);

        return request;
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
}
