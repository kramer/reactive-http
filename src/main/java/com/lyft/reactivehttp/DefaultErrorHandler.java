package com.lyft.reactivehttp;

/**
 * Created by zakharov on 12/16/13.
 */
public class DefaultErrorHandler implements HttpErrorHandler {
    @Override
    public Exception getError(int statusCode, String error) {
        return new RestException(statusCode, error);
    }
}
