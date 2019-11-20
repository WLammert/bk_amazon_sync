package eu.dev089;

import eu.dev089.components.Updater;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BkUpdaterApplication {

    public static void main(String[] args) {
        var updater = new Updater();
        var scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleWithFixedDelay(updater, 1, 5, TimeUnit.MINUTES);
    }
}
