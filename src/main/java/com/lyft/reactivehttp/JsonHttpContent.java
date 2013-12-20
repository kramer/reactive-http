package com.lyft.reactivehttp;

/**
 * Created by zakharov on 12/15/13.
 */
class JsonHttpContent implements HttpContent {
    private final Object data;

    public JsonHttpContent(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String mimeType() {
        return "application/json; charset=UTF-8";
    }
}
