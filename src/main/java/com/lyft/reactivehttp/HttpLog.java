package com.lyft.reactivehttp;

/**
 * Created by zakharov on 12/27/13.
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
