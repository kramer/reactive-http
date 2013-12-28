package com.lyft.reactivehttp;

import java.io.InputStream;

/**
 * Created by zakharov on 12/27/13.
 */
public class HttpResponse {
    TypedInput body;
    int statusCode;

    public HttpResponse(int statusCode, TypedInput body) {
        this.body = body;
        this.statusCode = statusCode;
    }

    public TypedInput getBody() {
        return body;
    }

    public int getStatus() {
        return statusCode;
    }

    public boolean isSuccess() {
        return  (statusCode >= 200 && statusCode < 300);
    }
}
