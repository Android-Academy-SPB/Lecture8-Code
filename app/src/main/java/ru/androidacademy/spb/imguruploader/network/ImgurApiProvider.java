package ru.androidacademy.spb.imguruploader.network;

import android.support.annotation.AnyThread;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Artur Vasilov
 */
public final class ImgurApiProvider {

    @Nullable
    private static volatile ImgurApiService sApiService;

    @MainThread
    public static void initialize() {
        if (sApiService != null) {
            throw new IllegalStateException("Attempting to initialize service second time");
        }

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        sApiService = new Retrofit.Builder()
                .baseUrl("https://api.imgur.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
                .create(ImgurApiService.class);
    }

    @AnyThread
    public static ImgurApiService getApiService() {
        if (sApiService == null) {
            throw new IllegalStateException("You should call initialize first");
        }
        return sApiService;
    }
}
