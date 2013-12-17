package com.lyft.reactivehttp;

import rx.Observable;

import java.util.Map;

/**
 * Created by zakharov on 12/15/13.
 */
public interface RequestExecutor {
     <T> Observable<T> execute(
            String method,
            String url,
            Map<String, String> headers,
            HttpContent httpContent,
            HttpErrorHandler errorHandler,
            Class<T> responseClass);
}
