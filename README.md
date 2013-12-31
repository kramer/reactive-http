### Lightweight REST http library with Observable interface.

### Chainable API

    client.create()
        .get("https://api.bar.com/do/%s/%s", "abc", "cba")
        .query("foo, "bar");
        .set("Authorization", "foo:bar");
        .observe(MyResponse.class)
        .subscribe(new Action1<MyResponse>() {
                       @Override
                       public void call(MyResponse response) {
                               if (throwable instanceof HttpResponseException) {
                                   HttpResponseException hre = (HttpResponseException) throwable;
                                   GitHubError error = hre.getError(GithubApiError.class).message);
                               }
                       }
                   }, new Action1<Throwable>() {
                       @Override
                       public void call(Throwable throwable) {

                       }
                   }
        );

### Upload file


    client.create()
            .post("https://api.imgur.com/3/image")
            .file("image/jpeg", file)
            .set("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
            .observe(ImgurResponse.class)
            .subscribe(new Action1<ImgurResponse>() {
                           @Override
                           public void call(ImgurResponse response) {
                              ...
                           }
                       }
            );