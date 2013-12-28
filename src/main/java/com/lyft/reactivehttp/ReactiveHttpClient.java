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
    private HttpLog log;
    private boolean logEnabled;
    private OkHttpClient okHttpClient;
    private Gson gson;

    public ReactiveHttpClient(OkHttpClient okHttpClient, Gson gson, Scheduler scheduler, HttpLog log, boolean logEnabled) {
        this.okHttpClient = okHttpClient;
        this.gson = gson;
        this.scheduler = scheduler;
        this.log = log;
        this.logEnabled = logEnabled;

        requestExecutor = new DefaultHttpRequestExecutor(okHttpClient, gson, scheduler, log, logEnabled);
    }

    public HttpRequest create() {
        return new HttpRequest(requestExecutor, gson);
    }
}
