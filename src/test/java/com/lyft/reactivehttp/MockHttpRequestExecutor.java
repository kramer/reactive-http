package com.lyft.reactivehttp;

import com.lyft.reactivehttp.HttpContent;
import com.lyft.reactivehttp.RequestExecutor;
import rx.Observable;

import java.util.Map;

/**
 * Created by zakharov on 12/15/13.
 */
public class MockHttpRequestExecutor implements RequestExecutor {
    String method;
    String url;
    Map<String, String> headers;
    HttpContent httpContent;
    Class<?> responseClass;

    @Override
    public <T> Observable<T> execute(
            String method,
            String url,
            Map<String, String> headers,
            HttpContent httpContent,
            Class<T> responseClass) {
        this.method = method;
        this.url = url;
        this.headers = headers;
        this.httpContent = httpContent;
        this.responseClass = responseClass;

        return null;
    }
}
