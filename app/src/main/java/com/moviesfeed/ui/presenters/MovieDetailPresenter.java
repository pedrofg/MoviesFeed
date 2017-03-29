package com.moviesfeed.ui.presenters;

import android.content.Context;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.moviesfeed.R;
import com.moviesfeed.interactors.MovieDetailInteractor;
import com.moviesfeed.interactors.MovieDetailInteractorCallback;
import com.moviesfeed.models.Cast;
import com.moviesfeed.models.Crew;
import com.moviesfeed.models.Genre;
import com.moviesfeed.models.Movie;
import com.moviesfeed.models.MovieBackdrop;
import com.moviesfeed.models.MovieDetail;
import com.moviesfeed.models.Review;
import com.moviesfeed.models.Video;
import com.moviesfeed.ui.activities.uicomponents.AppBarStateChangeListener.State;

import java.util.List;


/**
 * Created by Pedro on 8/24/2016.
 */
public class MovieDetailPresenter implements Presenter, MovieDetailInteractorCallback {

    public interface MovieDetailPresenterCallback {

        void showError(String message);

        Context context();

        void contentUpdated(boolean error);

        void updatingContent();

        void showMovieTitle(String title);

        void showMovieYear(String year);

        void showMovieRuntime(String runtime);

        void showRating(String rating, float ratingFloat);

        void showOverview(String overview);

        void showHomepage(String homepage);

        void showGenres(String genres);

        void showCastAndCrew(List<Cast> castList, List<Crew> crewList);

        void showSimilarMovies(List<Movie> movieList);

        void showReviews(List<Review> reviewList);

        void showVideos(List<Video> videoList);

        void showImages(List<MovieBackdrop> imageList);

        void updateToolbarTitle(String title);

        String getToolbarTitle();

        int getToolbarHeight();

        void updateToolbarBackground(int alpha);

        void openVideoUrl(Uri url);

        void openMovieDetail(int movieId);

        void animImagePoster(ImageView imageView, int height, ImageView.ScaleType scaleType);

        void setScrollsEnable(boolean blocked);

        void openReview(Uri url);
    }

    private MovieDetailInteractor movieDetailInteractor;
    private MovieDetailPresenterCallback callback;
    private int MAX_ALPHA = 255;
    private String MINUTES = "m";


    public void appBarLayoutStateChanged(AppBarLayout appBarLayout, State state, int verticalOffset) {
        if (state == State.COLLAPSED && this.movieDetailInteractor.hasCache())
            callback.updateToolbarTitle(this.movieDetailInteractor.getMovieTitle());
        else if (!TextUtils.isEmpty(callback.getToolbarTitle())) {
            callback.updateToolbarTitle("");
        }
        //measuring for alpha
        int toolBarHeight = callback.getToolbarHeight();
        int appBarHeight = appBarLayout.getMeasuredHeight();
        Float f = ((((float) appBarHeight - toolBarHeight) + verticalOffset) / ((float) appBarHeight - toolBarHeight)) * MAX_ALPHA;
        callback.updateToolbarBackground(MAX_ALPHA - Math.round(f));
    }

    private String getGenresText(List<Genre> genres) {
        String jumpLine = "\n";
        String[] genreNames = new String[genres.size()];
        for (int i = 0; i < genres.size(); i++) {
            genreNames[i] = genres.get(i).getName().replaceAll(" ", "-");
        }
        return TextUtils.join(jumpLine, genreNames);
    }

    private float calculateRatingByVotes(double votes) {
        return (float) votes * 5 / 10;
    }


    public void init(Context context, MovieDetailPresenterCallback callback) {
        this.callback = callback;
        this.movieDetailInteractor = new MovieDetailInteractor(context, this);
        this.movieDetailInteractor.init();
    }

    public void requestMovieDetail(int movieId) {
        Log.d(MovieDetailPresenter.class.getName(), "requestMovieDetail movieId: " + movieId);
        callback.updatingContent();
        this.movieDetailInteractor.requestMovieDetail(movieId);
    }

    public void tryAgain() {
        Log.d(MovieDetailPresenter.class.getName(), "tryAgain()");
        callback.updatingContent();
        this.movieDetailInteractor.tryAgain();
    }

