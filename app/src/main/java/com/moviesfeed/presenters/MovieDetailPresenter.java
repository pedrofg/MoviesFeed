package com.moviesfeed.presenters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.moviesfeed.activities.MovieDetailActivity;
import com.moviesfeed.api.MoviesApi;
import com.moviesfeed.daggercomponents.DaggerAppComponent;
import com.moviesfeed.exceptions.NullResponseException;
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
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.functions.BiConsumer;
import nucleus5.presenter.Factory;
import nucleus5.presenter.RxPresenter;


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

    //create to supply rxjava/android 2.0 which null is not a valid return.
    public static final MovieDetail NOT_FOUND = new MovieDetail();

    @Override
    public void onCreate(Bundle savedState) {
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
                new Factory<Observable<MovieDetail>>() {
                    @Override
                    public Observable<MovieDetail> create() {
                        return api.getMovieDetail(movieId).subscribeOn(ioScheduler)
                                .observeOn(mainThreadScheduler);
                    }
                },
                new BiConsumer<MovieDetailActivity, MovieDetail>() {
                    @Override
                    public void accept(MovieDetailActivity activity, final MovieDetail movieDetail) throws Exception {
                        if (movieDetail != NOT_FOUND) {
                            Log.d(MovieDetailPresenter.class.getName(), "REQUEST_MOVIE_DETAIL_API success");
                            lastResponse = movieDetail;
                            start(REQUEST_UPDATE_MOVIE_DETAIL_DB);
                        } else {
                            handleError().accept(activity, new NullResponseException());
                        }
                    }
                },
                handleError());
    }

    private void createLoadMovieDetailDb() {
        restartableFirst(REQUEST_LOAD_MOVIE_DETAIL_DB,
                new Factory<Observable<MovieDetail>>() {
                    @Override
                    public Observable<MovieDetail> create() {
                        return Observable.fromCallable(new Callable<MovieDetail>() {
                            @Override
                            public MovieDetail call() throws Exception {
                                return loadMovieDetailDb();
                            }
                        }).subscribeOn(ioScheduler)
                                .observeOn(mainThreadScheduler);

                    }
                },
                new BiConsumer<MovieDetailActivity, MovieDetail>() {
                    @Override
                    public void accept(MovieDetailActivity activity, MovieDetail movieDetail) {
                        if (movieDetail != NOT_FOUND) {
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
        restartableFirst(REQUEST_UPDATE_MOVIE_DETAIL_DB,
                new Factory<Observable<MovieDetail>>() {
                    @Override
                    public Observable<MovieDetail> create() {
                        return Observable.fromCallable(new Callable<MovieDetail>() {
                            @Override
                            public MovieDetail call() throws Exception {
                                return updateMovieDetailDb(lastResponse);
                            }
                        }).subscribeOn(ioScheduler)
                                .observeOn(mainThreadScheduler);

                    }
                },
                new BiConsumer<MovieDetailActivity, MovieDetail>() {
                    @Override
                    public void accept(MovieDetailActivity activity, MovieDetail movieDetail) {
                        Log.d(MovieDetailPresenter.class.getName(), "REQUEST_UPDATE_MOVIES_FEED_DB success");
                        activity.requestMovieDetailSuccess(movieDetail);
                    }
                },
                handleError());
    }


    private BiConsumer handleError() {
        return new BiConsumer<MovieDetailActivity, Throwable>() {
            @Override
            public void accept(MovieDetailActivity activity, Throwable throwable) {
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
                Log.d(MovieDetailPresenter.class.getName(), "loadMovieDetailDb() movieDetail: " + movieDetail);
                movieDetail.getGenres();
                movieDetail.getImages().getBackdrops();
                movieDetail.getImages().getPosters();
                movieDetail.getVideos().getVideos();
                movieDetail.getCredits().getCast();
                movieDetail.getCredits().getCrew();
                movieDetail.getSimilarMovies().getMovies();
                movieDetail.getMovieReviews().getReviews();
                return movieDetail;
            } else {
                return NOT_FOUND;
            }
        }
        return NOT_FOUND;
    }


    private MovieDetail updateMovieDetailDb(MovieDetail movieDetail) {

        //set ids with the TmdbID so the database relations will work.
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

        if (movieDetail.getGenres() != null) {
            for (Genre genre : movieDetail.getGenres()) {
                genre.setMovieDetailId(movieDetail.getId());
            }
        }

        if (movieDetail.getImages() != null) {
            if (movieDetail.getImages().getBackdrops() != null) {
                for (MovieBackdrop mb : movieDetail.getImages().getBackdrops()) {
                    mb.setMovieImagesId(movieDetail.getImagesId());
                }
            }

            if (movieDetail.getImages().getPosters() != null) {
                for (MoviePoster mp : movieDetail.getImages().getPosters()) {
                    mp.setMovieImagesId(movieDetail.getImagesId());
                }
            }
        }


        if (movieDetail.getVideos() != null && movieDetail.getVideos().getVideos() != null) {
            Iterator<Video> videoIterator = movieDetail.getVideos().getVideos().iterator();

            while (videoIterator.hasNext()) {
                Video video = videoIterator.next();

                if (Validator.validateVideo(video)) {
                    video.setMovieVideosId(movieDetail.getVideosId());
                } else {
                    videoIterator.remove();
                }
            }
        }

        if (movieDetail.getCredits() != null) {

            if (movieDetail.getCredits().getCrew() != null) {
                Iterator<Crew> crewIterator = movieDetail.getCredits().getCrew().iterator();

                while (crewIterator.hasNext()) {
                    Crew crew = crewIterator.next();

                    if (Validator.validateCrew(crew)) {
                        crew.setCreditsKey(movieDetail.getCreditsId());
                    } else {
                        crewIterator.remove();
                    }
                }

            }
            if (movieDetail.getCredits().getCast() != null) {
                Iterator<Cast> castIterator = movieDetail.getCredits().getCast().iterator();

                while (castIterator.hasNext()) {
                    Cast cast = castIterator.next();

                    if (Validator.validateCast(cast)) {
                        cast.setCreditsKey(movieDetail.getCreditsId());
                    } else {
                        castIterator.remove();
                    }
                }
            }
        }

        if (movieDetail.getSimilarMovies() != null && movieDetail.getSimilarMovies().getMovies() != null) {
            List<Movie> filteredMovies = new ArrayList<>();
            for (Movie movie : movieDetail.getSimilarMovies().getMovies()) {
                if (Validator.validateMovie(movie)) {
                    movie.setSimilarMovieKey(movieDetail.getSimilarId());
                    filteredMovies.add(movie);
                }
            }

            movieDetail.getSimilarMovies().setMovies(filteredMovies);
        }

        if (movieDetail.getMovieReviews() != null && movieDetail.getMovieReviews().getReviews() != null) {
            for (Review review : movieDetail.getMovieReviews().getReviews()) {
                review.setMovieReviewId(movieDetail.getReviewId());
            }
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
