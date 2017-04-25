package com.moviesfeed.interactors;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.moviesfeed.api.Filters;
import com.moviesfeed.di.DaggerSchedulersComponent;
import com.moviesfeed.di.SchedulersModule;
import com.moviesfeed.interactors.exceptions.LimitMoviesReachedException;
import com.moviesfeed.models.Movie;
import com.moviesfeed.models.MoviesFeed;
import com.moviesfeed.repository.FeedRepository;
import com.moviesfeed.ui.presenters.Util;
import com.moviesfeed.ui.presenters.Validator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import retrofit2.HttpException;

import static com.moviesfeed.repository.FeedRepository.NOT_FOUND;

/**
 * Created by Pedro on 2017-03-13.
 */

public class FeedInteractor {

    @Inject
    @Named(SchedulersModule.IO_SCHEDULER)
    Scheduler ioScheduler;
    @Inject
    @Named(SchedulersModule.MAIN_THREAD_SCHEDULER)
    Scheduler mainThreadScheduler;

    private static final int AMOUNT_MOVIES_BY_PAGE = 20;
    private static final int LIMIT_MOVIES_DOWNLOADED = 1000;
    private static final int FIRST_PAGE = 1;
    private static final int HTTP_TOO_MANY_REQUEST_ERROR = 429;
    //Delay implemented to avoid errors when scrolling too fast.
    private static final int DELAY_TO_PROCESS = 300;

