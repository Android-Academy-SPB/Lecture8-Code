package ru.androidacademy.spb.imguruploader;

import android.app.Application;

import java.util.concurrent.Executors;

import androidx.work.Configuration;
import androidx.work.WorkManager;
import ru.androidacademy.spb.imguruploader.network.ImgurApiProvider;

/**
 * @author Artur Vasilov
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ImgurApiProvider.initialize();

        Configuration configuration = new Configuration.Builder()
                .withExecutor(Executors.newCachedThreadPool())
                .build();
        WorkManager.initialize(getApplicationContext(), configuration);
    }
}
