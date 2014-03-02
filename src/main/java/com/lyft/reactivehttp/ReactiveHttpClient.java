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
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Alexey Zakharov
 */
public class ReactiveHttpClient {
    HttpLog log;
    boolean logEnabled;
    Gson gson;
    HttpTransport httpTransport;
    Scheduler scheduler;
    ErrorHandler errorHandler = ErrorHandler.DEFAULT;


    public ReactiveHttpClient(
            HttpTransport httpTransport,
            Gson gson,
            Scheduler scheduler,
            HttpLog log,
            boolean logEnabled) {

        this.httpTransport = httpTransport;
        this.gson = gson;
        this.scheduler = scheduler;
        this.log = log;
        this.logEnabled = logEnabled;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public HttpRequestBuilder create() {
        return new HttpRequestBuilder(this, gson);
    }


    public HttpResponse execute(HttpRequest httpRequest) throws Throwable {
        return executeAndProcess(httpRequest, new SimpleResponseProcessor());
    }

    public String executeAsString(HttpRequest httpRequest) throws Throwable {
        return executeAndProcess(httpRequest, new StringResponseProcessor());
    }

    public <T> T execute(final HttpRequest request, final Class<T> clazz) throws Throwable {
        return executeAndProcess(request, new JsonResponseProcessor<T>(gson, clazz));
    }

    public Observable<HttpResponse> observe(HttpRequest httpRequest) {
        return observeAndProcess(httpRequest, new SimpleResponseProcessor());
    }

    public Observable<String> observeAsString(HttpRequest httpRequest) {
        return observeAndProcess(httpRequest, new StringResponseProcessor());
    }

    public <T> Observable<T> observe(final HttpRequest request, final Class<T> clazz) {
        return observeAndProcess(request, new JsonResponseProcessor<T>(gson, clazz));
    }

    static class SimpleResponseProcessor implements ResponseProcessor<HttpResponse> {
        @Override
        public HttpResponse process(HttpResponse httpResponse) throws IOException {
            return httpResponse;
        }
    }

    static class StringResponseProcessor implements ResponseProcessor<String> {
        @Override
        public String process(HttpResponse httpResponse) throws IOException {
            return Utils.inputStreamToString(httpResponse.getBody().in());
        }
    }

    static class JsonResponseProcessor<T> implements ResponseProcessor<T> {
        private Gson gson;
        private Class<T> clazz;

        JsonResponseProcessor(Gson gson, Class<T> clazz) {
            this.gson = gson;
            this.clazz = clazz;
        }

        @Override
        public T process(HttpResponse httpResponse) throws IOException {
            InputStream in = httpResponse.getBody().in();

            InputStreamReader isr = new InputStreamReader(in);
            T result = gson.fromJson(isr, clazz);
            in.close();

            return result;
        }
    }

    private <T> T executeAndProcess(final HttpRequest request, final ResponseProcessor<T> responseProcessor) throws Throwable {
        long start = System.nanoTime();

        if (logEnabled) {
            logRequest(request);
        }

        HttpResponse response = httpTransport.execute(request);

        if (logEnabled) {
            long elapsedTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

            response = logResponse(request.getUrl(), response, elapsedTime);
        }

        if (response.getStatus() >= 200 && response.getStatus() < 300) {
            T result = responseProcessor.process(response);
            return result;
        } else {
            HttpResponseException e = new HttpResponseException(request.getUrl(), response, gson);
            throw errorHandler.handleError(e);
        }
    }

    private <T> Observable<T> observeAndProcess(final HttpRequest request, final ResponseProcessor<T> responseProcessor) {
        final Observable<T> observable = Observable.create(new Observable.OnSubscribeFunc<T>() {

            @Override
            public Subscription onSubscribe(Observer<? super T> observer) {
                try {
                    T result = executeAndProcess(request, responseProcessor);

                    observer.onNext(result);
                    observer.onCompleted();
                } catch (Throwable e) {
                    observer.onError(e);
                }

                return Subscriptions.empty();
            }
        });

        return observable
                .subscribeOn(scheduler);
    }

    public static interface ResponseProcessor<T> {
        T process(HttpResponse httpResponse) throws IOException;
    }

    private HttpRequest logRequest(HttpRequest request) throws IOException {
        log.log(String.format("---> %s %s", request.getMethod(), request.getUrl()));


        for (NameValuePair header : request.getHeaders()) {
            log.log(header.getName() + ":" + header.getValue());
        }

        long bodySize = 0;
        TypedOutput body = request.getBody();

        if (body != null) {
            request = Utils.cacheRequest(request);

            bodySize = request.getBody().length();
            String bodyMime = request.getBody().mimeType();

            if (bodyMime != null) {
                log.log("Content-Type: " + bodyMime);
            }
            if (bodySize != -1) {
                log.log("Content-Length: " + bodySize);
            }

            byte[] bodyBytes = ((ByteArrayTypedOutput) request.getBody()).getBytes();

            log.log(new String(bodyBytes, "UTF-8"));
        }

        log.log(String.format("---> END (%s-byte body)", bodySize));

        return request;
    }

    private HttpResponse logResponse(String url, HttpResponse response, long elapsedTime) throws IOException {
        log.log(String.format("<--- HTTP %s %s (%sms)", response.getStatus(), url, elapsedTime));


        for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
            log.log(header.getKey() + ":" + header.getValue());
        }

        long bodySize = 0;
        TypedInput body = response.getBody();
        if (body != null) {
            response = Utils.cacheResponse(response);

            body = response.getBody();

            byte[] bodyBytes = ((TypedInputByteArray) body).getBytes();
            bodySize = bodyBytes.length;
            String bodyMime = body.mimeType();
            log.log(bodyMime);
            log.log(new String(bodyBytes, "UTF-8"));

        }

        log.log(String.format("<--- END HTTP (%s-byte body)", bodySize));

        return response;
    }
}