    private Filters filter;
    private FeedRepository feedRepository;
    private Context context;
    private final CompositeDisposable disposables;
    private FeedInteractorCallback presenterCallback;
    private String query;
    private MoviesFeed moviesFeedCache;
    private boolean isNextPage, isSwipeToRefresh;
    private int counterMoviesReadyToRender;

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
        new Handler().postDelayed(this::loadMoviesFeedApi, DELAY_TO_PROCESS);
    }

    public void searchMovieFeed(String query) {
        this.query = query;
        this.filter = Filters.SEARCH;
        clearMemoryCache();
        loadMoviesFeedApi();
    }

    public void refreshMoviesFeed() {
        this.isSwipeToRefresh = true;
        loadMoviesFeedApi();
    }

    public void tryAgain() {
        new Handler().postDelayed(() -> {
            if (this.filter == Filters.SEARCH) {
                searchMovieFeed(this.query);
            } else {
                loadMoviesFeedApi();
            }
        }, DELAY_TO_PROCESS);
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
                    presenterCallback.onLoadSuccess(moviesFeed.cloneMoviesFeed(), moviesFeed.getMovies().size());
                }
            }

            @Override
            public void onError(Throwable throwable) {
                presenterCallback.onLoadError(throwable, Errors.GENERIC);
            }

            @Override
            public void onComplete() {
            }
        };

        addDisposable(observable.subscribeWith(observer));
    }


    private void loadMoviesFeedApi() {
        if (validateCanDownloadMovie()) {

        Observable<MoviesFeed> observable = null;

        int page;
        if (moviesFeedCache == null || isSwipeToRefresh) {
            page = FIRST_PAGE;
        } else {
            page = moviesFeedCache.getPage();
        }

        if (filter == Filters.SEARCH) {
            observable = feedRepository.loadMoviesFeedBySearch(query, page);
        } else if (filter.getId() == Filters.REVENUE.getId()) {
            observable = feedRepository.loadMoviesFeedByDiscover(filter.toString(), page);
        } else if (filter.getId() > Filters.REVENUE.getId()) {
            observable = feedRepository.loadMoviesFeedByGenre(Integer.valueOf(filter.toString()), getFormattedDate(), page);
        } else if (filter.getId() < Filters.REVENUE.getId()) {
            observable = feedRepository.loadMoviesFeedByCategory(filter.toString(), page);
        }

        observable = observable.map(moviesFeedDownloaded -> {
            if (isSwipeToRefresh) {
                clearMemoryCache();
            }

            List<Movie> filteredMovies = updateMemoryCache(moviesFeedDownloaded, false);
            updateDiskCache(moviesFeedCache, filteredMovies);


            counterMoviesReadyToRender += filteredMovies != null ? filteredMovies.size() : 0;

            return moviesFeedDownloaded;
        }).subscribeOn(ioScheduler).observeOn(mainThreadScheduler);

        DisposableObserver<MoviesFeed> observer = new DisposableObserver<MoviesFeed>() {
            @Override
            public void onNext(MoviesFeed moviesFeedDownloaded) {
                Log.d(FeedInteractor.class.getName(), "onNext() moviesFeedCache size: " + moviesFeedCache.getMovies().size());
                if (shouldDownloadNextPage()) {
                    requestNextPage();
                } else {
                    Log.d(FeedInteractor.class.getName(), "counterMoviesReadyToRender: " + counterMoviesReadyToRender);
                    presenterCallback.onLoadSuccess(moviesFeedCache.cloneMoviesFeed(), counterMoviesReadyToRender);
                    counterMoviesReadyToRender = 0;
                }
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(FeedInteractor.class.getName(), "onError() throwable: " + throwable.getMessage());
                if (isNextPage) {
                    moviesFeedCache.setPage(moviesFeedCache.getPage() - 1);
                }

                isNextPage = false;
                isSwipeToRefresh = false;

                Errors error = Errors.GENERIC;
                if (throwable instanceof IOException && !Util.isNetworkAvailable(context)) {
                    error = Errors.NETWORK;
                } else if (throwable instanceof HttpException) {
                    HttpException httpException = (HttpException) throwable;
                    boolean isTooManyRequest = httpException.code() == HTTP_TOO_MANY_REQUEST_ERROR;

                    error = isTooManyRequest ? Errors.TOO_MANY_REQUEST : Errors.GENERIC;
                }

                presenterCallback.onLoadError(throwable, error);
            }

            @Override
            public void onComplete() {
                isNextPage = false;
                isSwipeToRefresh = false;
            }
        };

        addDisposable(observable.subscribeWith(observer));
        }
    }

    private boolean validateCanDownloadMovie() {
        boolean proceed = true;

        if (moviesFeedCache != null && moviesFeedCache.isLimitMoviesReached()) {
            presenterCallback.onLoadError(new LimitMoviesReachedException(), Errors.LIMIT_MOVIES_REACHED);
            proceed = false;
        } else if (!Util.isNetworkAvailable(context)) {
            presenterCallback.onLoadError(new NetworkErrorException(), Errors.NETWORK);
            proceed = false;
        }


        return proceed;
    }

    private boolean shouldDownloadNextPage() {
        return counterMoviesReadyToRender < AMOUNT_MOVIES_BY_PAGE
                && !moviesFeedCache.isAllMoviesDownloaded()
                && !moviesFeedCache.isLimitMoviesReached();
    }


    private void clearMemoryCache() {
        this.moviesFeedCache = null;
    }

    private List<Movie> updateMemoryCache(MoviesFeed moviesFeed, boolean isFromDb) {
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
                Log.d(FeedInteractor.class.getName(), "updateMemoryCache() checkAllMoviesDownloaded == true");
            }

            final List<Movie> filteredMovies = new ArrayList<>();

            if (moviesFeed.getMovies().size() > 0) {
                for (Iterator<Movie> iter = moviesFeed.getMovies().iterator(); iter.hasNext(); ) {
                    Movie movie = iter.next();
                    if (Validator.validateMovie(movie)) {
                        movie.setMoviesFeedKey(this.moviesFeedCache.getId());
                        filteredMovies.add(movie);
                    }
                }
                Log.d(FeedInteractor.class.getName(), "updateMemoryCache() filtered movies: " + filteredMovies.size());
            }

            this.moviesFeedCache.getMovies().addAll(filteredMovies);

            if (checkLimitMoviesDownloaded()) {
                this.moviesFeedCache.setLimitMoviesReached(true);
                Log.d(FeedInteractor.class.getName(), "updateMemoryCache() checkLimitMoviesDownloaded == true");
            }

            Log.d(FeedInteractor.class.getName(), "updateMemoryCache() page: " + moviesFeed.getPage());

            return filteredMovies;
        }
        return null;
    }

    private boolean checkLimitMoviesDownloaded() {
        return this.moviesFeedCache.getMovies().size() >= LIMIT_MOVIES_DOWNLOADED;
    }

    private void updateDiskCache(MoviesFeed moviesFeed, List<Movie> filteredMovies) {

        Observable<MoviesFeed> observable = Observable.fromCallable(() -> {
            if (isSwipeToRefresh) {
                feedRepository.deleteMoviesFeedDb(moviesFeed.getId());
            }
            feedRepository.updateMoviesFeedDb(moviesFeed, filteredMovies);
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
        return moviesFeedCache != null;
    }
}
