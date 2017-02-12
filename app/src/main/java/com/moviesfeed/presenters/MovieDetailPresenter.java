package com.moviesfeed.presenters;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.moviesfeed.App;
import com.moviesfeed.activities.MovieDetailActivity;
import com.moviesfeed.models.Cast;
import com.moviesfeed.models.Crew;
import com.moviesfeed.models.DaoSession;
import com.moviesfeed.models.Genre;
import com.moviesfeed.models.Movie;
import com.moviesfeed.models.MovieBackdrop;
import com.moviesfeed.models.MovieDetail;
import com.moviesfeed.models.MovieImages;
import com.moviesfeed.models.MoviePoster;
import com.moviesfeed.models.Video;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    private void createRequestMovieDetailAPI() {
        restartableFirst(REQUEST_MOVIE_DETAIL_API,
                new Func0<Observable<MovieDetail>>() {
                    @Override
                    public Observable<MovieDetail> call() {
                        return App.getServerApi().getMovieDetail(movieId).subscribeOn(Schedulers.io())
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

    private void createLoadMovieDetailDb() {
        restartableFirst(REQUEST_LOAD_MOVIE_DETAIL_DB, new Func0<Observable<MovieDetail>>() {
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
                            activity.requestMovieDetailSuccess(movieDetail);
                        } else {
                            Log.d(MovieDetailPresenter.class.getName(), "REQUEST_LOAD_MOVIE_DETAIL_DB success movieDetail == null");
                            start(REQUEST_MOVIE_DETAIL_API);
                        }
                    }
                },
                handleError());
    }

    private void createUpdateMovieDetailDb() {
        restartableFirst(REQUEST_UPDATE_MOVIE_DETAIL_DB, new Func0<Observable<MovieDetail>>() {
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
                        activity.requestMovieDetailSuccess(movieDetail);
                    }
                },
                handleError());
    }


    private Action2 handleError() {
        return new Action2<MovieDetailActivity, Throwable>() {
            @Override
            public void call(MovieDetailActivity activity, Throwable throwable) {
                Log.e(MovieDetailPresenter.class.getName(), throwable.getMessage(), throwable);
                boolean isNetworkError = false;
                if (throwable instanceof IOException && !Util.isNetworkAvailable(activity))
                    isNetworkError = true;

                activity.requestMovieDetailError(isNetworkError);
            }
        };
    }

    private MovieDetail loadMovieDetailDb() {
        MovieDetail movieDetail = App.getDaoSession().getMovieDetailDao().loadDeep((long) this.movieId);
        //Doing that the GreenDao gets() will load the objects while still in another thread.
        if (movieDetail != null) {
            movieDetail.getGenres();
            movieDetail.getImages().getBackdrops();
            movieDetail.getImages().getPosters();
            movieDetail.getVideos().getVideos();
            movieDetail.getCredits().getCast();
            movieDetail.getCredits().getCrew();
            movieDetail.getSimilarMovies().getMovies();
        }
        Log.d(MovieDetailPresenter.class.getName(), "loadMovieDetailDb() movieDetail: " + movieDetail);
        return movieDetail;
    }


    private MovieDetail updateMovieDetailDb(MovieDetail movieDetail) {

        movieDetail.setImagesId(movieDetail.getId());
        movieDetail.getImages().setId(movieDetail.getId());
        movieDetail.setVideosId(movieDetail.getId());
        movieDetail.getVideos().setId(movieDetail.getId());
        movieDetail.setCreditsId(movieDetail.getId());
        movieDetail.getCredits().setIdDb(movieDetail.getId());
        movieDetail.setSimilarId(movieDetail.getId());
        movieDetail.getSimilarMovies().setId(movieDetail.getId());

        for (Genre genre : movieDetail.getGenres()) {
            genre.setMovieDetailId(movieDetail.getId());
        }
        for (MovieBackdrop mb : movieDetail.getImages().getBackdrops()) {
            mb.setMovieImagesId(movieDetail.getImages().getId());
        }
        for (MoviePoster mp : movieDetail.getImages().getPosters()) {
            mp.setMovieImagesId(movieDetail.getImages().getId());
        }
        for (Video video : movieDetail.getVideos().getVideos()) {
            if (Validator.validateVideo(video)) {
                video.setMovieVideosId(movieDetail.getVideos().getId());
            } else {
                movieDetail.getVideos().getVideos().remove(video);
            }

        }
        for (Crew crew : movieDetail.getCredits().getCrew()) {
            crew.setCreditsKey(movieDetail.getCredits().getIdDb());
        }
        for (Cast cast : movieDetail.getCredits().getCast()) {
            cast.setCreditsKey(movieDetail.getCredits().getIdDb());
        }

        List<Movie> filteredMovies = new ArrayList<>();
        for (Movie movie : movieDetail.getSimilarMovies().getMovies()) {
            if (Validator.validateMovie(movie)) {
                movie.setSimilarMovieKey(movieDetail.getSimilarId());
                filteredMovies.add(movie);
            }
        }

        movieDetail.getSimilarMovies().setMovies(filteredMovies);

        App.getDaoSession().getGenreDao().insertOrReplaceInTx(movieDetail.getGenres());
        App.getDaoSession().getMovieImagesDao().insertOrReplace(movieDetail.getImages());
        App.getDaoSession().getMovieBackdropDao().insertOrReplaceInTx(movieDetail.getImages().getBackdrops());
        App.getDaoSession().getMoviePosterDao().insertOrReplaceInTx(movieDetail.getImages().getPosters());
        App.getDaoSession().getMovieVideosDao().insertOrReplaceInTx(movieDetail.getVideos());
        App.getDaoSession().getVideoDao().insertOrReplaceInTx(movieDetail.getVideos().getVideos());
        App.getDaoSession().getCreditsDao().insertOrReplace(movieDetail.getCredits());
        App.getDaoSession().getCrewDao().insertOrReplaceInTx(movieDetail.getCredits().getCrew());
        App.getDaoSession().getCastDao().insertOrReplaceInTx(movieDetail.getCredits().getCast());
        App.getDaoSession().getMovieDetailDao().insertOrReplace(movieDetail);
        App.getDaoSession().getSimilarMoviesDao().insertOrReplace(movieDetail.getSimilarMovies());
        App.getDaoSession().getMovieDao().insertOrReplaceInTx(movieDetail.getSimilarMovies().getMovies());
        return movieDetail;
    }

    public void requestMovieDetail(int movieId) {
        Log.d(MovieDetailPresenter.class.getName(), "requestMovieDetail() movieId: " + movieId);
        this.movieId = movieId;
        start(REQUEST_LOAD_MOVIE_DETAIL_DB);
    }
}
