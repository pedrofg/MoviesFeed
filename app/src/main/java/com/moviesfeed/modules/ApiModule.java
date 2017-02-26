package com.moviesfeed.modules;

import com.moviesfeed.api.MoviesApi;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Pedro on 2017-02-25.
 */

@Module
public class ApiModule {

    public static final String IO_SCHEDULER = "IO_SCHEDULER";
    public static final String MAIN_THREAD_SCHEDULER = "MAIN_THREAD_SCHEDULER";

    @Singleton
    @Provides
    public MoviesApi provideServerAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MoviesApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit.create(MoviesApi.class);
    }

    @Provides
    @Named(IO_SCHEDULER)
    public Scheduler provideIOScheduler() {
        return Schedulers.io();
    }

    @Provides
    @Named(MAIN_THREAD_SCHEDULER)
    public Scheduler provideMainThreadScheduler() {
        return AndroidSchedulers.mainThread();
    }
}
