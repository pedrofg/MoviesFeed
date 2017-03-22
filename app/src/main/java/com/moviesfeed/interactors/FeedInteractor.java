package com.moviesfeed.interactors;

import android.content.Context;
import android.util.Log;

import com.moviesfeed.api.Filters;
import com.moviesfeed.di.DaggerSchedulersComponent;
import com.moviesfeed.di.SchedulersModule;
import com.moviesfeed.models.Movie;
import com.moviesfeed.models.MoviesFeed;
import com.moviesfeed.repository.FeedRepository;
import com.moviesfeed.ui.presenters.Util;
import com.moviesfeed.ui.presenters.Validator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

import static com.moviesfeed.repository.FeedRepository.NOT_FOUND;

/**
 * Created by Pedro on 2017-03-13.
 */

public class FeedInteractor {

    @Inject
    @Named(SchedulersModule.IO_SCHEDULER)
    public Scheduler ioScheduler;
    @Inject
    @Named(SchedulersModule.MAIN_THREAD_SCHEDULER)
    public Scheduler mainThreadScheduler;

    private Filters filter;
    private FeedRepository feedRepository;
    private Context context;
    private final CompositeDisposable disposables;
    private FeedInteractorCallback presenterCallback;
    private String query;
    private MoviesFeed moviesFeedCache;
    private boolean isNextPage, isSwipeToRefresh;

    public FeedInteractor(Context context, FeedInteractorCallback callback) {
        DaggerSchedulersComponent.builder()
                .schedulersModule(new SchedulersModule())
                .build().inject(this);

        this.context = context;
        this.presenterCallback = callback;
        this.disposables = new CompositeDisposable();
    }

    public void init() {
        this.feedRepository = new FeedRepository(this.context);
    }

    public void requestMoviesFeed(Filters filter) {
        this.filter = filter;
        clearMemoryCache();
        loadMoviesFeedDb();
    }

    public void requestNextPage() {
        this.moviesFeedCache.setPage(this.moviesFeedCache.getPage() + 1);
        this.isNextPage = true;
        loadMoviesFeedApi();
    }

    public void searchMovieFeed(String query) {
        this.query = query;
        this.filter = Filters.SEARCH;
        clearMemoryCache();
        loadMoviesFeedApi();
    }

    public void refreshMoviesFeed() {
        this.isSwipeToRefresh = true;
        clearMemoryCache();
        loadMoviesFeedApi();
    }

    public void tryAgain() {
        if (this.filter == Filters.SEARCH) {
            searchMovieFeed(this.query);
        } else {
            loadMoviesFeedApi();
        }
    }


    private void loadMoviesFeedDb() {
        Observable<MoviesFeed> observable = Observable.fromCallable(() -> feedRepository.loadMoviesFeedDb(filter))
                .subscribeOn(ioScheduler)
                .observeOn(mainThreadScheduler);

        DisposableObserver<MoviesFeed> observer = new DisposableObserver<MoviesFeed>() {
            @Override
            public void onNext(MoviesFeed moviesFeed) {
                if (moviesFeed == NOT_FOUND) {
                    loadMoviesFeedApi();
                } else {
                    updateMemoryCache(moviesFeed, true);
                    presenterCallback.onLoadSuccess(moviesFeed.cloneMoviesFeed());
                }
            }

            @Override
            public void onError(Throwable throwable) {
                presenterCallback.onLoadError(throwable, false);
            }

            @Override
            public void onComplete() {
            }
        };

        addDisposable(observable.subscribeWith(observer));
    }


