package com.lyft.reactivehttp;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zakharov on 12/27/13.
 */
public class HttpResponse {
    TypedInput body;
    int statusCode;

    final Map<String, String> headers = new LinkedHashMap<String, String>();

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

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
