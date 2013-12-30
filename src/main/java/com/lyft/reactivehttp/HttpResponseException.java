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

public class HttpResponseException extends IOException {
    int statusCode;
    String error;
    private Gson gson;

    public HttpResponseException(int statusCode, String error, Gson gson) {
        this.statusCode = statusCode;
        this.error = error;
        this.gson = gson;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getError() {
        return error;
    }

    public <T> T getError(Class<T> clazz) {
        try {
            return gson.fromJson(error, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isServerError() {
        return statusCode / 100 == 5;
    }
}
