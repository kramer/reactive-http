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

/**
 * Created by zakharov on 12/15/13.
 */
public class DefaultHttpRequestExecutor implements RequestExecutor {
    Gson gson;
    OkHttpClient okHttpClient;
    private Scheduler scheduler;

    public DefaultHttpRequestExecutor(OkHttpClient okHttpClient, Gson gson, Scheduler scheduler) {
        this.okHttpClient = okHttpClient;
        this.gson = gson;
        this.scheduler = scheduler;
    }


    @Override
    public <T> Observable<T> execute(final HttpRequest httpRequest, final Class<T> clazz) {
        final Observable<T> observable = Observable.create(new Observable.OnSubscribeFunc<T>() {

            @Override
            public Subscription onSubscribe(Observer<? super T> observer) {
                OutputStream out = null;
                InputStream in = null;

                HttpURLConnection connection = null;

                try {
                    connection = okHttpClient.open(new URL(httpRequest.getUrlWithQueryString()));

                    connection.setRequestMethod(httpRequest.getMethod());

                    for (Map.Entry<String, String> headerEntry : httpRequest.getHeaders().entrySet()) {
                        connection.addRequestProperty(headerEntry.getKey(), headerEntry.getValue());
                    }

                    HttpContent httpContent = httpRequest.getHttpContent();

                    if (httpContent != null) {

                        connection.addRequestProperty("Content-type", httpContent.mimeType());

                        long contentLength = httpContent.getLength();
                        connection.setFixedLengthStreamingMode((int) contentLength);

                        connection.addRequestProperty("Content-Length", String.valueOf(contentLength));
                        out = connection.getOutputStream();

                        httpContent.writeTo(out);
                        out.close();
                    }


                    int statusCode = connection.getResponseCode();

                    if (statusCode >= 200 && statusCode < 300) {
                        in = connection.getInputStream();
                        InputStreamReader isr = new InputStreamReader(in);

                        T result = gson.fromJson(isr, clazz);

                        observer.onNext(result);
                        observer.onCompleted();
                    } else {
                        in = connection.getErrorStream();
                        InputStreamReader isr = new InputStreamReader(in);

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

    private String getStringFromInputStreamReader(InputStreamReader isr) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(isr);

        StringBuilder result = new StringBuilder();

        String line;

        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }
}
