package com.moviesfeed.interactors;

import com.moviesfeed.models.MovieDetail;

/**
 * Created by Pedro on 2017-03-23.
 */

public interface MovieDetailInteractorCallback {

    void onLoadSuccess(MovieDetail movieDetail);

    void onLoadError(Throwable throwable, boolean isNetworkError);
}
