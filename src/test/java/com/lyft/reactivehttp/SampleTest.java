package com.lyft.reactivehttp;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import org.junit.Test;
import rx.concurrency.Schedulers;
import rx.util.functions.Action0;
import rx.util.functions.Action1;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

/**
 * Created by zakharov on 12/16/13.
 */
public class SampleTest {

    public static final String IMGUR_CLIENT_ID = "146bbf89f891032";

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

    ReactiveHttpClient client = new ReactiveHttpClient(new OkHttpClient(), gson, Schedulers.currentThread());

    @Test
    public void getRepoContributors() {
        client.create()
                .get("https://api.github.com/repos/%s/%s/contributors", "lyft", "reactive-http")
                .end(Contributors.class)
                .subscribe(new Action1<Contributors>() {
                    @Override
                    public void call(Contributors contributors) {
                        for (Contributor contributor : contributors) {
                            System.out.println(contributor.login);
                        }
                    }
                });
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
                                   throwable.printStackTrace();
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
                                   if (throwable instanceof HttpResponseException) {
                                       HttpResponseException hre = (HttpResponseException) throwable;
                                       System.out.print(hre.getError(GithubApiError.class).message);
                                   }
                                   throwable.printStackTrace();
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
                                   System.out.print("imgur response:" + gson.toJson(response));
                               }
                           }, new Action1<Throwable>() {
                               @Override
                               public void call(Throwable throwable) {
                                   throwable.printStackTrace();

                                   if (throwable instanceof HttpResponseException) {
                                       HttpResponseException hre = (HttpResponseException) throwable;
                                       System.out.print(hre.getError());
                                   }
                               }
                           }
                );

        latch.await();
    }

}
