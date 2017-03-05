package com.moviesfeed.presenters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.moviesfeed.activities.FeedActivity;
import com.moviesfeed.api.Filters;
import com.moviesfeed.api.MoviesApi;
import com.moviesfeed.daggercomponents.DaggerAppComponent;
import com.moviesfeed.exceptions.NullResponseException;
import com.moviesfeed.models.DaoSession;
import com.moviesfeed.models.Movie;
import com.moviesfeed.models.MovieDao;
import com.moviesfeed.models.MoviesFeed;
import com.moviesfeed.modules.ApiModule;
import com.moviesfeed.modules.DatabaseModule;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
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
 * Created by Pedro on 8/17/2016.
 */
public class FeedPresenter extends RxPresenter<FeedActivity> {

    public static final int REQUEST_MOVIES_FEED_SORT_BY_API = 1;
    public static final int REQUEST_MOVIES_FEED_FILTER_BY_API = 2;
    public static final int REQUEST_UPDATE_MOVIES_FEED_DB = 3;
    public static final int REQUEST_LOAD_MOVIES_FEED_DB = 4;
    public static final int REQUEST_SEARCH_MOVIES_API = 5;
    public static final int FIRST_PAGE = 1;
    public static final String DESC = ".desc";

    private Filters filterBy = Filters.POPULARITY;
    private MoviesFeed moviesFeed;
    private MoviesFeed lastResponse;
    private int page = 1;

