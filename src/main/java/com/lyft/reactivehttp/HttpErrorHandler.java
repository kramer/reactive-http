package com.lyft.reactivehttp;

/**
 * Created by zakharov on 12/16/13.
 */
public interface HttpErrorHandler {
    Exception getError(int statusCode, String error);
}
