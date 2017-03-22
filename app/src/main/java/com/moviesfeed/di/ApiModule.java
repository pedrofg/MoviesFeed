package com.moviesfeed.di;

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

}
