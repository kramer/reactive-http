package com.lyft.reactivehttp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by zakharov on 12/15/13.
 */
public class HttpRequestTest {
    public static class Foo {

    }


    @Test
    public void createGet() {
        MockHttpRequestExecutor requestExecutor = new MockHttpRequestExecutor();
        new HttpRequest(requestExecutor).get("http://google.com/%s/", "request").end(Foo.class);
        assertEquals("http://google.com/request/", requestExecutor.url);
    }

    @Test
    public void createQueryString() {
        MockHttpRequestExecutor requestExecutor = new MockHttpRequestExecutor();
        new HttpRequest(requestExecutor).get("http://google.com").query("q1", "abc").query("q2", "cba").end(Foo.class);
        assertEquals("http://google.com?q1=abc&q2=cba", requestExecutor.url);
    }
}
