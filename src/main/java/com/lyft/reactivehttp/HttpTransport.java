package com.lyft.reactivehttp;


import java.io.IOException;

public interface HttpTransport {
    HttpResponse execute(HttpRequest request) throws IOException;
}
