package com.lyft.reactivehttp;

import rx.Observable;

/**
 * Created by zakharov on 12/15/13.
 */
public interface RequestExecutor {
    <T> Observable<T> execute(final HttpRequest httpRequest, final Class<T> clazz);
}
