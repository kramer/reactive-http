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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Alexey Zakharov
 */
public class HttpRequest {
    public static final String METHOD_DELETE = "DELETE";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_HEAD = "HEAD";
    public static final String METHOD_OPTIONS = "OPTIONS";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_PATCH = "PATCH";

    final Map<String, String> queryString = new LinkedHashMap<String, String>();
    final Map<String, String> headers = new LinkedHashMap<String, String>();

    private String url;
    private String method;
    private TypedOutput body;
    private RequestExecutor requestExecutor;
    private Gson gson;

    public HttpRequest(RequestExecutor requestExecutor, Gson gson) {
        this.requestExecutor = requestExecutor;
        this.gson = gson;
    }

    public HttpRequest get(String url, Object... params) {
        request(METHOD_GET, url, params);
        return this;
    }

    public HttpRequest post(String url, Object... params) {
        request(METHOD_POST, url, params);
        return this;
    }

    public HttpRequest put(String url, Object... params) {
        request(METHOD_PUT, url, params);
        return this;
    }

    public HttpRequest head(String url, Object... params) {
        request(METHOD_HEAD, url, params);
        return this;
    }

    public HttpRequest patch(String url, Object... params) {
        request(METHOD_PATCH, url, params);
        return this;
    }

    public HttpRequest options(String url, Object... params) {
        request(METHOD_OPTIONS, url, params);
        return this;
    }

    public HttpRequest delete(String url, Object... params) {
        request(METHOD_DELETE, url, params);
        return this;
    }

    public HttpRequest request(String method, String url, Object... params) {
        this.url = String.format(url, params);
        this.method = method;
        return this;
    }

    public HttpRequest query(String paramName, Object paramValue) {
        queryString.put(paramName, paramValue.toString());
        return this;
    }

    public HttpRequest set(String paramName, Object paramValue) {
        headers.put(paramName, paramValue.toString());
        return this;
    }

    public HttpRequest data(Object data) {
        body = new JsonTypedOutput(data, gson);
        return this;
    }

    public HttpRequest file(String contentType, File file) {
        body = new FileTypedOutput(contentType, file);
        return this;
    }

    public String getMethod() {
        return method;
    }

    public TypedOutput getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public <T> Observable<T> end(Class<T> responseClass) {
        return requestExecutor.execute(this, responseClass);
    }


    public String getUrlWithQueryString() {
        StringBuilder queryStringStr = new StringBuilder();

        int queryStringSize = queryString.size();
        if (queryStringSize > 0) {
            queryStringStr.append("?");

            int i = 0;

            for (Map.Entry<String, String> entry : queryString.entrySet()) {
                queryStringStr.append(encodeForUrl(entry.getKey()));
                queryStringStr.append("=");
                queryStringStr.append(encodeForUrl(entry.getValue()));

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