    public void onVideoClicked(Video video) {
        Uri url = Uri.parse(video.getYoutubeUrl());
        this.callback.openVideoUrl(url);
    }

    public void onSimilarMovieClicked(Movie movie) {
        this.callback.openMovieDetail(movie.getIdTmdb());
    }

    public void onReviewClicked(Review review) {
        Uri url = Uri.parse(review.getUrl());
        this.callback.openReview(url);
    }

    public void onImagePosterClicked(ImageView imageView) {

        boolean isZoomed;
        int imgDefaultHeight = (int) callback.context().getResources().getDimension(R.dimen.movie_detail_img_backdrop_height);

        isZoomed = imageView.getHeight() != imgDefaultHeight;

        int height = isZoomed ? imgDefaultHeight
                : ViewGroup.LayoutParams.MATCH_PARENT;

        ImageView.ScaleType scaleType = isZoomed ? ImageView.ScaleType.FIT_XY
                : ImageView.ScaleType.CENTER_CROP;


        callback.animImagePoster(imageView, height, scaleType);

        callback.setScrollsEnable(isZoomed);
    }


    @Override
    public void onLoadSuccess(MovieDetail movieDetail) {
        this.callback.showMovieTitle(movieDetail.getTitle());

        if (movieDetail.getReleaseDate() != null && !movieDetail.getReleaseDate().isEmpty()) {
            this.callback.showMovieYear(movieDetail.getReleaseDate().substring(0, 4));
        }
        if (movieDetail.getRuntime() > 0) {
            this.callback.showMovieRuntime(movieDetail.getRuntime() + MINUTES);
        }
        if (movieDetail.getVoteAverage() > 0) {
            this.callback.showRating(String.valueOf(movieDetail.getVoteAverage()), calculateRatingByVotes(movieDetail.getVoteAverage()));
        }

        this.callback.showOverview(movieDetail.getOverview());

        if (!TextUtils.isEmpty(movieDetail.getHomepage())) {
            this.callback.showHomepage(movieDetail.getHomepage());
        }

        if (movieDetail.getGenres().size() > 0) {
            this.callback.showGenres(getGenresText(movieDetail.getGenres()));
        }

        if (movieDetail.getCredits() != null &&
                movieDetail.getCredits().getCast() != null &&
                movieDetail.getCredits().getCrew() != null &&
                (movieDetail.getCredits().getCast().size() > 0 || movieDetail.getCredits().getCrew().size() > 0)) {
            this.callback.showCastAndCrew(movieDetail.getCredits().getCast(), movieDetail.getCredits().getCrew());
        }

        if (movieDetail.getSimilarMovies() != null &&
                movieDetail.getSimilarMovies().getMovies() != null &&
                movieDetail.getSimilarMovies().getMovies().size() > 0) {
            this.callback.showSimilarMovies(movieDetail.getSimilarMovies().getMovies());
        }

        if (movieDetail.getMovieReviews() != null &&
                movieDetail.getMovieReviews().getReviews() != null &&
                movieDetail.getMovieReviews().getReviews().size() > 0) {
            this.callback.showReviews(movieDetail.getMovieReviews().getReviews());
        }

        if (movieDetail.getVideos() != null &&
                movieDetail.getVideos().getVideos() != null &&
                movieDetail.getVideos().getVideos().size() > 0) {
            this.callback.showVideos(movieDetail.getVideos().getVideos());
        }

        if (movieDetail.getImages().getBackdrops() != null && movieDetail.getImages().getBackdrops().size() > 0) {
            this.callback.showImages(movieDetail.getImages().getBackdrops());
        }

        this.callback.contentUpdated(false);
    }

    @Override
    public void onLoadError(Throwable throwable, boolean isNetworkError) {
        StringBuilder message = new StringBuilder();
        message.append(isNetworkError ? callback.context().getString(R.string.error_request_feed_network) : callback.context().getString(R.string.error_request_feed));
        message.append(callback.context().getString(R.string.tap_here_try_again));
        callback.showError(message.toString());
        callback.contentUpdated(true);
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {
        this.movieDetailInteractor.dispose();
        this.callback = null;
    }
}
