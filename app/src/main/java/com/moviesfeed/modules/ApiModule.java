package com.moviesfeed.modules;

import com.moviesfeed.api.MoviesApi;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

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
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
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
