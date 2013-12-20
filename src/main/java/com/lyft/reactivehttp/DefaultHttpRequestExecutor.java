package com.lyft.reactivehttp;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import rx.Observable;
import rx.Observer;
import rx.Subscription;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by zakharov on 12/15/13.
 */
public class DefaultHttpRequestExecutor implements RequestExecutor {
    Gson gson = new Gson();
    OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    public <T> Observable<T> execute(
            final String method,
            final String url,
            final Map<String, String> headers,
            final HttpContent httpContent,
            final Class<T> responseClass) {
        return Observable.create(new Observable.OnSubscribeFunc<T>() {
            @Override
            public Subscription onSubscribe(Observer<? super T> observer) {
                OutputStream out = null;
                InputStream in = null;

                HttpURLConnection connection = null;

                try {
                    connection = okHttpClient.open(new URL(url));

                    connection.setRequestMethod(method);

                    for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                        connection.addRequestProperty(headerEntry.getKey(), headerEntry.getValue());
                    }

                    if (httpContent instanceof JsonHttpContent) {
                        JsonHttpContent jsonHttpContent = (JsonHttpContent) httpContent;
                        connection.addRequestProperty("Content-type", jsonHttpContent.mimeType());

                        String serializedData = gson.toJson(jsonHttpContent.getData());
                        byte[] serializedBytes = serializedData.getBytes(Charset.forName("UTF-8"));

                        long contentLength = serializedBytes.length;
                        connection.setFixedLengthStreamingMode((int) contentLength);
                        connection.addRequestProperty("Content-Length", String.valueOf(contentLength));

                        out = connection.getOutputStream();

                        out.write(serializedBytes);
                        out.close();
                    }

                    int statusCode = connection.getResponseCode();

                    if (statusCode >= 200 && statusCode < 300) {
                        in = connection.getInputStream();
                        InputStreamReader isr = new InputStreamReader(in);

                        T result = gson.fromJson(isr, responseClass);

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
