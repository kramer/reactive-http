package com.lyft.reactivehttp;

import com.google.gson.Gson;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by zakharov on 12/15/13.
 */
public class HttpRequestTest {
    private Gson gson = new Gson();

    public static class Foo {

    }


    @Test
    public void createGet() {
        MockHttpRequestExecutor requestExecutor = new MockHttpRequestExecutor();
        HttpRequest request = new HttpRequest(requestExecutor, gson).get("http://google.com/%s/", "request");
        assertEquals("http://google.com/request/", request.getUrlWithQueryString());
    }

    @Test
    public void createQueryString() {
        MockHttpRequestExecutor requestExecutor = new MockHttpRequestExecutor();
        HttpRequest request = new HttpRequest(requestExecutor, gson).get("http://google.com").query("q1", "abc").query("q2", "cba");
        assertEquals("http://google.com?q1=abc&q2=cba", request.getUrlWithQueryString());
    }
}
