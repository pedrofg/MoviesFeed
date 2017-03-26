package com.moviesfeed.di;

import com.moviesfeed.repository.FeedRepository;
import com.moviesfeed.repository.MovieDetailRepository;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Pedro on 2017-02-25.
 */

@Singleton
@Component(modules = {ApiModule.class, DatabaseModule.class})
public interface AppComponent {

    void inject(FeedRepository feedRepository);

    void inject(MovieDetailRepository feedRepository);
}
