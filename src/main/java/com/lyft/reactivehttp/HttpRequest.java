package com.lyft.reactivehttp;

import rx.Observable;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zakharov on 12/15/13.
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
    private HttpContent httpContent;
    private RequestExecutor requestExecutor;

    public HttpRequest(RequestExecutor requestExecutor) {
        this.requestExecutor = requestExecutor;
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
        httpContent = new JsonHttpContent(data);
        return this;
    }

    public HttpRequest file(File file) {
        httpContent = new FileContent(file);
        return this;
    }


    public <T> Observable<T> end(Class<T> responseClass) {
        return requestExecutor.execute(method, getUrlWithQueryString(), headers, httpContent, responseClass);
    }


    public String getUrlWithQueryString() {
        StringBuilder queryStringStr = new StringBuilder();

        int queryStringSize = queryString.size();
        if (queryStringSize > 0) {
            queryStringStr.append("?");

            int i = 0;

            for (Map.Entry<String, String> entry : queryString.entrySet()) {
                queryStringStr.append(entry.getKey());
                queryStringStr.append("=");
                queryStringStr.append(entry.getValue());

                if (i < queryStringSize - 1) {
                    queryStringStr.append("&");
                }

                i++;
            }
        }
        return queryStringStr.insert(0, url).toString();
    }


}
