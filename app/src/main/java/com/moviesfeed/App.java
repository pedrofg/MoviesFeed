package com.moviesfeed;

import android.app.Application;

import com.moviesfeed.api.MoviesApi;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Pedro on 13/08/2016.
 */
public class App extends Application {

    private static MoviesApi serverApi;

    @Override
    public void onCreate() {
        super.onCreate();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MoviesApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        serverApi = retrofit.create(MoviesApi.class);
    }

    public static MoviesApi getServerApi() {
        return serverApi;
    }
}
