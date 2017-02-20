package com.moviesfeed.daggercomponents;

import android.app.Application;

import com.moviesfeed.presenters.FeedPresenter;
import com.moviesfeed.presenters.MovieDetailPresenter;

import javax.inject.Singleton;

import dagger.Component;
import com.moviesfeed.modules.ApiModule;
import com.moviesfeed.modules.DatabaseModule;

/**
 * Created by Pedro on 2017-02-25.
 */

@Singleton
@Component(modules = {ApiModule.class, DatabaseModule.class})
public interface AppComponent {

    void inject(FeedPresenter o);

    void inject(MovieDetailPresenter o);

    void inject(Application app);
}
