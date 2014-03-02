# Reactive HTTP

Reactive HTTP is a lightweight REST HTTP library with Observable(RxJava) interface.

### Chainable API

```java
ReactiveHttpClient client = new ReactiveHttpClient(new OkHttpClient(), new Gson(), Schedulers.executor(Executors.newFixedThreadPool(3), null, false);
client.create()
    .post("https://api.bar.com/do/%s/%s", "abc", "cba")
    .query("foo", "bar")
    .set("Authorization", "foo:bar")
    .data(new MyData(1, "2"))
    .observe(MyResponse.class)
    .subscribe(new Action1<MyResponse>() {
                   @Override
                   public void call(MyResponse response) {

                   }
               },
               new Action1<Throwable>() {
                   @Override
                   public void call(Throwable throwable) {
                       if (throwable instanceof HttpResponseException) {
                           HttpResponseException hre = (HttpResponseException) throwable;
                           GitHubError error = hre.getBodyAs(GithubApiError.class).message);
                       }
                   }
               }
    );
```

### Fix Fatal signal 11 (SIGSEGV) at 0x00000000 (code=1) on Android

OkHttp has a [problem](https://github.com/square/okhttp/issues/184) that causes this crash on Android. The official workaround from Square is to do the following just after OkHttpClient instance is created:

```java
OkHttpClient okHttpClient = new OkHttpClient();
URL.setURLStreamHandlerFactory(okHttpClient);
```

### Add common headers
```java
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
```
### Send HTTP request, with HttpResponse callback.
```java
client.create()
        .get("https://api.bar.com/do/")
        .observe()
        .subscribe(new Action1<HttpResponse>() {
               @Override
               public void call(HttpResponse response) {

               }
        });
```
### Send HTTP request, with String callback.
```java
client.create()
        .get("https://api.bar.com/do/")
        .observeAsString()
        .subscribe(new Action1<String>() {
               @Override
               public void call(String response) {

               }
        });
```
### Upload a file

```java
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
```

### Sync execution

```java
HttpRequest request = client.create()
        .get("https://api.bar.com/do/");

HttpResponse response = request.execute();
Bar result = request.execute(Bar.class);
String str = request.executeAsString();

```

### Custom error handler

```java

    public class GithubApiError {
        String message;
    }

    public class GithubException extends IOException {

        private int status;
        private GithubApiError error;

        public GithubException(int status, GithubApiError error) {

            this.status = status;
            this.error = error;
        }

        public int getStatus() {
            return status;
        }

        public GithubApiError getError() {
            return error;
        }
    }

    public class GithubErrorHandler implements ErrorHandler {

        @Override
        public Throwable handleError(HttpResponseException cause) {
            GithubException ge = new GithubException(cause.getStatus(), cause.getBodyAs(GithubApiError.class));

            return ge;
        }
    }

    ReactiveHttpClient client = createClient();

    client.setErrorHandler(new GithubErrorHandler());
    client.create()
            .get("https://api.github.com")
            .set("Authorization", "foo")
            .observe(Void.class)
            .subscribe(new Action1<Void>() {
                           @Override
                           public void call(Void v) {

                           }
                       }, new Action1<Throwable>() {
                           @Override
                           public void call(Throwable e) {
                                if (e instanceof GithubException) {

                                    GithubException ge = (GithubException) e;
                                }
                           }
                       }
            );

```

### Logging
```java
public class ConsoleLog implements HttpLog {
    @Override
    public void log(String message) {
        for (int i = 0, len = message.length(); i < len; i += LOG_CHUNK_SIZE) {
            int end = Math.min(len, i + LOG_CHUNK_SIZE);
            System.out.println(message.substring(i, end));
        }
    }
 }

// supply log class and set logging enabled param to true
ReactiveHttpClient client = new ReactiveHttpClient(new OkHttpClient(), new Gson(), Schedulers.currentThread(), new ConsoleLog(), true);
```
### Maven
```xml
<dependency>
    <groupId>com.lyft</groupId>
    <artifactId>reactivehttp</artifactId>
    <version>0.0.1</version>
</dependency>
```

### Android studio
```groovy
compile 'com.lyft:reactivehttp:0.0.x'
```

### Bugs and Feedback

For bugs, questions and discussions please use the [Github Issues](https://github.com/lyft/rective-http/issues).

### Inspired by

* [Http Request](https://github.com/kevinsawicki/http-request) by Kevin Sawicki
* [Retrofit](http://square.github.io/retrofit/) by Square
* [Super Agent](http://visionmedia.github.io/superagent/) by TJ Holowaychuk
