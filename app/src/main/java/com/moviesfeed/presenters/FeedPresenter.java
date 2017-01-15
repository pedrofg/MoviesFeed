package com.moviesfeed.presenters;

import android.os.Bundle;
import android.util.Log;

import com.moviesfeed.App;
import com.moviesfeed.activities.FeedActivity;
import com.moviesfeed.api.Filters;
import com.moviesfeed.models.Movie;
import com.moviesfeed.models.MovieDao;
import com.moviesfeed.models.MoviesFeed;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;

import nucleus.presenter.RxPresenter;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

/**
 * Created by Pedro on 8/17/2016.
 */
public class FeedPresenter extends RxPresenter<FeedActivity> {

    public static final int REQUEST_MOVIES_SORT_BY = 1;
    public static final int REQUEST_MOVIES_FILTER = 2;
    public static final int REQUEST_UPDATE_MOVIES_FEED = 3;
    public static final int REQUEST_LOAD_MOVIES_FEED = 4;
    public static final int FIRST_PAGE = 1;
    public static final String DESC = ".desc";

    private Filters filterBy = Filters.POPULARITY;
    private MoviesFeed moviesFeed;
    private MoviesFeed lastResponse;
    private int page = 1;

    private boolean isRequestingNextPage;
    private boolean isUpdating;

