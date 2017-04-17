package com.moviesfeed.di;

import com.moviesfeed.interactors.FeedInteractor;
import com.moviesfeed.interactors.MovieDetailInteractor;
import com.moviesfeed.interactors.PersonDetailsInteractor;

import dagger.Component;

/**
 * Created by Pedro on 2017-03-13.
 */

@Component(modules = {SchedulersModule.class})
public interface SchedulersComponent {

    void inject(FeedInteractor feedInteractor);
    void inject(MovieDetailInteractor movieDetailInteractor);
    void inject(PersonDetailsInteractor personDetailsInteractor);

}
