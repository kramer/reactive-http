package com.lyft.reactivehttp;

/**
* Created by zakharov on 2/3/14.
*/
class ConsoleLog implements HttpLog {
    private static final int LOG_CHUNK_SIZE = 4000;

    @Override
    public void log(String message) {
        for (int i = 0, len = message.length(); i < len; i += LOG_CHUNK_SIZE) {
            int end = Math.min(len, i + LOG_CHUNK_SIZE);
            System.out.println(message.substring(i, end));
        }
    }
}