    private void loadMoviesFeedApi() {
        Observable<MoviesFeed> observable = null;

        int page = moviesFeedCache == null ? 1 : moviesFeedCache.getPage();

        if (filter == Filters.SEARCH) {
            observable = feedRepository.loadMoviesFeedBySearch(query, page).subscribeOn(ioScheduler).observeOn(mainThreadScheduler);
        } else if (filter.getId() == Filters.REVENUE.getId()) {
            observable = feedRepository.loadMoviesFeedByDiscover(filter.toString(), page).subscribeOn(ioScheduler).observeOn(mainThreadScheduler);
        } else if (filter.getId() > Filters.REVENUE.getId()) {
            observable = feedRepository.loadMoviesFeedByGenre(Integer.valueOf(filter.toString()), getFormattedDate(), page).subscribeOn(ioScheduler).observeOn(mainThreadScheduler);
        } else if (filter.getId() < Filters.REVENUE.getId()) {
            observable = feedRepository.loadMoviesFeedByCategory(filter.toString(), page).subscribeOn(ioScheduler).observeOn(mainThreadScheduler);
        }

//        observable.subscribeOn(ioScheduler).observeOn(mainThreadScheduler);

        DisposableObserver<MoviesFeed> observer = new DisposableObserver<MoviesFeed>() {
            @Override
            public void onNext(MoviesFeed moviesFeedDownloaded) {
                updateMemoryCache(moviesFeedDownloaded, false);
                presenterCallback.onLoadSuccess(moviesFeedCache.cloneMoviesFeed());
                updateDiskCache(moviesFeedCache);
            }

            @Override
            public void onError(Throwable throwable) {
                if (isNextPage) {
                    moviesFeedCache.setPage(moviesFeedCache.getPage() - 1);
                    isNextPage = false;
                }

                if (isSwipeToRefresh)
                    isSwipeToRefresh = false;

                boolean isNetworkError = false;

                if (throwable instanceof IOException && !Util.isNetworkAvailable(context))
                    isNetworkError = true;

                presenterCallback.onLoadError(throwable, isNetworkError);
            }

            @Override
            public void onComplete() {
            }
        };

        addDisposable(observable.subscribeWith(observer));
    }


    private void clearMemoryCache() {
        this.moviesFeedCache = null;
    }

    private void updateMemoryCache(MoviesFeed moviesFeed, boolean isFromDb) {
        if (isFromDb) {
            this.moviesFeedCache = moviesFeed;
        } else {


            if (this.moviesFeedCache == null) {
                this.moviesFeedCache = new MoviesFeed();
                this.moviesFeedCache.setMovies(new ArrayList<>());
            }

            this.moviesFeedCache.setFilterAndId(this.filter);
            this.moviesFeedCache.setPage(moviesFeed.getPage());
            this.moviesFeedCache.setTotalPages(moviesFeed.getTotalPages());

            if (checkAllMoviesDownloaded(moviesFeed)) {
                this.moviesFeedCache.setAllMoviesDownloaded(true);
                Log.d(FeedInteractor.class.getName(), "updateMoviesFeedDb() checkAllMoviesDownloaded == true");
            }

            final List<Movie> filteredMovies = new ArrayList<>();

            if (moviesFeed.getMovies().size() > 0) {
                for (Movie movie : moviesFeed.getMovies()) {
                    if (Validator.validateMovie(movie)) {
                        movie.setMoviesFeedKey(this.moviesFeedCache.getId());
                        filteredMovies.add(movie);
                    }
                }

                Log.d(FeedInteractor.class.getName(), "updateMoviesFeedDb() filtered movies: " + filteredMovies.size());
            }

            this.moviesFeedCache.getMovies().addAll(filteredMovies);

            Log.d(FeedInteractor.class.getName(), "updateMoviesFeedDb() page: " + moviesFeed.getPage());
        }
    }

    private void updateDiskCache(MoviesFeed moviesFeed) {

        Observable<MoviesFeed> observable = Observable.fromCallable(() -> {
            if (isSwipeToRefresh) {
                feedRepository.deleteMoviesFeedDb(moviesFeed.getId());
                isSwipeToRefresh = false;
            }
            feedRepository.updateMoviesFeedDb(moviesFeed);
            return NOT_FOUND;
        }).subscribeOn(ioScheduler)
                .observeOn(mainThreadScheduler);


        addDisposable(observable.subscribe());

    }

    private boolean checkAllMoviesDownloaded(MoviesFeed response) {
        return response.getPage() == response.getTotalPages() || response.getMovies().size() == 0;
    }


    private String getFormattedDate() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1; //+1 is needed by the fact that month starts in 0
        int day = c.get(Calendar.DAY_OF_MONTH);

        String formattedDate = year + "-" + month + "-" + day;

        Log.d(FeedInteractor.class.getName(), "getFormattedDate() date: " + formattedDate);
        return formattedDate;
    }


    private void addDisposable(Disposable disposable) {
        disposables.add(disposable);
    }

    public void dispose() {
        if (!disposables.isDisposed()) {
            disposables.dispose();
        }
    }

    public boolean hasCache() {
        return moviesFeedCache != null ? true : false;
    }
}
