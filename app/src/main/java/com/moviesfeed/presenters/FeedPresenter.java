package com.moviesfeed.presenters;

import android.os.Bundle;
import android.util.Log;

import com.moviesfeed.App;
import com.moviesfeed.activities.FeedActivity;
import com.moviesfeed.api.Filters;
import com.moviesfeed.models.Movie;
import com.moviesfeed.models.MoviesFeed;

import org.greenrobot.greendao.async.AsyncSession;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import nucleus.presenter.RxPresenter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Pedro on 8/17/2016.
 */
public class FeedPresenter extends RxPresenter<FeedActivity> {

    public static final int REQUEST_MOVIES_SORT_BY = 1;
    public static final int REQUEST_MOVIES_FILTER = 2;
    public static final int FIRST_PAGE = 1;
    public static final String DESC = ".desc";

    private Filters filterBy = Filters.POPULARITY;
    private MoviesFeed moviesFeed;
    private int page = 1;

    private boolean isRequestingNextPage;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        createRequestMoviesFeedSortBy();
        createRequestMoviesFeedFiltered();
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
                handleRequestSuccess(),
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
                handleRequestSuccess(),
                handleRequestFailure());
    }

    private Action2 handleRequestSuccess() {
        return new Action2<FeedActivity, MoviesFeed>() {
            @Override
            public void call(final FeedActivity activity, final MoviesFeed response) {
                response.setFilter(filterBy);
                filterMovieFeed(activity, response);
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

    private void filterMovieFeed(final FeedActivity activity, final MoviesFeed response) {
        final List<Movie> filteredMovies = new ArrayList<Movie>();
        Observable.from(response.getMovies()).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).filter(new Func1<Movie, Boolean>() {
            @Override
            public Boolean call(Movie movie) {
                return movie.getPosterPath() != null;
            }
        }).subscribe(new Subscriber<Movie>() {
            @Override
            public void onCompleted() {
                if (isRequestingNextPage) {
                    moviesFeed.getMovies().addAll(filteredMovies);
                    moviesFeed.setPage(page);
                } else if (page == FIRST_PAGE) {
                    response.setMovies(filteredMovies);
                    moviesFeed = response;
                }

                insertMoviesFeedDB(moviesFeed);
                activity.requestMoviesFeedCallback(moviesFeed, isRequestingNextPage);
                isRequestingNextPage = false;
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(FeedPresenter.class.getName(), throwable.getMessage(), throwable);
                //TODO handle error
            }

            @Override
            public void onNext(Movie movie) {
                movie.setMoviesFeedKey(filterBy.getId());
                filteredMovies.add(movie);
            }
        });
    }

    private void insertMoviesFeedDB(MoviesFeed moviesFeed) {
        App.getDaoSession().getMoviesFeedDao().insertOrReplace(moviesFeed);
        App.getDaoSession().getMovieDao().insertOrReplaceInTx(moviesFeed.getMovies());
    }

    private MoviesFeed loadMoviesFeedDB() {
        return App.getDaoSession().getMoviesFeedDao().load(filterBy.getId());
    }

    private void request(boolean isRequestingNextPage) {
        this.isRequestingNextPage = isRequestingNextPage;

        if (isRequestingNextPage) {
            this.page = this.page + 1;
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
    }


    public void requestMoviesFeed(FeedActivity activity, Filters filterBy) {
        this.filterBy = filterBy;
        MoviesFeed moviesFeed = loadMoviesFeedDB();
        if (moviesFeed != null) {
            moviesFeed.setMovies(moviesFeed.getMovies());
            this.moviesFeed = moviesFeed;
            this.page = moviesFeed.getPage();
            activity.requestMoviesFeedCallback(this.moviesFeed, isRequestingNextPage);
        } else {
            request(false);
        }

    }

    public void requestNextPage() {
        request(true);
    }


    private String getFormattedDate() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1; //+1 is needed by the fact that month starts in 0
        int day = c.get(Calendar.DAY_OF_MONTH);

        return year + "-" + month + "-" + day;
    }
}
