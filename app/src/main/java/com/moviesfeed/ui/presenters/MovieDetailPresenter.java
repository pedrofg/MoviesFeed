package com.moviesfeed.ui.presenters;

import android.content.Context;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.text.TextUtils;
import android.util.Log;

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
import com.moviesfeed.ui.components.AppBarStateChangeListener.State;

import java.text.NumberFormat;
import java.util.List;


/**
 * Created by Pedro on 8/24/2016.
 */
public class MovieDetailPresenter extends DetailsPresenter implements MovieDetailInteractorCallback {


    public interface MovieDetailPresenterCallback extends DetailsPresenter.DetailsPresenterCallback {

        void showError(String message);

        void contentUpdated(boolean error);

        void updatingContent();

        void showMovieTitle(String title);

        void showMovieYear(String year);

        void showMovieRuntime(String runtime);

        void showRating(String rating, float ratingFloat);

        void showOverview(String overview);

        void showHomepage(Uri homepage);

        void showGenres(String genres);

        void showCastAndCrew(List<Cast> castList, List<Crew> crewList);

        void showSimilarMovies(List<Movie> movieList);

        void showReviews(List<Review> reviewList);

        void showVideos(List<Video> videoList);

        void showImages(List<MovieBackdrop> imageList);

        void openVideoUrl(Uri url);

        void openMovieDetail(int movieId);

        void openReview(Uri url);

        void openPersonDetails(int id);

        void showBudget(String budget);

        void showRevenue(String revenue);
    }

    private MovieDetailInteractor movieDetailInteractor;
    private MovieDetailPresenterCallback callback;
    private String MINUTES = "m";


    public void appBarLayoutStateChanged(AppBarLayout appBarLayout, State state, int verticalOffset) {
        super.appBarLayoutStateChanged(appBarLayout, state, verticalOffset, this.movieDetailInteractor.getMovieTitle());
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
        setCallback(callback);
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

    public void onCastCrewClicked(int id) {
        callback.openPersonDetails(id);
    }

    public String formatCurrency(long currency) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        return formatter.format(currency);
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

        if (movieDetail.getBudget() > 0) {
            this.callback.showBudget(formatCurrency(movieDetail.getBudget()));
        }

        if (movieDetail.getRevenue() > 0) {
            this.callback.showRevenue(formatCurrency(movieDetail.getRevenue()));
        }

        if (!TextUtils.isEmpty(movieDetail.getHomepage())) {
            Uri url = Uri.parse(movieDetail.getHomepage());
            this.callback.showHomepage(url);
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
