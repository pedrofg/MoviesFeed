package com.moviesfeed.interactors;

import android.content.Context;
import android.util.Log;

import com.moviesfeed.di.DaggerSchedulersComponent;
import com.moviesfeed.di.SchedulersModule;
import com.moviesfeed.models.Cast;
import com.moviesfeed.models.Crew;
import com.moviesfeed.models.Genre;
import com.moviesfeed.models.Movie;
import com.moviesfeed.models.MovieBackdrop;
import com.moviesfeed.models.MovieDetail;
import com.moviesfeed.models.MoviePoster;
import com.moviesfeed.models.Review;
import com.moviesfeed.models.Video;
import com.moviesfeed.repository.MovieDetailRepository;
import com.moviesfeed.ui.presenters.Util;
import com.moviesfeed.ui.presenters.Validator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

import static com.moviesfeed.repository.MovieDetailRepository.NOT_FOUND;

/**
 * Created by Pedro on 2017-03-23.
 */

public class MovieDetailInteractor {

    @Inject
    @Named(SchedulersModule.IO_SCHEDULER)
    public Scheduler ioScheduler;
    @Inject
    @Named(SchedulersModule.MAIN_THREAD_SCHEDULER)
    public Scheduler mainThreadScheduler;

    private Context context;
    private MovieDetailInteractorCallback callback;
    private final CompositeDisposable disposables;
    private int movieId;
    private MovieDetail movieDetailCache;
    private MovieDetailRepository movieDetailRepository;


    public MovieDetailInteractor(Context context, MovieDetailInteractorCallback callback) {
        DaggerSchedulersComponent.builder()
                .schedulersModule(new SchedulersModule())
                .build().inject(this);

        this.context = context;
        this.callback = callback;
        this.disposables = new CompositeDisposable();
    }

    public void init() {
        this.movieDetailRepository = new MovieDetailRepository(this.context);
    }

    public void requestMovieDetail(int movieId) {
        Log.d(MovieDetailInteractor.class.getName(), "requestMovieDetail() movieId: " + movieId);
        this.movieId = movieId;
        loadMovieDetailDb();
    }

    public boolean hasCache() {
        return this.movieDetailCache != null;
    }

    public String getMovieTitle() {
        return this.movieDetailCache.getTitle();
    }

    private void loadMovieDetailDb() {
        Observable<MovieDetail> observable = Observable.fromCallable(() -> this.movieDetailRepository.loadMovieDetailDb((long) this.movieId))
                .subscribeOn(ioScheduler)
                .observeOn(mainThreadScheduler);

        DisposableObserver<MovieDetail> observer = new DisposableObserver<MovieDetail>() {
            @Override
            public void onNext(MovieDetail movieDetail) {
                if (movieDetail == NOT_FOUND) {
                    loadMovieDetailApi();
                } else {
                    updateMemoryCache(movieDetail, true);
                    callback.onLoadSuccess(movieDetailCache);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onLoadError(throwable, false);
            }

            @Override
            public void onComplete() {
            }
        };

        addDisposable(observable.subscribeWith(observer));
    }

    private void updateMemoryCache(MovieDetail movieDetail, boolean isFromDb) {
        if (!isFromDb) {

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
        }

        this.movieDetailCache = movieDetail;
    }

    private void loadMovieDetailApi() {

        Observable<MovieDetail> observable = movieDetailRepository.loadMovieDetailApi(this.movieId)
                .subscribeOn(ioScheduler).observeOn(mainThreadScheduler);

        DisposableObserver<MovieDetail> observer = new DisposableObserver<MovieDetail>() {
            @Override
            public void onNext(MovieDetail movieDetail) {
                updateMemoryCache(movieDetail, false);
                callback.onLoadSuccess(movieDetailCache);
                updateDiskCache(movieDetailCache);
            }

            @Override
            public void onError(Throwable throwable) {

                boolean isNetworkError = false;

                if (throwable instanceof IOException && !Util.isNetworkAvailable(context))
                    isNetworkError = true;

                callback.onLoadError(throwable, isNetworkError);
            }

            @Override
            public void onComplete() {
            }
        };

        addDisposable(observable.subscribeWith(observer));
    }

    private void updateDiskCache(MovieDetail movieDetail) {
        Observable<MovieDetail> observable = Observable.fromCallable(() -> {
            movieDetailRepository.updateMovieDetailDb(movieDetail);
            return NOT_FOUND;
        }).subscribeOn(ioScheduler)
                .observeOn(mainThreadScheduler);


        addDisposable(observable.subscribe());
    }

    public void tryAgain() {
        loadMovieDetailApi();
    }

    private void addDisposable(Disposable disposable) {
        disposables.add(disposable);
    }

    public void dispose() {
        if (!disposables.isDisposed()) {
            disposables.dispose();
        }
    }
}
