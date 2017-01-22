package com.moviesfeed.presenters;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.moviesfeed.App;
import com.moviesfeed.activities.FeedActivity;
import com.moviesfeed.api.Filters;
import com.moviesfeed.models.Movie;
import com.moviesfeed.models.MovieDao;
import com.moviesfeed.models.MoviesFeed;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import nucleus.presenter.RxPresenter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

/**
 * Created by Pedro on 8/17/2016.
 */
public class FeedPresenter extends RxPresenter<FeedActivity> {

    public static final int REQUEST_MOVIES_FEED_SORT_BY_API = 1;
    public static final int REQUEST_MOVIES_FEED_FILTER_BY_API = 2;
    public static final int REQUEST_UPDATE_MOVIES_FEED_DB = 3;
    public static final int REQUEST_LOAD_MOVIES_FEED_DB = 4;
    public static final int FIRST_PAGE = 1;
    public static final String DESC = ".desc";

    private Filters filterBy = Filters.POPULARITY;
    private MoviesFeed moviesFeed;
    private MoviesFeed lastResponse;
    private int page = 1;


    private boolean isRequestingNextPage;
    private boolean isUpdating;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        createRequestMoviesFeedSortByAPI();
        createRequestMoviesFeedFilterByAPI();
        createUpdateMoviesFeed();
        createLoadMoviesFeedDb();
    }

    private void createRequestMoviesFeedFilterByAPI() {
        restartableFirst(REQUEST_MOVIES_FEED_FILTER_BY_API,
                new Func0<Observable<MoviesFeed>>() {
                    @Override
                    public Observable<MoviesFeed> call() {
                        return App.getServerApi().getMoviesFilterBy(filterBy.toString(), page)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                },
                handleRequestMovieFeedAPISuccess(),
                handleError());

    }

    private void createRequestMoviesFeedSortByAPI() {
        restartableFirst(REQUEST_MOVIES_FEED_SORT_BY_API,
                new Func0<Observable<MoviesFeed>>() {
                    @Override
                    public Observable<MoviesFeed> call() {
                        if (filterBy.getId() >= Filters.ACTION.getId()) {
                            return App.getServerApi().getMovieByGenre(Integer.valueOf(filterBy.toString()), getFormattedDate(), page)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread());
                        } else {
                            return App.getServerApi().getDiscoverMovie(filterBy.toString() + FeedPresenter.DESC, page)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread());
                        }
                    }
                },
                handleRequestMovieFeedAPISuccess(),
                handleError());
    }

    private void createUpdateMoviesFeed() {
        restartableFirst(REQUEST_UPDATE_MOVIES_FEED_DB,
                new Func0<Observable<List<Movie>>>() {
                    @Override
                    public Observable<List<Movie>> call() {

                        return Observable.fromCallable(new Callable<List<Movie>>() {
                            @Override
                            public List<Movie> call() throws Exception {
                                return updateMoviesFeedDb(lastResponse);
                            }
                        }).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());

                    }
                },
                new Action2<FeedActivity, List<Movie>>() {
                    @Override
                    public void call(FeedActivity activity, List<Movie> movies) {
                        Log.d(FeedPresenter.class.getName(), "REQUEST_UPDATE_MOVIES_FEED_DB success");
                        activity.requestMoviesFeedSuccess(moviesFeed.clone(), isRequestingNextPage, isUpdating, movies.size());

                        isUpdating = false;
                        isRequestingNextPage = false;
                    }
                },
                handleError());
    }

    private void createLoadMoviesFeedDb() {
        restartableFirst(REQUEST_LOAD_MOVIES_FEED_DB, new Func0<Observable<MoviesFeed>>() {
                    @Override
                    public Observable<MoviesFeed> call() {
                        return Observable.fromCallable(new Callable<MoviesFeed>() {
                            @Override
                            public MoviesFeed call() {
                                return loadMoviesFeedDb();
                            }
                        }).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());

                    }
                },
                new Action2<FeedActivity, MoviesFeed>() {
                    @Override
                    public void call(FeedActivity activity, MoviesFeed moviesFeed) {
                        if (moviesFeed != null) {
                            Log.d(FeedPresenter.class.getName(), "REQUEST_LOAD_MOVIES_FEED_DB success moviesFeed != null");
                            activity.requestMoviesFeedSuccess(moviesFeed.clone(), false, false, moviesFeed.getMovies().size());
                        } else {
                            Log.d(FeedPresenter.class.getName(), "REQUEST_LOAD_MOVIES_FEED_DB success moviesFeed == null");
                            requestAPI(false);
                        }
                    }
                },
                handleError());
    }

    private Action2 handleRequestMovieFeedAPISuccess() {
        return new Action2<FeedActivity, MoviesFeed>() {
            @Override
            public void call(final FeedActivity activity, final MoviesFeed response) {
                Log.d(FeedPresenter.class.getName(), "handleRequestMovieFeedAPISuccess()");
                lastResponse = response;
                start(REQUEST_UPDATE_MOVIES_FEED_DB);
            }
        };
    }

    private Action2 handleError() {
        return new Action2<FeedActivity, Throwable>() {
            @Override
            public void call(FeedActivity activity, Throwable throwable) {
                Log.e(FeedPresenter.class.getName(), "handleError() " + throwable.getMessage(), throwable);
                boolean isNetworkError = false;
                if (throwable instanceof IOException && !Util.isNetworkAvailable(activity))
                    isNetworkError = true;

                if (moviesFeed == null) {
                    activity.requestMoviesFeedError(false, isNetworkError);
                } else {
                    activity.requestMoviesFeedError(true, isNetworkError);
                }
            }
        };
    }

    private List<Movie> updateMoviesFeedDb(final MoviesFeed response) {
        response.setFilterAndId(filterBy);

        final List<Movie> filteredMovies = new ArrayList<>();

        for (Movie movie : response.getMovies()) {
            if (validateMovie(movie)) {
                filteredMovies.add(movie);
                movie.setMoviesFeedKey(response.getId());
            }
        }

        Log.d(FeedPresenter.class.getName(), "updateMoviesFeedDb() filtered movies: " + filteredMovies.size());

        if (isRequestingNextPage) {
            Log.d(FeedPresenter.class.getName(), "updateMoviesFeedDb() isRequestingNextPage");
            this.moviesFeed.getMovies().addAll(filteredMovies);
            this.moviesFeed.setPage(page);
        } else if (page == FIRST_PAGE) {
            Log.d(FeedPresenter.class.getName(), "updateMoviesFeedDb() FIRST_PAGE");
            response.setMovies(filteredMovies);
            this.moviesFeed = response;
        }

        if (isUpdating) {
            Log.d(FeedPresenter.class.getName(), "updateMoviesFeedDb() isUpdating");
            deleteMoviesFeedDb(this.moviesFeed.getId());
        }

        App.getDaoSession().getMoviesFeedDao().insertOrReplace(this.moviesFeed);
        App.getDaoSession().getMovieDao().insertOrReplaceInTx(this.moviesFeed.getMovies());

        return filteredMovies;
    }

    private void deleteMoviesFeedDb(Long id) {
        Log.d(FeedPresenter.class.getName(), "deleteMoviesFeedDb() id: " + id);
        QueryBuilder qb = App.getDaoSession().getMovieDao().queryBuilder();
        qb.where(MovieDao.Properties.MoviesFeedKey.eq(id));
        qb.buildDelete().executeDeleteWithoutDetachingEntities();

        App.getDaoSession().getMoviesFeedDao().deleteByKey(id);
    }

    private MoviesFeed loadMoviesFeedDb() {
        MoviesFeed moviesFeed = App.getDaoSession().getMoviesFeedDao().load(filterBy.getId());
        if (moviesFeed != null) {
            moviesFeed.setMovies(moviesFeed.getMovies());
            this.moviesFeed = moviesFeed;
            this.page = moviesFeed.getPage();
        }
        Log.d(FeedPresenter.class.getName(), "loadMoviesFeedDb() moviesFeed: " + moviesFeed);
        return moviesFeed;
    }

    private void requestAPI(boolean isRequestingNextPage) {
        this.isRequestingNextPage = isRequestingNextPage;

        if (isRequestingNextPage) {
            this.page = ++this.page;
        } else {
            this.page = 1;
        }


        stop(REQUEST_MOVIES_FEED_FILTER_BY_API);
        stop(REQUEST_MOVIES_FEED_SORT_BY_API);
        if (this.filterBy.getId() >= Filters.REVENUE.getId()) {
            start(REQUEST_MOVIES_FEED_SORT_BY_API);
        } else {
            start(REQUEST_MOVIES_FEED_FILTER_BY_API);
        }

        Log.d(FeedPresenter.class.getName(), "requestAPI() isRequestingNextPage: " + this.isRequestingNextPage + " page: " + this.page + " filterBy: " + this.filterBy);
    }

    private boolean validateMovie(Movie movie) {
        if (TextUtils.isEmpty(movie.getOverview()) ||
                TextUtils.isEmpty(movie.getTitle()) ||
                TextUtils.isEmpty(movie.getPosterPath()) ||
                TextUtils.isEmpty(movie.getBackdropPath())) {
            return false;
        }
        return true;
    }

    private String getFormattedDate() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1; //+1 is needed by the fact that month starts in 0
        int day = c.get(Calendar.DAY_OF_MONTH);

        String formattedDate = year + "-" + month + "-" + day;

        Log.d(FeedPresenter.class.getName(), "getFormattedDate() date: " + formattedDate);
        return formattedDate;
    }

    public void requestMoviesFeed(Filters filterBy) {
        Log.d(FeedPresenter.class.getName(), "requestMoviesFeed() filterBy: " + filterBy);
        this.filterBy = filterBy;
        //set movies feed to null because if in case of error handleError() will know if there was a cache or not.
        this.moviesFeed = null;
        start(REQUEST_LOAD_MOVIES_FEED_DB);
    }

    public void refreshMoviesFeed() {
        this.isUpdating = true;
        Log.d(FeedPresenter.class.getName(), "refreshMoviesFeed() isUpdating: " + this.isUpdating);
        requestAPI(false);
    }

    public void requestNextPage() {
        Log.d(FeedPresenter.class.getName(), "requestNextPage()");
        requestAPI(true);
    }

    public Filters getCurrentFilter() {
        return filterBy;
    }


}
