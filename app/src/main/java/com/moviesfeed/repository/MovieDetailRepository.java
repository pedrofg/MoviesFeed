package com.moviesfeed.repository;

import android.content.Context;
import android.util.Log;

import com.moviesfeed.api.MoviesApi;
import com.moviesfeed.di.ApiModule;
import com.moviesfeed.di.DaggerAppComponent;
import com.moviesfeed.di.DatabaseModule;
import com.moviesfeed.models.DaoSession;
import com.moviesfeed.models.MovieDetail;
import com.moviesfeed.ui.presenters.MovieDetailPresenter;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by Pedro on 2017-03-23.
 */

public class MovieDetailRepository {

    @Inject
    public MoviesApi api;
    @Inject
    public DaoSession daoSession;

    //create to supply rxjava/android 2.0 which null is not a valid return.
    public static final MovieDetail NOT_FOUND = new MovieDetail();


    public MovieDetailRepository(Context context) {
        DaggerAppComponent.builder()
                .apiModule(new ApiModule())
                .databaseModule(new DatabaseModule(context))
                .build().inject(this);
    }

    public Observable<MovieDetail> loadMovieDetailApi(int movieId) {
        return this.api.getMovieDetail(movieId);

    }

    public void updateMovieDetailDb(MovieDetail movieDetail) {
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
    }

    public MovieDetail loadMovieDetailDb(Long movieId) {
        if (verifyDb()) {
            MovieDetail movieDetail = daoSession.getMovieDetailDao().loadDeep(movieId);
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

        Log.d(MovieDetailRepository.class.getName(), "verifyDb() = " + isDbValid);
        return isDbValid;
    }
}
