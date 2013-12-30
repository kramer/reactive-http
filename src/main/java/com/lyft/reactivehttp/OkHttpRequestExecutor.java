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
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscription;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpRequestExecutor implements RequestExecutor {
    HttpLog log;
    boolean logEnabled;
    Gson gson;
    OkHttpClient okHttpClient;
    Scheduler scheduler;


    public OkHttpRequestExecutor(OkHttpClient okHttpClient, Gson gson, Scheduler scheduler, HttpLog log, boolean logEnabled) {
        this.okHttpClient = okHttpClient;
        this.gson = gson;
        this.scheduler = scheduler;
        this.log = log;
        this.logEnabled = logEnabled;
    }


    @Override
    public <T> Observable<T> execute(final HttpRequest request, final Class<T> clazz) {
        final Observable<T> observable = Observable.create(new Observable.OnSubscribeFunc<T>() {

            @Override
            public Subscription onSubscribe(Observer<? super T> observer) {
                OutputStream out = null;
                InputStream in = null;

                HttpURLConnection connection = null;

                try {
                    long start = System.nanoTime();

                    connection = okHttpClient.open(new URL(request.getUrlWithQueryString()));

                    if (logEnabled) {
                        logRequest(request);
                    }

                    connection.setRequestMethod(request.getMethod());

                    for (Map.Entry<String, String> headerEntry : request.getHeaders().entrySet()) {
                        connection.addRequestProperty(headerEntry.getKey(), headerEntry.getValue());
                    }

                    TypedOutput requestBody = request.getBody();

                    if (requestBody != null) {

                        connection.addRequestProperty("Content-type", requestBody.mimeType());

                        long contentLength = requestBody.length();
                        connection.setFixedLengthStreamingMode((int) contentLength);

                        connection.addRequestProperty("Content-Length", String.valueOf(contentLength));
                        out = connection.getOutputStream();

                        requestBody.writeTo(out);
                        out.close();
                    }


                    int statusCode = connection.getResponseCode();

                    if (statusCode >= 200 && statusCode < 300) {
                        in = connection.getInputStream();
                    } else {
                        in = connection.getErrorStream();
                    }

                    String mimeType = connection.getContentType();
                    long length = connection.getContentLength();

                    TypedInputStream typedInputStream = new TypedInputStream(mimeType, length, in);

                    HttpResponse response = new HttpResponse(statusCode, typedInputStream);

                    for (Map.Entry<String, List<String>> field : connection.getHeaderFields().entrySet()) {
                        String name = field.getKey();
                        for (String value : field.getValue()) {
                            response.addHeader(name, value);
                        }
                    }

                    if (logEnabled) {
                        long elapsedTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

                        response = logResponse(request.getUrlWithQueryString(), response, elapsedTime);
                    }

                    if (response.isSuccess()) {
                        InputStreamReader isr = new InputStreamReader(response.getBody().in());

                        T result = gson.fromJson(isr, clazz);

                        observer.onNext(result);
                        observer.onCompleted();
                    } else {
                        Exception error = new HttpResponseException(connection.getResponseCode(), Utils.inputStreamToString(response.getBody().in()), gson);

                        throw (error);
                    }

                } catch (Throwable e) {
                    observer.onError(e);
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }

                        if (in != null) {
                            in.close();
                        }

                    } catch (Throwable e) {
                        // suppress stream close errors
                    }
                }

                final HttpURLConnection finalConnection = connection;

                return new Subscription() {
                    @Override
                    public void unsubscribe() {
                        if (finalConnection != null) {
                            try {
                                finalConnection.disconnect();
                            } catch (Throwable e) {
                                //suppress any exceptions during disconnect
                            }
                        }
                    }
                };
            }
        });

        return observable
                .subscribeOn(scheduler);
    }

    private HttpRequest logRequest(HttpRequest request) throws IOException {
        log.log(String.format("---> %s %s", request.getMethod(), request.getUrlWithQueryString()));


        for (Map.Entry<String, String> header : request.getHeaders().entrySet()) {
            log.log(header.getKey() + ":" + header.getValue());
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
