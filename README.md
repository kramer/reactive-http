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

### Put common headers
    private HttpRequest createRequest() {
        return client.create()
            .set("Authorization", "foo:bar")
            .set("Accept-Language", "en-US")
    }

    private Observable<Foo> requestFoo() {
        return createRequest()
                .get("https://api.bar.com/foo")
                .observe(Foo.class);
    }

### Receive full http response

    client.create()
            .get("https://api.bar.com/do/")
            .observe()
            .subscribe(new Action1<HttpResponse>() {
                   @Override
                   public void call(HttpResponse response) {

                   }
            });

### Receive response as string

    client.create()
            .get("https://api.bar.com/do/")
            .observeAsString()
            .subscribe(new Action1<String>() {
                   @Override
                   public void call(String response) {

                   }
            });


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

### Inspired by

* [Http Request](https://github.com/kevinsawicki/http-request) by Kevin Sawicki
* [Retrofit](http://square.github.io/retrofit/) by Square
* [Super Agent](http://visionmedia.github.io/superagent/) by TJ Holowaychuk