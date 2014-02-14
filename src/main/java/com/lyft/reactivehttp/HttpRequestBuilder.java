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

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Alexey Zakharov
 */
public class HttpRequestBuilder {
    public static final String METHOD_DELETE = "DELETE";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_HEAD = "HEAD";
    public static final String METHOD_OPTIONS = "OPTIONS";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_PATCH = "PATCH";

    final ArrayList<NameValuePair> queryString = new ArrayList<NameValuePair>();
    final ArrayList<NameValuePair> headers = new ArrayList<NameValuePair>();

    private String url;
    private String method;
    private TypedOutput body;
    private ReactiveHttpClient reactiveHttpClient;
    private Gson gson;

    public HttpRequestBuilder(ReactiveHttpClient reactiveHttpClient, Gson gson) {
        this.reactiveHttpClient = reactiveHttpClient;
        this.gson = gson;
    }

    public HttpRequestBuilder get(String url, Object... params) {
        request(METHOD_GET, url, params);
        return this;
    }

    public HttpRequestBuilder post(String url, Object... params) {
        request(METHOD_POST, url, params);
        return this;
    }

    public HttpRequestBuilder put(String url, Object... params) {
        request(METHOD_PUT, url, params);
        return this;
    }

    public HttpRequestBuilder head(String url, Object... params) {
        request(METHOD_HEAD, url, params);
        return this;
    }

    public HttpRequestBuilder patch(String url, Object... params) {
        request(METHOD_PATCH, url, params);
        return this;
    }

    public HttpRequestBuilder options(String url, Object... params) {
        request(METHOD_OPTIONS, url, params);
        return this;
    }

    public HttpRequestBuilder delete(String url, Object... params) {
        request(METHOD_DELETE, url, params);
        return this;
    }

    public HttpRequestBuilder request(String method, String url, Object... params) {
        this.url = String.format(url, params);
        this.method = method;
        return this;
    }

    public HttpRequestBuilder query(String name, Object value) {
        queryString.add(new NameValuePair(name, value.toString()));
        return this;
    }

    public HttpRequestBuilder set(String name, String value) {
        headers.add(new NameValuePair(name, value));
        return this;
    }

    public HttpRequestBuilder data(Object data) {
        body = new JsonTypedOutput(data, gson);
        return this;
    }

    public HttpRequestBuilder file(String contentType, File file) {
        body = new FileTypedOutput(contentType, file);
        return this;
    }

    public HttpRequest build() {
        return new HttpRequest(method, getUrlWithQueryString(), headers, body);
    }

    public <T> Observable<T> observe(Class<T> responseClass) {
        return reactiveHttpClient.observe(build(), responseClass);
    }

    public Observable<HttpResponse> observe() {
        return reactiveHttpClient.observe(build());
    }

    public Observable<String> observeAsString() {
        return reactiveHttpClient.observeAsString(build());
    }

    public <T> T execute(Class<T> responseClass) throws IOException {
        return reactiveHttpClient.execute(build(), responseClass);
    }

    public HttpResponse execute() throws IOException {
        return reactiveHttpClient.execute(build());
    }

    public String executeAsString() throws IOException {
        return reactiveHttpClient.executeAsString(build());
    }


    public String getUrlWithQueryString() {
        StringBuilder queryStringStr = new StringBuilder();

        int queryStringSize = queryString.size();
        if (queryStringSize > 0) {
            queryStringStr.append("?");

            int i = 0;

            for (NameValuePair queryStringParam : queryString) {
                queryStringStr.append(encodeForUrl(queryStringParam.getName()));
                queryStringStr.append("=");
                queryStringStr.append(encodeForUrl(queryStringParam.getValue()));

                if (i < queryStringSize - 1) {
                    queryStringStr.append("&");
                }

                i++;
            }
        }
        return queryStringStr.insert(0, url).toString();
    }

    private String encodeForUrl(String value) {
        String encodedValue = "";

        try {
            encodedValue = URLEncoder.encode(String.valueOf(value), "UTF-8");
            // URLEncoder encodes for use as a query parameter. Path encoding uses %20 to
            // encode spaces rather than +. Query encoding difference specified in HTML spec.
            // Any remaining plus signs represent spaces as already URLEncoded.
            encodedValue = encodedValue.replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            // TODO raise custom exception here
        }

        return encodedValue;
    }

    void body(TypedOutput body) {
        this.body = body;
    }
}
