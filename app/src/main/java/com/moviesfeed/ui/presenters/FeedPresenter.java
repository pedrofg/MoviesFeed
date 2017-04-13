package com.moviesfeed.ui.presenters;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.moviesfeed.R;
import com.moviesfeed.api.Filters;
import com.moviesfeed.interactors.Errors;
import com.moviesfeed.interactors.FeedInteractor;
import com.moviesfeed.interactors.FeedInteractorCallback;
import com.moviesfeed.models.Movie;
import com.moviesfeed.models.MoviesFeed;

public class FeedPresenter implements Presenter, FeedInteractorCallback {

    public interface FeedPresenterCallback {

        void showError(String message);

        Context context();

        void renderFeed(MoviesFeed moviesFeed, int insertedMovies);

        void viewMovie(Movie movie);

        void createGrid();

        void removeProgressBottomGrid();

        void addProgressBottomGrid();

        void addErrorBottomGrid();

        void contentUpdated(boolean error);

        void updatingContent();

        void clearScrollListener();

        void stopRefreshing();

        void showTryAgain();

        void hideTryAgain();
    }

    private FeedInteractor feedInteractor;
    private FeedPresenterCallback callback;
    private boolean isUpdating, isNextPage;

    public void init(Context context, FeedPresenterCallback callback) {
        this.callback = callback;
        this.feedInteractor = new FeedInteractor(context, this);
        this.feedInteractor.init();
    }


    public void requestMoviesFeed(Filters filter) {
        Log.d(FeedPresenter.class.getName(), "requestMoviesFeed() filterBy: " + filter);
        callback.updatingContent();
        this.feedInteractor.requestMoviesFeed(filter);
    }

    public void requestSearchMoviesFeed(String query) {
        Log.d(FeedPresenter.class.getName(), "requestSearchMoviesFeed() " + query);
        callback.createGrid();
        callback.updatingContent();
        this.feedInteractor.searchMovieFeed(query);
    }

    public void refreshMoviesFeed() {
        this.isUpdating = true;
        Log.d(FeedPresenter.class.getName(), "refreshMoviesFeed()");
        this.feedInteractor.refreshMoviesFeed();
    }

    public void requestNextPage() {
        this.isNextPage = true;
        Log.d(FeedPresenter.class.getName(), "requestNextPage()");
        callback.removeProgressBottomGrid();
        callback.addProgressBottomGrid();
        this.feedInteractor.requestNextPage();
    }

    public void tryAgain() {
        Log.d(FeedPresenter.class.getName(), "tryAgain()");
        callback.updatingContent();
        this.feedInteractor.tryAgain();
    }

    public void navigationItemSelected(Filters filter) {
        callback.createGrid();
        requestMoviesFeed(filter);
    }

    public void onBtnUpdateFeedClicked() {
        this.requestNextPage();
    }

    public void onMovieClicked(Movie movie) {
        if (movie != null) {
            callback.viewMovie(movie);
        } else {
            Log.i(FeedPresenter.class.getName(), "onItemClick movie == null / update item clicked");
            //it means the user clicked on the update icon item
            requestNextPage();
        }

    }


    @Override
    public void onLoadSuccess(MoviesFeed moviesFeed, int insertedMovies) {
        Log.i(FeedPresenter.class.getName(), "onLoadSuccess()");
        if (isUpdating) {
            callback.createGrid();
            this.isUpdating = false;
        }

        if (isNextPage) {
            callback.removeProgressBottomGrid();
            this.isNextPage = false;
        }

        callback.renderFeed(moviesFeed, insertedMovies);
        callback.contentUpdated(false);

        if (moviesFeed.isAllMoviesDownloaded() || moviesFeed.isLimitMoviesReached()) {
            Log.i(FeedPresenter.class.getName(), "requestMoviesFeedSuccess() allMoviesDownloaded = true");

            if (moviesFeed.getMovies().size() == 0) {
                Log.i(FeedPresenter.class.getName(), "requestMoviesFeedSuccess() moviesFeed.getMovies().size() == 0");
                callback.showError(callback.context().getString(R.string.no_movies_found));
                callback.contentUpdated(true);
                callback.hideTryAgain();
            } else {
                Log.i(FeedPresenter.class.getName(), "requestMoviesFeedSuccess() clearOnScrollListeners");
                callback.clearScrollListener();
            }
        }


    }

    @Override
    public void onLoadError(Throwable throwable, Errors error) {
        Log.e(FeedPresenter.class.getName(), "onLoadError() throwable: " + throwable.getMessage() + " error: " + error.toString());

        String message = getFriendlyMessage(error);

        if (feedInteractor.hasCache()) {
            Toast.makeText(callback.context(), message, Toast.LENGTH_LONG).show();
            callback.removeProgressBottomGrid();
            callback.addErrorBottomGrid();
        } else {
            callback.showTryAgain();
            callback.showError(message);
            callback.contentUpdated(true);
        }

        this.isNextPage = false;
        this.isUpdating = false;

        callback.stopRefreshing();
    }


    private String getFriendlyMessage(Errors error) {
        int messageId = R.string.error_request_feed;

        if (error.equals(Errors.TOO_MANY_REQUEST)) {
            messageId = R.string.error_request_feed_too_many_requests;
        } else if (error.equals(Errors.NETWORK)) {
            messageId = R.string.error_request_feed_network;
        } else if (error.equals(Errors.LIMIT_MOVIES_REACHED)) {
            messageId = R.string.error_request_feed_limit_movies_reached;
        }

        return callback.context().getString(messageId);
    }


    @Override
    public void resume() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void destroy() {
        this.feedInteractor.dispose();
        this.callback = null;
    }
}
