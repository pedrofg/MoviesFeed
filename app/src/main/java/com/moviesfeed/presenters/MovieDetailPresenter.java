package com.moviesfeed.presenters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.moviesfeed.activities.MovieDetailActivity;
import com.moviesfeed.api.MoviesApi;
import com.moviesfeed.daggercomponents.DaggerAppComponent;
import com.moviesfeed.models.Cast;
import com.moviesfeed.models.Crew;
import com.moviesfeed.models.DaoSession;
import com.moviesfeed.models.Genre;
import com.moviesfeed.models.Movie;
import com.moviesfeed.models.MovieBackdrop;
import com.moviesfeed.models.MovieDetail;
import com.moviesfeed.models.MoviePoster;
import com.moviesfeed.models.Review;
import com.moviesfeed.models.Video;
import com.moviesfeed.modules.ApiModule;
import com.moviesfeed.modules.DatabaseModule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Named;

import nucleus.presenter.RxPresenter;
import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action2;
import rx.functions.Func0;
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
    @Inject
    public MoviesApi api;
    @Inject
    public DaoSession daoSession;
    @Inject
    @Named(ApiModule.IO_SCHEDULER)
    public Scheduler ioScheduler;
    @Inject
    @Named(ApiModule.MAIN_THREAD_SCHEDULER)
    public Scheduler mainThreadScheduler;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        createLoadMovieDetailDb();
        createUpdateMovieDetailDb();
        createRequestMovieDetailAPI();
    }

    public void init(Context context) {
        DaggerAppComponent.builder()
                .apiModule(new ApiModule())
                .databaseModule(new DatabaseModule(context)).build().inject(this);
    }

    private void createRequestMovieDetailAPI() {
        restartableFirst(REQUEST_MOVIE_DETAIL_API,
                new Func0<Observable<MovieDetail>>() {
                    @Override
                    public Observable<MovieDetail> call() {
                        return api.getMovieDetail(movieId).subscribeOn(ioScheduler)
                                .observeOn(mainThreadScheduler);
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
                        }).subscribeOn(ioScheduler)
                                .observeOn(mainThreadScheduler);

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
                        }).subscribeOn(ioScheduler)
                                .observeOn(mainThreadScheduler);

                    }
                },
                new Action2<MovieDetailActivity, MovieDetail>() {
                    @Override
                    public void call(MovieDetailActivity activity, MovieDetail movieDetail) {
                        Log.d(MovieDetailPresenter.class.getName(), "REQUEST_UPDATE_MOVIES_FEED_DB success");
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
        if (verifyDb()) {
            MovieDetail movieDetail = daoSession.getMovieDetailDao().loadDeep((long) this.movieId);
            //Doing that the GreenDao gets() will load the objects while still in another thread.
            if (movieDetail != null) {
                movieDetail.getGenres();
                movieDetail.getImages().getBackdrops();
                movieDetail.getImages().getPosters();
                movieDetail.getVideos().getVideos();
                movieDetail.getCredits().getCast();
                movieDetail.getCredits().getCrew();
                movieDetail.getSimilarMovies().getMovies();
                movieDetail.getMovieReviews().getReviews();
            }
            Log.d(MovieDetailPresenter.class.getName(), "loadMovieDetailDb() movieDetail: " + movieDetail);
            return movieDetail;
        }
        return null;
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
        movieDetail.getMovieReviews().setIdDB(movieDetail.getId());
        movieDetail.setReviewId(movieDetail.getId());

        for (Genre genre : movieDetail.getGenres()) {
            genre.setMovieDetailId(movieDetail.getId());
        }
        for (MovieBackdrop mb : movieDetail.getImages().getBackdrops()) {
            mb.setMovieImagesId(movieDetail.getImagesId());
        }
        for (MoviePoster mp : movieDetail.getImages().getPosters()) {
            mp.setMovieImagesId(movieDetail.getImagesId());
        }
        for (Video video : movieDetail.getVideos().getVideos()) {
            if (Validator.validateVideo(video)) {
                video.setMovieVideosId(movieDetail.getVideosId());
            } else {
                movieDetail.getVideos().getVideos().remove(video);
            }

        }
        for (Crew crew : movieDetail.getCredits().getCrew()) {
            crew.setCreditsKey(movieDetail.getCreditsId());
        }
        for (Cast cast : movieDetail.getCredits().getCast()) {
            cast.setCreditsKey(movieDetail.getCreditsId());
        }

        List<Movie> filteredMovies = new ArrayList<>();
        for (Movie movie : movieDetail.getSimilarMovies().getMovies()) {
            if (Validator.validateMovie(movie)) {
                movie.setSimilarMovieKey(movieDetail.getSimilarId());
                filteredMovies.add(movie);
            }
        }

        movieDetail.getSimilarMovies().setMovies(filteredMovies);

        for (Review review : movieDetail.getMovieReviews().getReviews()) {
            review.setMovieReviewId(movieDetail.getReviewId());
        }

        if (verifyDb()) {
            Log.d(MovieDetailPresenter.class.getName(), "daoSession != null");
            daoSession.getGenreDao().insertOrReplaceInTx(movieDetail.getGenres());
            daoSession.getMovieImagesDao().insertOrReplace(movieDetail.getImages());
            daoSession.getMovieBackdropDao().insertOrReplaceInTx(movieDetail.getImages().getBackdrops());
            daoSession.getMoviePosterDao().insertOrReplaceInTx(movieDetail.getImages().getPosters());
            daoSession.getMovieVideosDao().insertOrReplaceInTx(movieDetail.getVideos());
            daoSession.getVideoDao().insertOrReplaceInTx(movieDetail.getVideos().getVideos());
            daoSession.getCreditsDao().insertOrReplace(movieDetail.getCredits());
            daoSession.getCrewDao().insertOrReplaceInTx(movieDetail.getCredits().getCrew());
            daoSession.getCastDao().insertOrReplaceInTx(movieDetail.getCredits().getCast());
            daoSession.getMovieDetailDao().insertOrReplace(movieDetail);
            daoSession.getSimilarMoviesDao().insertOrReplace(movieDetail.getSimilarMovies());
            daoSession.getMovieDao().insertOrReplaceInTx(movieDetail.getSimilarMovies().getMovies());
            daoSession.getMovieReviewsDao().insertOrReplace(movieDetail.getMovieReviews());
            daoSession.getReviewDao().insertOrReplaceInTx(movieDetail.getMovieReviews().getReviews());
        } else {
            Log.d(MovieDetailPresenter.class.getName(), "daoSession null");
        }
        return movieDetail;
    }

    private boolean verifyDb() {
        boolean isDbValid = daoSession != null &&
                daoSession.getGenreDao() != null &&
                daoSession.getMovieImagesDao() != null &&
                daoSession.getMovieBackdropDao() != null &&
                daoSession.getMoviePosterDao() != null &&
                daoSession.getMovieVideosDao() != null &&
                daoSession.getVideoDao() != null &&
                daoSession.getCreditsDao() != null &&
                daoSession.getCrewDao() != null &&
                daoSession.getCastDao() != null &&
                daoSession.getMovieDetailDao() != null &&
                daoSession.getSimilarMoviesDao() != null &&
                daoSession.getMovieDao() != null &&
                daoSession.getMovieReviewsDao() != null &&
                daoSession.getReviewDao() != null;

        Log.d(MovieDetailPresenter.class.getName(), "verifyDb() = " + isDbValid);
        return isDbValid;
    }

    public void requestMovieDetail(int movieId) {
        Log.d(MovieDetailPresenter.class.getName(), "requestMovieDetail() movieId: " + movieId);
        this.movieId = movieId;
        start(REQUEST_LOAD_MOVIE_DETAIL_DB);
    }
}
