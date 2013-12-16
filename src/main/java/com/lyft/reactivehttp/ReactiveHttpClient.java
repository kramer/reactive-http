package com.lyft.reactivehttp;

/**
 * Created by zakharov on 12/15/13.
 */
public class ReactiveHttpClient {
    public ReactiveHttpClient() {

    }

    public HttpRequest create() {
        return new HttpRequest(new DefaultHttpRequestExecutor());
    }
}
