package com.moviesfeed;

import com.moviesfeed.daggercomponents.AppComponent;
import com.moviesfeed.presenters.FeedPresenter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Pedro on 2017-02-25.
 */

@Singleton
@Component(modules = TestAppModule.class)
public interface TestComponent {
    void inject(FeedPresenter p);
}
