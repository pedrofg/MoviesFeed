package com.moviesfeed.interactors;

import com.moviesfeed.models.MoviesFeed;

/**
 * Created by Pedro on 2017-03-13.
 */

public interface FeedInteractorCallback {

    void onLoadSuccess(MoviesFeed moviesFeed, int amountMoviesByPage);

    void onLoadError(Throwable throwable, boolean isNetworkError);
}
