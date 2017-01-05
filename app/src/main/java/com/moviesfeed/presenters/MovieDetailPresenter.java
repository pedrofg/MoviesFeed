package com.moviesfeed.presenters;

import android.os.Bundle;

import com.google.gson.JsonObject;
import com.moviesfeed.App;
import com.moviesfeed.activities.FeedActivity;
import com.moviesfeed.activities.MovieDetailActivity;
import com.moviesfeed.models.MovieDetail;
import com.moviesfeed.models.MoviesFeed;

import icepick.State;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by Pedro on 8/24/2016.
 */
public class MovieDetailPresenter extends BasePresenter<MovieDetailActivity> {

    private static final int REQUEST_MOVIE_DETAIL = 2;
    @State
    int movieId;

    private void createRequestMovieDetail() {

        final Observable<MovieDetail> movieDetailsObservable = App.getServerApi().getMovieDetail(this.movieId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        final Observable<MovieDetail> movieImagesObservable = App.getServerApi().getMovieImages(this.movieId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        final Observable<MovieDetail> combined = Observable.zip(movieDetailsObservable, movieImagesObservable, new Func2<MovieDetail, MovieDetail, MovieDetail>() {
            @Override
            public MovieDetail call(MovieDetail movieDetail, MovieDetail movieImage) {
                movieDetail.setBackdrops(movieImage.getBackdrops());
                movieDetail.setPosters(movieImage.getPosters());
                return movieDetail;
            }
        });

//        combined.subscribe(new Subscriber<MovieDetail>() {
//            @Override
//            public void onCompleted() {
//                //do nothing
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                //TODO handle error
//            }
//
//            @Override
//            public void onNext(MovieDetail movieDetail) {
//                getView().fillMovieDetails(movieDetail);
//            }
//        });


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

                        activity.fillMovieDetails(response);

                    }
                },
                new Action2<MovieDetailActivity, Throwable>() {
                    @Override
                    public void call(MovieDetailActivity activity, Throwable throwable) {
                        //TODO handle error
                    }
                });
    }

    public void requestMoviedetail(int movieId) {
        this.movieId = movieId;
        createRequestMovieDetail();
        start(REQUEST_MOVIE_DETAIL);
    }
}
