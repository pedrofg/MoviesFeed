package com.moviesfeed.repository;

import android.content.Context;
import android.util.Log;

import com.moviesfeed.api.Filters;
import com.moviesfeed.api.MoviesApi;
import com.moviesfeed.di.ApiModule;
import com.moviesfeed.di.DaggerAppComponent;
import com.moviesfeed.di.DatabaseModule;
import com.moviesfeed.models.DaoSession;
import com.moviesfeed.models.MovieDao;
import com.moviesfeed.models.MoviesFeed;

import org.greenrobot.greendao.query.QueryBuilder;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by Pedro on 2017-03-13.
 */

public class FeedRepository {

    @Inject
    public MoviesApi api;
    @Inject
    public DaoSession daoSession;

    //create to supply rxjava/android 2.0 which null is not a valid return.
    public static final MoviesFeed NOT_FOUND = new MoviesFeed();


    public FeedRepository(Context context) {
        DaggerAppComponent.builder()
                .apiModule(new ApiModule())
                .databaseModule(new DatabaseModule(context))
                .build().inject(this);
    }

    public MoviesFeed loadMoviesFeedDb(Filters filter) {
        if (verifyDb()) {
            MoviesFeed moviesFeed = daoSession.getMoviesFeedDao().load(filter.getId());
            //Doing that the GreenDao gets() will load the objects while still in another thread.
            if (moviesFeed != null) {
                Log.d(FeedRepository.class.getName(), "loadMoviesFeedDb() moviesFeed: " + moviesFeed);
                moviesFeed.getMovies();
                return moviesFeed;
            } else {
                return NOT_FOUND;
            }
        }
        return NOT_FOUND;
    }

    private boolean verifyDb() {
        boolean isDbValid = daoSession != null && daoSession.getMoviesFeedDao() != null && daoSession.getMovieDao() != null;
        Log.d(FeedRepository.class.getName(), "verifyDb() = " + isDbValid);
        return isDbValid;
    }

    public void updateMoviesFeedDb(MoviesFeed moviesFeed) {
        if (verifyDb()) {
            daoSession.getMoviesFeedDao().insertOrReplace(moviesFeed);
            daoSession.getMovieDao().insertOrReplaceInTx(moviesFeed.getMovies());
        }
    }

    public void deleteMoviesFeedDb(Long id) {
        if (verifyDb()) {
            Log.d(FeedRepository.class.getName(), "deleteMoviesFeedDb() id: " + id);

            QueryBuilder qb = daoSession.getMovieDao().queryBuilder();
            qb.where(MovieDao.Properties.MoviesFeedKey.eq(id));
            qb.buildDelete().executeDeleteWithoutDetachingEntities();
            daoSession.getMoviesFeedDao().deleteByKey(id);
        }
    }


    public Observable<MoviesFeed> loadMoviesFeedBySearch(String query, int page) {
        return api.getSearchMovie(query, page);
    }

    public Observable<MoviesFeed> loadMoviesFeedByGenre(int genre, String date, int page) {
        return api.getMovieByGenre(Integer.valueOf(genre), date, page);
    }

    public Observable<MoviesFeed> loadMoviesFeedByCategory(String category, int page) {
        return api.getMoviesFilterBy(category, page);
    }

    public Observable<MoviesFeed> loadMoviesFeedByDiscover(String sortBy, int page) {
        return api.getDiscoverMovie(sortBy + MoviesApi.DESC, page);
    }

}