    private Subscription updateMoviesFeedSubscription;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        createRequestMoviesFeedSortBy();
        createRequestMoviesFeedFiltered();
        createUpdateMoviesFeed();
        createLoadMoviesFeed();
    }

    private void createRequestMoviesFeedFiltered() {
        restartableLatestCache(REQUEST_MOVIES_FILTER,
                new Func0<Observable<MoviesFeed>>() {
                    @Override
                    public Observable<MoviesFeed> call() {
                        return App.getServerApi().getFilteredMovies(filterBy.toString(), page)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                },
                handleRequestMovieFeedSuccess(),
                handleRequestFailure());

    }

    private void createRequestMoviesFeedSortBy() {
        restartableLatestCache(REQUEST_MOVIES_SORT_BY,
                new Func0<Observable<MoviesFeed>>() {
                    @Override
                    public Observable<MoviesFeed> call() {
                        return App.getServerApi().getMoviesSortBy(filterBy.toString() + FeedPresenter.DESC,
                                getFormattedDate(), page)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                },
                handleRequestMovieFeedSuccess(),
                handleRequestFailure());
    }

    private void createUpdateMoviesFeed() {
        restartableLatestCache(REQUEST_UPDATE_MOVIES_FEED, new Func0<Observable<List<Movie>>>() {
                    @Override
                    public Observable<List<Movie>> call() {

                        return Observable.fromCallable(new Callable<List<Movie>>() {
                            @Override
                            public List<Movie> call() throws Exception {
                                return updateMoviesFeed(lastResponse);
                            }
                        }).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());

                    }
                },
                new Action2<FeedActivity, List<Movie>>() {
                    @Override
                    public void call(FeedActivity activity, List<Movie> movies) {
                        Log.d(FeedPresenter.class.getName(), "REQUEST_UPDATE_MOVIES_FEED success");
                        activity.requestMoviesFeedCallback(moviesFeed, isRequestingNextPage, isUpdating, movies.size());

                        isUpdating = false;
                        isRequestingNextPage = false;
                    }
                },
                handleRequestFailure());
    }

    private void createLoadMoviesFeed() {
        restartableLatestCache(REQUEST_LOAD_MOVIES_FEED, new Func0<Observable<MoviesFeed>>() {
                    @Override
                    public Observable<MoviesFeed> call() {
                        return Observable.fromCallable(new Callable<MoviesFeed>() {
                            @Override
                            public MoviesFeed call() throws Exception {
                                return loadMoviesFeed();
                            }
                        }).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());

                    }
                },
                new Action2<FeedActivity, MoviesFeed>() {
                    @Override
                    public void call(FeedActivity activity, MoviesFeed moviesFeed) {
                        if (moviesFeed != null) {
                            Log.d(FeedPresenter.class.getName(), "REQUEST_LOAD_MOVIES_FEED success moviesFeed != null");
                            activity.requestMoviesFeedCallback(moviesFeed, false, false, moviesFeed.getMovies().size());
                        } else {
                            Log.d(FeedPresenter.class.getName(), "REQUEST_LOAD_MOVIES_FEED success moviesFeed == null");
                            request(false);
                        }
                    }
                },
                handleRequestFailure());
    }

    private Action2 handleRequestMovieFeedSuccess() {
        return new Action2<FeedActivity, MoviesFeed>() {
            @Override
            public void call(final FeedActivity activity, final MoviesFeed response) {
                Log.d(FeedPresenter.class.getName(), "handleRequestMovieFeedSuccess()");
                lastResponse = response;
                start(REQUEST_UPDATE_MOVIES_FEED);
            }
        };
    }

    private Action2 handleRequestFailure() {
        return new Action2<FeedActivity, Throwable>() {
            @Override
            public void call(FeedActivity activity, Throwable throwable) {
                Log.e(FeedPresenter.class.getName(), throwable.getMessage(), throwable);
                //TODO handle error
            }
        };
    }

    //called by another thread.
    private List<Movie> updateMoviesFeed(final MoviesFeed response) {
        response.setFilterAndId(filterBy);

        final List<Movie> filteredMovies = new ArrayList<>();

        for (Movie movie : response.getMovies()) {
            if (movie.getPosterPath() != null) {
                filteredMovies.add(movie);
                movie.setMoviesFeedKey(response.getId());
            }
        }

        Log.d(FeedPresenter.class.getName(), "updateMoviesFeed() filtered movies: " + filteredMovies.size());

        if (isRequestingNextPage) {
            Log.d(FeedPresenter.class.getName(), "updateMoviesFeed() isRequestingNextPage");
            moviesFeed.getMovies().addAll(filteredMovies);
            moviesFeed.setPage(page);
        } else if (page == FIRST_PAGE) {
            Log.d(FeedPresenter.class.getName(), "updateMoviesFeed() FIRST_PAGE");
            response.setMovies(filteredMovies);
            moviesFeed = response;
        }

        if (isUpdating) {
            Log.d(FeedPresenter.class.getName(), "updateMoviesFeed() isUpdating");
            deleteMoviesFeedDb(moviesFeed.getId());
        }

        insertMoviesFeedDb(moviesFeed);

        return filteredMovies;
    }

    private void deleteMoviesFeedDb(Long id) {
        Log.d(FeedPresenter.class.getName(), "deleteMoviesFeedDb() id: " + id);
        QueryBuilder qb = App.getDaoSession().getMovieDao().queryBuilder();
        qb.where(MovieDao.Properties.MoviesFeedKey.eq(id));
        qb.buildDelete().executeDeleteWithoutDetachingEntities();

        App.getDaoSession().getMoviesFeedDao().deleteByKey(id);
    }

    private void insertMoviesFeedDb(MoviesFeed moviesFeed) {
        Log.d(FeedPresenter.class.getName(), "insertMoviesFeedDb() id: " + moviesFeed.getId());
        App.getDaoSession().getMoviesFeedDao().insertOrReplace(moviesFeed);
        App.getDaoSession().getMovieDao().insertOrReplaceInTx(moviesFeed.getMovies());
    }

    private MoviesFeed loadMoviesFeedDb() {
        Log.d(FeedPresenter.class.getName(), "loadMoviesFeedDb() id: " + filterBy.getId());
        return App.getDaoSession().getMoviesFeedDao().load(filterBy.getId());
    }

    //called by another thread.
    private MoviesFeed loadMoviesFeed() {
        MoviesFeed moviesFeed = loadMoviesFeedDb();
        if (moviesFeed != null) {
            moviesFeed.setMovies(moviesFeed.getMovies());
            this.moviesFeed = moviesFeed;
            this.page = moviesFeed.getPage();
        }
        Log.d(FeedPresenter.class.getName(), "loadMoviesFeed() moviesFeed: " + moviesFeed);
        return moviesFeed;
    }

    private void request(boolean isRequestingNextPage) {
        this.isRequestingNextPage = isRequestingNextPage;

        if (isRequestingNextPage) {
            this.page = ++this.page;
        } else {
            this.page = 1;
        }

        if (this.filterBy == Filters.TOP_RATED || this.filterBy == Filters.UPCOMING) {
            start(REQUEST_MOVIES_FILTER);
            stop(REQUEST_MOVIES_SORT_BY);
        } else {
            start(REQUEST_MOVIES_SORT_BY);
            stop(REQUEST_MOVIES_FILTER);
        }

        Log.d(FeedPresenter.class.getName(), "request() isRequestingNextPage: " + this.isRequestingNextPage + " page: " + this.page + " filterBy: " + this.filterBy);
    }


    public void requestMoviesFeed(FeedActivity activity, Filters filterBy) {
        Log.d(FeedPresenter.class.getName(), "requestMoviesFeed() filterBy: " + filterBy);
        this.filterBy = filterBy;
        start(REQUEST_LOAD_MOVIES_FEED);
    }

    public void refreshMoviesFeed() {
        this.isUpdating = true;
        Log.d(FeedPresenter.class.getName(), "refreshMoviesFeed() isUpdating: " + this.isUpdating);
        request(false);

    }

    public void requestNextPage() {
        Log.d(FeedPresenter.class.getName(), "requestNextPage()");
        request(true);
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
}
