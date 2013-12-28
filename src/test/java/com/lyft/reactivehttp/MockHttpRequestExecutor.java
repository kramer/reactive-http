package com.lyft.reactivehttp;

import com.lyft.reactivehttp.HttpContent;
import com.lyft.reactivehttp.RequestExecutor;
import rx.Observable;

import java.util.Map;

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
