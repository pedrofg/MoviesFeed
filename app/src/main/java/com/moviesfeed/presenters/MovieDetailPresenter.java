package com.moviesfeed.presenters;

import android.util.Log;

import com.moviesfeed.App;
import com.moviesfeed.activities.MovieDetailActivity;
import com.moviesfeed.models.MovieDetail;

import nucleus.presenter.RxPresenter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by Pedro on 8/24/2016.
 */
public class MovieDetailPresenter extends RxPresenter<MovieDetailActivity> {

    private static final int REQUEST_MOVIE_DETAIL = 1;
    int movieId;

    private void createRequestMovieDetail() {

        final Observable<MovieDetail> movieDetailsObservable = App.getServerApi().getMovieDetail(this.movieId);

        final Observable<MovieDetail> movieImagesObservable = App.getServerApi().getMovieImages(this.movieId);

        final Observable<MovieDetail> combined = Observable.zip(movieDetailsObservable, movieImagesObservable, new Func2<MovieDetail, MovieDetail, MovieDetail>() {
            @Override
            public MovieDetail call(MovieDetail movieDetail, MovieDetail movieImage) {
                movieDetail.setBackdrops(movieImage.getBackdrops());
                return movieDetail;
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        restartableLatestCache(REQUEST_MOVIE_DETAIL,
                new Func0<Observable<MovieDetail>>() {
                    @Override
                    public Observable<MovieDetail> call() {
                        return combined;
                    }
                },
                new Action2<MovieDetailActivity, MovieDetail>() {
                    @Override
                    public void call(MovieDetailActivity activity, final MovieDetail response) {
                        Log.d(MovieDetailPresenter.class.getName(), "REQUEST_MOVIE_DETAIL success");
                        activity.requestMovieDetailCallbackSuccess(response);

                    }
                },
                new Action2<MovieDetailActivity, Throwable>() {
                    @Override
                    public void call(MovieDetailActivity activity, Throwable throwable) {
                        Log.e(MovieDetailPresenter.class.getName(), throwable.getMessage(), throwable);
                        //TODO handle error
                    }
                });
    }

    public void requestMoviedetail(int movieId) {
        Log.d(MovieDetailPresenter.class.getName(), "requestMoviedetail() movieId: " + movieId);
        this.movieId = movieId;
        createRequestMovieDetail();
        start(REQUEST_MOVIE_DETAIL);
    }
}
