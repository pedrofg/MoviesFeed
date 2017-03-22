package com.moviesfeed;

import com.moviesfeed.api.MoviesApi;
import com.moviesfeed.models.DaoSession;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

import static com.moviesfeed.di.SchedulersModule.IO_SCHEDULER;
import static com.moviesfeed.di.SchedulersModule.MAIN_THREAD_SCHEDULER;
import static org.mockito.Mockito.mock;

/**
 * Created by Pedro on 2017-02-25.
 */

@Module
public class TestAppModule {

    @Provides
    @Singleton
    public MoviesApi provideServerAPI() {
        return mock(MoviesApi.class);
    }

    @Singleton
    @Provides
    public DaoSession getDaoSession() {
        return mock(DaoSession.class);
    }

    @Provides
    @Named(IO_SCHEDULER)
    public Scheduler provideIOScheduler() {
        return Schedulers.trampoline();
    }

    @Provides
    @Named(MAIN_THREAD_SCHEDULER)
    public Scheduler provideMainThreadScheduler() {
        return Schedulers.trampoline();
    }
}
