package com.lyft.reactivehttp;

import com.google.gson.Gson;
import org.junit.Test;
import rx.util.functions.Action1;

import java.util.ArrayList;

/**
 * Created by zakharov on 12/16/13.
 */
public class SampleTest {
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

    ReactiveHttpClient client = new ReactiveHttpClient();

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

    static class GithubApiException extends RuntimeException {

        private int statusCode;
        private GithubApiError error;

        public GithubApiException(int statusCode, GithubApiError error) {

            this.statusCode = statusCode;
            this.error = error;
        }

        public GithubApiError getError() {
            return error;
        }
    }

    static class GithubApiError {
        String message;
    }

    static class GithubApiErrorHandler implements HttpErrorHandler {
        Gson gson = new Gson();

        @Override
        public Exception getError(int statusCode, String error) {
            return new GithubApiException(statusCode, gson.fromJson(error, GithubApiError.class));
        }
    }

    @Test
    public void failAuthroizeOnGithub() {
        client.create()
                .get("https://api.github.com")
                .set("Authorization", "foo")
                .errorHandler(new GithubApiErrorHandler())
                .end(Void.class)
                .subscribe(new Action1<Void>() {
                               @Override
                               public void call(Void v) {

                               }
                           }, new Action1<Throwable>() {
                               @Override
                               public void call(Throwable throwable) {
                                   if (throwable instanceof GithubApiException) {
                                       GithubApiException get = (GithubApiException) throwable;
                                       System.out.print(get.getError().message);
                                   }
                                   throwable.printStackTrace();
                               }
                           }
                );
    }

}
