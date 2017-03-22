package com.moviesfeed.di;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Pedro on 2017-03-13.
 */

@Module
public class SchedulersModule {

    public static final String IO_SCHEDULER = "IO_SCHEDULER";
    public static final String MAIN_THREAD_SCHEDULER = "MAIN_THREAD_SCHEDULER";

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
