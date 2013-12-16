package com.lyft.reactivehttp;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import rx.Observable;
import rx.Observer;
import rx.Subscription;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
                        String serializedData = gson.toJson(jsonHttpContent.getData());

                        out = connection.getOutputStream();
                        out.write(serializedData.getBytes(Charset.forName("UTF-8")));
                        out.close();
                    }

                    in = connection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(in);

                    T result = gson.fromJson(isr, responseClass);

                    observer.onNext(result);
                    observer.onCompleted();

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
}
