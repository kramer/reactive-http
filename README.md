Lightweight REST http library inspired by Nodejs Superagent.

        client.create()
                .get("https://api.bar.com/do/%s/%s", "abc", "cba")
                .query("foo, "bar");
                .set("Authorization", "foo:bar");
                .end(MyResponse.class)
                .subscribe(new Action1<MyResponse>() {
                               @Override
                               public void call(MyResponse response) {

                               }
                           }, new Action1<Throwable>() {
                               @Override
                               public void call(Throwable throwable) {

                               }
                           }
                );