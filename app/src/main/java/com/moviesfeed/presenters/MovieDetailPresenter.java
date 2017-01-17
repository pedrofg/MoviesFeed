package com.moviesfeed.presenters;

import android.os.Bundle;
import android.util.Log;

import com.moviesfeed.App;
import com.moviesfeed.activities.MovieDetailActivity;
import com.moviesfeed.models.Genre;
import com.moviesfeed.models.MovieBackdrop;
import com.moviesfeed.models.MovieDetail;

import java.util.concurrent.Callable;

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

    private static final int REQUEST_MOVIE_DETAIL_API = 1;
    private static final int REQUEST_UPDATE_MOVIE_DETAIL_DB = 2;
    private static final int REQUEST_LOAD_MOVIE_DETAIL_DB = 3;

    private int movieId;
    private MovieDetail lastResponse;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        createLoadMovieDetailDb();
        createUpdateMovieDetailDb();
        createRequestMovieDetailAPI();
    }

    private void createLoadMovieDetailDb() {
        restartableLatestCache(REQUEST_LOAD_MOVIE_DETAIL_DB, new Func0<Observable<MovieDetail>>() {
                    @Override
                    public Observable<MovieDetail> call() {
                        return Observable.fromCallable(new Callable<MovieDetail>() {
                            @Override
                            public MovieDetail call() throws Exception {
                                return loadMovieDetailDb();
                            }
                        }).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());

                    }
                },
                new Action2<MovieDetailActivity, MovieDetail>() {
                    @Override
                    public void call(MovieDetailActivity activity, MovieDetail movieDetail) {
                        if (movieDetail != null) {
                            Log.d(MovieDetailPresenter.class.getName(), "REQUEST_LOAD_MOVIE_DETAIL_DB success movieDetail != null");
                            activity.requestMovieDetailCallbackSuccess(movieDetail);
                        } else {
                            Log.d(MovieDetailPresenter.class.getName(), "REQUEST_LOAD_MOVIE_DETAIL_DB success movieDetail == null");
                            start(REQUEST_MOVIE_DETAIL_API);
                        }
                    }
                },
                handleError());
    }

    private void createUpdateMovieDetailDb() {
        restartableLatestCache(REQUEST_UPDATE_MOVIE_DETAIL_DB, new Func0<Observable<MovieDetail>>() {
                    @Override
                    public Observable<MovieDetail> call() {
                        return Observable.fromCallable(new Callable<MovieDetail>() {
                            @Override
                            public MovieDetail call() throws Exception {
                                return updateMovieDetailDb(lastResponse);
                            }
                        }).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());

                    }
                },
                new Action2<MovieDetailActivity, MovieDetail>() {
                    @Override
                    public void call(MovieDetailActivity activity, MovieDetail movieDetail) {
                        Log.d(FeedPresenter.class.getName(), "REQUEST_UPDATE_MOVIES_FEED_DB success");
                        activity.requestMovieDetailCallbackSuccess(movieDetail);
                    }
                },
                handleError());
    }

    private void createRequestMovieDetailAPI() {
        restartableLatestCache(REQUEST_MOVIE_DETAIL_API,
                new Func0<Observable<MovieDetail>>() {
                    @Override
                    public Observable<MovieDetail> call() {
                        final Observable<MovieDetail> movieDetailsObservable = App.getServerApi().getMovieDetail(movieId);

                        final Observable<MovieDetail> movieImagesObservable = App.getServerApi().getMovieImages(movieId);

                        return Observable.zip(movieDetailsObservable, movieImagesObservable, new Func2<MovieDetail, MovieDetail, MovieDetail>() {
                            @Override
                            public MovieDetail call(MovieDetail movieDetail, MovieDetail movieImage) {
                                movieDetail.setBackdrops(movieImage.getBackdrops());
                                return movieDetail;
                            }
                        }).subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                },
                new Action2<MovieDetailActivity, MovieDetail>() {
                    @Override
                    public void call(MovieDetailActivity activity, final MovieDetail response) {
                        Log.d(MovieDetailPresenter.class.getName(), "REQUEST_MOVIE_DETAIL_API success");
                        lastResponse = response;
                        start(REQUEST_UPDATE_MOVIE_DETAIL_DB);

                    }
                },
                handleError());
    }


    private Action2 handleError() {
        return new Action2<MovieDetailActivity, Throwable>() {
            @Override
            public void call(MovieDetailActivity activity, Throwable throwable) {
                Log.e(MovieDetailPresenter.class.getName(), throwable.getMessage(), throwable);
                //TODO handle error
            }
        };
    }

    private MovieDetail loadMovieDetailDb() {
        MovieDetail movieDetail = App.getDaoSession().getMovieDetailDao().load((long) this.movieId);
        if (movieDetail != null) {
            movieDetail.setGenres(movieDetail.getGenres());
            movieDetail.setBackdrops(movieDetail.getBackdrops());
        }
        Log.d(MovieDetailPresenter.class.getName(), "loadMovieDetailDb() movieDetail: " + movieDetail);
        return movieDetail;
    }


    private MovieDetail updateMovieDetailDb(MovieDetail movieDetail) {
        for (Genre genre : movieDetail.getGenres()) {
            genre.setMovieDetailId(movieDetail.getId());
        }
        for (MovieBackdrop mb : movieDetail.getBackdrops()) {
            mb.setMovieDetailId(movieDetail.getId());
        }

        App.getDaoSession().getMovieDetailDao().insertOrReplace(movieDetail);
        App.getDaoSession().getGenreDao().insertOrReplaceInTx(movieDetail.getGenres());
        App.getDaoSession().getMovieBackdropDao().insertOrReplaceInTx(movieDetail.getBackdrops());
        return movieDetail;
    }

    public void requestMovieDetail(int movieId) {
        Log.d(MovieDetailPresenter.class.getName(), "requestMovieDetail() movieId: " + movieId);
        this.movieId = movieId;
        start(REQUEST_LOAD_MOVIE_DETAIL_DB);
    }
}
