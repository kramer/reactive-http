package com.lyft.reactivehttp;

import rx.Observable;

/**
 * Created by zakharov on 12/15/13.
 */
public class MockHttpRequestExecutor implements RequestExecutor {
    HttpRequest httpRequest;

    @Override
    public <T> Observable<T> execute(HttpRequest httpRequest, Class<T> clazz) {
        this.httpRequest = httpRequest;
        return null;
    }
}
