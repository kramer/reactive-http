package com.lyft.reactivehttp;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscription;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by zakharov on 12/15/13.
 */
public class DefaultHttpRequestExecutor implements RequestExecutor {
    HttpLog log;
    boolean logEnabled;
    Gson gson;
    OkHttpClient okHttpClient;
    Scheduler scheduler;


    public DefaultHttpRequestExecutor(OkHttpClient okHttpClient, Gson gson, Scheduler scheduler, HttpLog log, boolean logEnabled) {
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
                        InputStreamReader isr = new InputStreamReader(response.getBody().in());

                        Exception error = new HttpResponseException(connection.getResponseCode(), getStringFromInputStreamReader(isr), gson);

                        throw (error);
                    }

                } catch (Exception e) {
                    observer.onError(e);
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }

                        if (in != null) {
                            in.close();
                        }

                    } catch (Exception e) {

                    }
                }

                final HttpURLConnection finalConnection = connection;

                return new Subscription() {
                    @Override
                    public void unsubscribe() {
                        if (finalConnection != null) {
                            finalConnection.disconnect();
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


//            for (Header header : response.getHeaders()) {
//                log.log(header.toString());
//            }

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

    private String getStringFromInputStreamReader(InputStreamReader isr) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(isr);

        StringBuilder result = new StringBuilder();

        String line;

        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }

    public static class HttpURLConnectionLogger {

    }
}
