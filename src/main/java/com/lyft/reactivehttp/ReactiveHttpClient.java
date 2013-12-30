/*
 *
 *  * Copyright (C) 2012 Lyft, Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.lyft.reactivehttp;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import rx.Scheduler;

/**
 * @author Alexey Zakharov
 */
public class ReactiveHttpClient {
    private final OkHttpRequestExecutor requestExecutor;
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

        requestExecutor = new OkHttpRequestExecutor(okHttpClient, gson, scheduler, log, logEnabled);
    }

    public HttpRequest create() {
        return new HttpRequest(requestExecutor, gson);
    }
}
