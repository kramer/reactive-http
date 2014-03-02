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

import java.io.IOException;

/**
 * @author Alexey Zakharov
 */
public class HttpResponseException extends IOException {
    int statusCode;
    private HttpResponse response;
    private Gson gson;
    private String url;

    public HttpResponseException(String url, HttpResponse response,  Gson gson) {
        this.url = url;
        this.response = response;
        this.gson = gson;
    }

    public String getUrl() {
        return url;
    }

    public int getStatus() {
        return response.getStatus();
    }

    public <T> T getBodyAs(Class<T> clazz) {
        try {
            String errorStr = Utils.inputStreamToString(response.getBody().in());
            return gson.fromJson(errorStr, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isServerError() {
        return statusCode / 100 == 5;
    }
}