    private boolean isRequestingNextPage;
    private boolean isUpdating;
    private boolean loadingItems;
    private String searchQuery;
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
    public static final MoviesFeed NOT_FOUND = new MoviesFeed();

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        createRequestMoviesFeedSortByAPI();
        createRequestMoviesFeedFilterByAPI();
        createUpdateMoviesFeed();
        createLoadMoviesFeedDb();
        createRequestSearchMoviesAPI();

    }

    public void init(Context context) {
        DaggerAppComponent.builder()
                .apiModule(new ApiModule())
                .databaseModule(new DatabaseModule(context)).build().inject(this);
    }


    private void createRequestMoviesFeedFilterByAPI() {

        restartableFirst(REQUEST_MOVIES_FEED_FILTER_BY_API, () -> api.getMoviesFilterBy(filterBy.toString(), page)
                        .subscribeOn(ioScheduler)
                        .observeOn(mainThreadScheduler),
                handleRequestMovieFeedAPISuccess(),
                handleError());
    }

    private void createRequestMoviesFeedSortByAPI() {
        restartableFirst(REQUEST_MOVIES_FEED_SORT_BY_API,
                () -> {
                    if (filterBy == Filters.REVENUE) {
                        return api.getDiscoverMovie(filterBy.toString() + FeedPresenter.DESC, page)
                                .subscribeOn(ioScheduler)
                                .observeOn(mainThreadScheduler);
                    } else {
                        return api.getMovieByGenre(Integer.valueOf(filterBy.toString()), getFormattedDate(), page)
                                .subscribeOn(ioScheduler)
                                .observeOn(mainThreadScheduler);
                    }
                },
                handleRequestMovieFeedAPISuccess(),
                handleError());
    }

    private void createRequestSearchMoviesAPI() {
        restartableFirst(REQUEST_SEARCH_MOVIES_API,
                () -> api.getSearchMovie(searchQuery, page)
                        .subscribeOn(ioScheduler)
                        .observeOn(mainThreadScheduler),
                handleRequestMovieFeedAPISuccess(),
                handleError());

    }

    private void createUpdateMoviesFeed() {
        restartableFirst(REQUEST_UPDATE_MOVIES_FEED_DB,
                () -> Observable.fromCallable(() -> updateMoviesFeedDb(lastResponse))
                        .subscribeOn(ioScheduler)
                        .observeOn(mainThreadScheduler),
                (activity, movies) -> {
                    Log.d(FeedPresenter.class.getName(), "REQUEST_UPDATE_MOVIES_FEED_DB success");
                    loadingItems = false;
                    activity.requestMoviesFeedSuccess(moviesFeed.cloneMoviesFeed(), isRequestingNextPage, isUpdating, movies.size(), moviesFeed.getAllMoviesDownloaded());

                    isUpdating = false;
                },
                handleError());
    }

    private void createLoadMoviesFeedDb() {
        restartableFirst(REQUEST_LOAD_MOVIES_FEED_DB,
                () -> Observable.fromCallable(this::loadMoviesFeedDb)
                        .subscribeOn(ioScheduler)
                        .observeOn(mainThreadScheduler),
                (activity, moviesFeed1) -> {
                    if (moviesFeed1 != NOT_FOUND) {
                        Log.d(FeedPresenter.class.getName(), "REQUEST_LOAD_MOVIES_FEED_DB success moviesFeed != null");
                        activity.requestMoviesFeedSuccess(moviesFeed1.cloneMoviesFeed(), false, false, moviesFeed1.getMovies().size(), moviesFeed1.getAllMoviesDownloaded());
                    } else {
                        Log.d(FeedPresenter.class.getName(), "REQUEST_LOAD_MOVIES_FEED_DB success moviesFeed == null");
                        requestAPI(false);
                    }
                },
                handleError());
    }

    private BiConsumer handleRequestMovieFeedAPISuccess() {
        return (BiConsumer<FeedActivity, MoviesFeed>) (feedActivity, moviesFeed) -> {
            Log.d(FeedPresenter.class.getName(), "handleRequestMovieFeedAPISuccess()");
            if (moviesFeed != NOT_FOUND) {
                lastResponse = moviesFeed;
                start(REQUEST_UPDATE_MOVIES_FEED_DB);
            } else {
                handleError().accept(feedActivity, new NullResponseException());
            }
        };
    }

    private BiConsumer handleError() {
        return (BiConsumer<FeedActivity, Throwable>) (feedActivity, throwable) -> {
            Log.e(FeedPresenter.class.getName(), "handleError() " + throwable.getMessage(), throwable);

            if (isRequestingNextPage)
                page--;
            isRequestingNextPage = false;
            isUpdating = false;
            loadingItems = false;
            boolean isNetworkError = false;
            if (throwable instanceof IOException && !Util.isNetworkAvailable(feedActivity))
                isNetworkError = true;

            if (moviesFeed == null) {
                feedActivity.requestMoviesFeedError(false, isNetworkError);
            } else {
                feedActivity.requestMoviesFeedError(true, isNetworkError);
            }
        };
    }

    private boolean checkAllMoviesDownloaded(MoviesFeed response) {
        return response.getPage() == response.getTotalPages() || response.getMovies().size() == 0;
    }

    private List<Movie> updateMoviesFeedDb(final MoviesFeed response) {
        if (this.moviesFeed == null) {
            this.moviesFeed = new MoviesFeed();
        }
        this.moviesFeed.setFilterAndId(this.filterBy);
        this.moviesFeed.setPage(response.getPage());

        if (checkAllMoviesDownloaded(response)) {
            this.moviesFeed.setAllMoviesDownloaded(true);
            Log.d(FeedPresenter.class.getName(), "updateMoviesFeedDb() checkAllMoviesDownloaded == true");
        }

        final List<Movie> filteredMovies = new ArrayList<>();

        if (response.getMovies().size() > 0) {
            for (Movie movie : response.getMovies()) {
                if (Validator.validateMovie(movie)) {
                    filteredMovies.add(movie);
                    movie.setMoviesFeedKey(this.moviesFeed.getId());
                }
            }

            Log.d(FeedPresenter.class.getName(), "updateMoviesFeedDb() filtered movies: " + filteredMovies.size());
        }

        Log.d(FeedPresenter.class.getName(), "updateMoviesFeedDb() page: " + page);

        if (this.isRequestingNextPage) {
            Log.d(FeedPresenter.class.getName(), "updateMoviesFeedDb() isRequestingNextPage");
            this.moviesFeed.getMovies().addAll(filteredMovies);
        } else if (page == FIRST_PAGE) {
            Log.d(FeedPresenter.class.getName(), "updateMoviesFeedDb() FIRST_PAGE");
            this.moviesFeed.setMovies(filteredMovies);
            this.moviesFeed.setTotalPages(response.getTotalPages());
        }

        if (isUpdating) {
            Log.d(FeedPresenter.class.getName(), "updateMoviesFeedDb() isUpdating");
            deleteMoviesFeedDb(this.moviesFeed.getId());
        }

        if (verifyDb()) {
            daoSession.getMoviesFeedDao().insertOrReplace(this.moviesFeed);
            daoSession.getMovieDao().insertOrReplaceInTx(this.moviesFeed.getMovies());
        }

        return filteredMovies;
    }

    private void deleteMoviesFeedDb(Long id) {
        if (verifyDb()) {
            Log.d(FeedPresenter.class.getName(), "deleteMoviesFeedDb() id: " + id);

            QueryBuilder qb = daoSession.getMovieDao().queryBuilder();
            qb.where(MovieDao.Properties.MoviesFeedKey.eq(id));
            qb.buildDelete().executeDeleteWithoutDetachingEntities();
            daoSession.getMoviesFeedDao().deleteByKey(id);
        }
    }

    private MoviesFeed loadMoviesFeedDb() {
        if (verifyDb()) {
            MoviesFeed moviesFeed = daoSession.getMoviesFeedDao().load(filterBy.getId());
            //Doing that the GreenDao gets() will load the objects while still in another thread.
            if (moviesFeed != null) {
                Log.d(FeedPresenter.class.getName(), "loadMoviesFeedDb() moviesFeed: " + moviesFeed);
                moviesFeed.getMovies();
                this.moviesFeed = moviesFeed;
                this.page = moviesFeed.getPage();
                return moviesFeed;
            } else {
                return NOT_FOUND;
            }
        }
        return NOT_FOUND;
    }

    private boolean verifyDb() {
        boolean isDbValid = daoSession != null && daoSession.getMoviesFeedDao() != null && daoSession.getMovieDao() != null;
        Log.d(FeedPresenter.class.getName(), "verifyDb() = " + isDbValid);
        return isDbValid;
    }

    private void requestAPI(boolean isRequestingNextPage) {
        if (!loadingItems) {
            Log.d(FeedPresenter.class.getName(), "requestAPI() loadingItems = false");
            this.loadingItems = true;
            this.isRequestingNextPage = isRequestingNextPage;

            if (isRequestingNextPage) {
                this.page++;
            } else {
                this.page = FIRST_PAGE;
            }

            stop(REQUEST_MOVIES_FEED_FILTER_BY_API);
            stop(REQUEST_MOVIES_FEED_SORT_BY_API);
            stop(REQUEST_SEARCH_MOVIES_API);

            if (this.filterBy == Filters.SEARCH) {
                start(REQUEST_SEARCH_MOVIES_API);
            } else if (this.filterBy.getId() >= Filters.REVENUE.getId()) {
                start(REQUEST_MOVIES_FEED_SORT_BY_API);
            } else {
                start(REQUEST_MOVIES_FEED_FILTER_BY_API);
            }

            Log.d(FeedPresenter.class.getName(), "requestAPI() isRequestingNextPage: " + this.isRequestingNextPage + " page: " + this.page + " filterBy: " + this.filterBy);
        } else {
            Log.d(FeedPresenter.class.getName(), "requestAPI() loadingItems = true");
        }
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

    public void requestSearchMoviesFeed(String query) {
        this.searchQuery = query;
        this.filterBy = Filters.SEARCH;
        this.moviesFeed = null;
        requestAPI(false);
    }

    public void refreshMoviesFeed() {
        this.moviesFeed = null;
        this.isUpdating = true;
        Log.d(FeedPresenter.class.getName(), "refreshMoviesFeed() isUpdating: " + this.isUpdating);
        requestAPI(false);
    }

    public void requestNextPage() {
        Log.d(FeedPresenter.class.getName(), "requestNextPage()");
        requestAPI(true);
    }

    public void tryAgain() {
        if (this.filterBy == Filters.SEARCH) {
            requestSearchMoviesFeed(this.searchQuery);
        } else {
            requestMoviesFeed(this.filterBy);
        }
    }

    public boolean isSameFilter(Filters filter) {
        return this.filterBy == filter;
    }

}
