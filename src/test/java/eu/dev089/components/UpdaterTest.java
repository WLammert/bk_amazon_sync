package eu.dev089.components;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Response;

class UpdaterTest {

    private Updater updater = new Updater();
    private RequestCreator creator = new RequestCreator();


    void run() {
        updater.run();
    }

    void okhttp() throws IOException {
        OkHttpClient client = new Builder()
                .readTimeout(300, TimeUnit.SECONDS)
                .build();
        Instant start = Instant.now();
        Response response = client.newCall(creator.getAmazonCategoryProducts(1)).execute();
        System.out.println("returned responsecode " + response.code());

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("Call lasted " + timeElapsed + "ms");
    }
}