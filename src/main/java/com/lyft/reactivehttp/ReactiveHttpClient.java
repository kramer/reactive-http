package com.lyft.reactivehttp;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import rx.Scheduler;

/**
 * Created by zakharov on 12/15/13.
 */
public class ReactiveHttpClient {
    private final DefaultHttpRequestExecutor requestExecutor;
    private Scheduler scheduler;
    private OkHttpClient okHttpClient;
    private Gson gson;

    public ReactiveHttpClient(OkHttpClient okHttpClient, Gson gson, Scheduler scheduler) {
        this.okHttpClient = okHttpClient;
        this.gson = gson;
        this.scheduler = scheduler;

        requestExecutor = new DefaultHttpRequestExecutor(okHttpClient, gson, scheduler);
    }

    public HttpRequest create() {
        return new HttpRequest(requestExecutor, gson);
    }
}
