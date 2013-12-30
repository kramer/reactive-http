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
import com.squareup.okhttp.OkHttpClient;
import org.junit.Test;
import rx.concurrency.Schedulers;
import rx.util.functions.Action0;
import rx.util.functions.Action1;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class SampleTest {

    public static final String IMGUR_CLIENT_ID = "146bbf89f891032";

    static class ConsoleLog implements HttpLog {

        @Override
        public void log(String message) {
            for (int i = 0, len = message.length(); i < len; i += LOG_CHUNK_SIZE) {
                int end = Math.min(len, i + LOG_CHUNK_SIZE);
                System.out.println(message.substring(i, end));
            }
        }
    }

    private static final int LOG_CHUNK_SIZE = 4000;

    public static class Contributor {
        String login;
        int contributions;
    }

    static class Contributors extends ArrayList<Contributor> {
    }

//    interface GitHub {
//        @GET("/repos/{owner}/{repo}/contributors")
//        List<Contributor> contributors(
//                @Path("owner") String owner,
//                @Path("repo") String repo
//        );
//    }

    private final Gson gson = new Gson();

    ReactiveHttpClient client = new ReactiveHttpClient(new OkHttpClient(), gson, Schedulers.currentThread(), new ConsoleLog(), true);

    @Test
    public void getRepoContributors() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        client.create()
                .get("https://api.github.com/repos/%s/%s/contributors", "lyft", "reactive-http")
                .end(Contributors.class)
                .finallyDo(new Action0() {
                    @Override
                    public void call() {
                        latch.countDown();
                    }
                })
                .subscribe(new Action1<Contributors>() {
                    @Override
                    public void call(Contributors contributors) {
                        for (Contributor contributor : contributors) {

                        }
                    }
                });

        latch.await();
    }

    @Test
    public void postRepoContributors() {
        Contributor data = new Contributor();
        data.login = "sdfsdaf";
        int contributions = 232;

        client.create()
                .post("https://api.github.com/repos/%s/%s/contributors", "lyft", "reactive-http")
                .data(data)
                .end(Contributors.class)
                .subscribe(new Action1<Contributors>() {
                               @Override
                               public void call(Contributors contributors) {
                                   for (Contributor contributor : contributors) {

                                   }
                               }
                           }, new Action1<Throwable>() {
                               @Override
                               public void call(Throwable throwable) {

                               }
                           }
                );
    }

    @Test
    public void getNotExistingRepoContributors() {
        client.create()
                .get("https://api.github.com/repos/%s/%s/contributors", "lyft", "asdfdsaf")
                .end(Contributors.class)
                .subscribe(new Action1<Contributors>() {
                               @Override
                               public void call(Contributors contributors) {

                               }
                           }, new Action1<Throwable>() {
                               @Override
                               public void call(Throwable throwable) {

                               }
                           }
                );
    }


    static class GithubApiError {
        String message;
    }


    @Test
    public void failAuthroizeOnGithub() {
        client.create()
                .get("https://api.github.com")
                .set("Authorization", "foo")
                .end(Void.class)
                .subscribe(new Action1<Void>() {
                               @Override
                               public void call(Void v) {

                               }
                           }, new Action1<Throwable>() {
                               @Override
                               public void call(Throwable throwable) {

                               }
                           }
                );
    }

    static class ImgurResponse {
        Object data;
        Boolean success;
        Integer status;
    }

    @Test
    public void uploadImageToImgur() throws InterruptedException {
        File file = new File("src/test/resources/image_sample.jpg");

        final CountDownLatch latch = new CountDownLatch(1);

        client.create()
                .post("https://api.imgur.com/3/image")
                .file("image/jpeg", file)
                .set("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                .end(ImgurResponse.class)
                .finallyDo(new Action0() {
                    @Override
                    public void call() {
                        latch.countDown();
                    }
                })
                .subscribe(new Action1<ImgurResponse>() {
                               @Override
                               public void call(ImgurResponse response) {

                               }
                           }, new Action1<Throwable>() {
                               @Override
                               public void call(Throwable throwable) {

                               }
                           }
                );

        latch.await();
    }

}
