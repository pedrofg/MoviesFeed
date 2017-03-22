package com.moviesfeed;

import com.moviesfeed.ui.presenters.FeedPresenter;
import com.moviesfeed.ui.presenters.MovieDetailPresenter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Pedro on 2017-02-25.
 */

@Singleton
@Component(modules = TestAppModule.class)
public interface TestComponent {
    void inject(FeedPresenter p);

    void inject(MovieDetailPresenter p);
}
