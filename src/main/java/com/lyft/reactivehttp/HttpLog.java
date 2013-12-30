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

/**
 * @author Alexey Zakharov
 */
public interface HttpLog {
    /**
     * Log a debug message to the appropriate console.
     */
    void log(String message);

    /**
     * A {@link HttpLog} implementation which does not log anything.
     */
    HttpLog NONE = new HttpLog() {
        @Override
        public void log(String message) {
        }
    };
}
