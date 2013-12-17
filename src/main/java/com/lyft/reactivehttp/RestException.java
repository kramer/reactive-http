package com.lyft.reactivehttp;

/**
 * Created by zakharov on 12/16/13.
 */
public class RestException extends RuntimeException {
    int statusCode;
    String error;

    public RestException(int statusCode) {
        this.statusCode = statusCode;
    }

    public RestException(int statusCode, String error) {
        this.statusCode = statusCode;
        this.error = error;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getError() {
        return error;
    }
}
