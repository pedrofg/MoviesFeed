package com.moviesfeed.presenters;

import android.os.Bundle;

import com.moviesfeed.App;
import com.moviesfeed.activities.FeedActivity;
import com.moviesfeed.api.Filters;
import com.moviesfeed.api.FiltersType;
import com.moviesfeed.models.Movie;
import com.moviesfeed.models.MoviesFeed;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import icepick.State;
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
public class FeedPresenter extends BasePresenter<FeedActivity> {

    private static final int REQUEST_MOVIES_SORTY_BY = 1;
    private static final int REQUEST_MOVIES_FILTER = 2;
    public static final int FIRST_PAGE = 1;

    @State
    Filters filterBy = Filters.POPULARITY;
    @State
    FiltersType filterType = FiltersType.DESC;
    @State
    MoviesFeed moviesFeed;
    @State
    int page = 1;

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
                new Action2<FeedActivity, MoviesFeed>() {
                    @Override
                    public void call(final FeedActivity activity, final MoviesFeed response) {

                        filterMovieFeed(activity, response);

                    }
                },
                new Action2<FeedActivity, Throwable>() {
                    @Override
                    public void call(FeedActivity activity, Throwable throwable) {
                        //TODO handle error
                    }
                });

    }

    private String getFormattedDate() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1; //+1 is needed by the fact that month starts in 0
        int day = c.get(Calendar.DAY_OF_MONTH);

        return year + "-" + month + "-" + day;
    }

    private void createRequestMoviesFeedSortBy() {
        restartableLatestCache(REQUEST_MOVIES_SORTY_BY,
                new Func0<Observable<MoviesFeed>>() {
                    @Override
                    public Observable<MoviesFeed> call() {
                        return App.getServerApi().getMoviesSortBy(filterBy.toString() + filterType.toString(),
                                getFormattedDate(), page)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                },
                new Action2<FeedActivity, MoviesFeed>() {
                    @Override
                    public void call(final FeedActivity activity, final MoviesFeed response) {

                        filterMovieFeed(activity, response);

                    }
                },
                new Action2<FeedActivity, Throwable>() {
                    @Override
                    public void call(FeedActivity activity, Throwable throwable) {
                        //TODO handle error
                    }
                });
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
                    activity.requestMoviesFeedCallback(moviesFeed, isRequestingNextPage);
                } else if (page == FIRST_PAGE) {
                    response.setMovies(filteredMovies);
                    moviesFeed = response;
                    activity.requestMoviesFeedCallback(moviesFeed, isRequestingNextPage);
                }
                isRequestingNextPage = false;
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Movie movie) {
                filteredMovies.add(movie);
            }
        });
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
            stop(REQUEST_MOVIES_SORTY_BY);
        } else {
            start(REQUEST_MOVIES_SORTY_BY);
            stop(REQUEST_MOVIES_FILTER);
        }
    }


    public void requestMoviesFeed(Filters filterBy, FiltersType filterType) {
        this.filterBy = filterBy;
        this.filterType = filterType;

        request(false);
    }

    //Ready to use when necessary.
    public void requestOrderByUpdate() {
        if (this.filterType == FiltersType.ASC)
            this.filterType = FiltersType.DESC;
        else
            this.filterType = FiltersType.ASC;

        request(false);
    }

    public void requestNextPage() {
        request(true);
    }
}
