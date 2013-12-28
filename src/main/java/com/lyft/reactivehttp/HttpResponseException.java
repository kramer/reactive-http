package com.lyft.reactivehttp;

import com.google.gson.Gson;

import java.io.IOException;

/**
 * Created by zakharov on 12/16/13.
 */
public class HttpResponseException extends IOException {
    int statusCode;
    String error;
    private Gson gson;

    public HttpResponseException(int statusCode, String error, Gson gson) {
        this.statusCode = statusCode;
        this.error = error;
        this.gson = gson;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getError() {
        return error;
    }

    public <T> T getError(Class<T> clazz) {
        try {
            return gson.fromJson(error, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isServerError() {
        return statusCode / 100 == 5;
    }
}
