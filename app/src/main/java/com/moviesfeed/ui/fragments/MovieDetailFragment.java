package com.moviesfeed.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;
import com.moviesfeed.R;
import com.moviesfeed.models.Cast;
import com.moviesfeed.models.Crew;
import com.moviesfeed.models.Movie;
import com.moviesfeed.models.MovieBackdrop;
import com.moviesfeed.models.Review;
import com.moviesfeed.models.Video;
import com.moviesfeed.ui.components.AppBarStateChangeListener;
import com.moviesfeed.ui.components.CustomLinearLayoutManager;
import com.moviesfeed.ui.components.DividerItemDecoration;
import com.moviesfeed.ui.components.RecyclerItemClickListener;
import com.moviesfeed.ui.adapters.CastCrewAdapter;
import com.moviesfeed.ui.adapters.FeedAdapter;
import com.moviesfeed.ui.adapters.MovieImagesAdapter;
import com.moviesfeed.ui.adapters.MovieVideosAdapter;
import com.moviesfeed.ui.adapters.ReviewsAdapter;
import com.moviesfeed.ui.presenters.MovieDetailPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.moviesfeed.ui.activities.FeedActivity.INTENT_MOVIE_DETAIL_ID;

/**
 * Created by Pedro on 2017-03-23.
 */

public class MovieDetailFragment extends DetailsFragment implements MovieDetailPresenter.MovieDetailPresenterCallback, RecyclerItemClickListener.OnItemClickListener, MovieImagesAdapter.ImagesAdapterCallback, FeedAdapter.OnFeedItemClicked, CastCrewAdapter.OnCastCrewItemClicked {


    public interface MovieDetailFragmentCallback {
        void openMovieDetail(int movieId);

        void openVideoUrl(Uri url);

        void openReviewUrl(Uri url);

        void openMovieHomepage(Uri url);

        void openPersonDetails(int personID);

        void setToolbar(Toolbar toolbar);
    }

    public static final int TXT_TITLE_MAX_LINES = 1;
    private MovieDetailPresenter presenter;
    private Unbinder unbinder;
    private MovieDetailFragmentCallback callback;
    private DividerItemDecoration dividerItemDecoration;
    private FeedAdapter rvSimilarMoviesAdapter;
    private MovieVideosAdapter rvVideosAdapter;
    private ReviewsAdapter rvReviewsAdapter;
    private CustomLinearLayoutManager rvMovieImagesLayoutManager;

    @BindView(R.id.toolbarMovieDetail)
    Toolbar toolbar;
    @BindView(R.id.txtMovieTitle)
    TextView txtMovieTitle;
    @BindView(R.id.txtMovieYear)
    TextView txtMovieYear;
    @BindView(R.id.txtMovieRuntime)
    TextView txtMovieRuntime;
    @BindView(R.id.txtMovieRating)
    TextView txtMovieRating;
    @BindView(R.id.txtMovieOverview)
    TextView txtMovieOverview;
    @BindView(R.id.imgHomepage)
    ImageView imgHomePage;
    @BindView(R.id.txtMovieGenres)
    TextView txtMovieGenres;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.collapsingToolBar)
    CollapsingToolbarLayout collapsingToolBar;
    @BindView(R.id.recyclerViewMovieImages)
    RecyclerViewPager rvMovieImages;
    @BindView(R.id.progressLoadingMovies)
    ProgressBar progressLoadingMovies;
    @BindView(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;
    @BindView(R.id.rbMovieRating)
    RatingBar rbMovieRating;
    @BindView(R.id.layoutMovieRating)
    View layoutMovieRating;
    @BindView(R.id.txtError)
    TextView txtMovieDetailError;
    @BindView(R.id.recyclerViewMovieVideos)
    RecyclerView rvMovieVideos;
    @BindView(R.id.cardViewVideos)
    View layoutVideos;
    @BindView(R.id.recyclerViewMovieCastCrew)
    RecyclerView rvMovieCastCrew;
    @BindView(R.id.cardViewCastCrew)
    View layoutCastCrew;
    @BindView(R.id.layoutError)
    View layoutError;
    @BindView(R.id.recyclerViewSimilarMovies)
    RecyclerView rvSimilarMovies;
    @BindView(R.id.cardViewSimilarMovies)
    View layoutSimilarMovies;
    @BindView(R.id.recyclerViewReviews)
    RecyclerView rvReviews;
    @BindView(R.id.cardViewReviews)
    View layoutReviews;
    @BindView(R.id.viewRoot)
    CoordinatorLayout viewRoot;
    @BindView(R.id.movieDetailsAdView)
    AdView adView;

    public MovieDetailFragment() {
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MovieDetailFragmentCallback) {
            this.callback = (MovieDetailFragmentCallback) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            this.presenter = new MovieDetailPresenter();
            this.presenter.init(context(), this);
            this.dividerItemDecoration = new DividerItemDecoration(ContextCompat.getDrawable(context(), R.drawable.list_separator), false, false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        this.unbinder = ButterKnife.bind(this, fragmentView);

        this.callback.setToolbar(toolbar);

        this.appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state, int verticalOffset) {
                presenter.appBarLayoutStateChanged(appBarLayout, state, verticalOffset);
            }
        });

        RecyclerItemClickListener itemClickListener = new RecyclerItemClickListener(context(), this);
        this.rvMovieVideos.addOnItemTouchListener(itemClickListener);
        this.rvReviews.addOnItemTouchListener(itemClickListener);

        this.rvMovieVideos.setHasFixedSize(true);
        this.rvSimilarMovies.setHasFixedSize(true);
        this.rvMovieImages.setHasFixedSize(true);
        this.rvMovieCastCrew.setHasFixedSize(true);
        this.rvReviews.setHasFixedSize(true);

        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            this.presenter.requestMovieDetail(getArguments().getInt(INTENT_MOVIE_DETAIL_ID));
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }
    }

    @Override
    public void showError(String message) {
        this.txtMovieDetailError.setText(message);
    }

    @Override
    public Context context() {
        return this.getActivity().getApplicationContext();
    }

    @Override
    public void contentUpdated(boolean error) {
        hideAllViews();
        if (error) {
            this.layoutError.setVisibility(View.VISIBLE);
        } else {
            this.appBarLayout.setVisibility(View.VISIBLE);
            this.nestedScrollView.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void updatingContent() {
        hideAllViews();
        this.progressLoadingMovies.setVisibility(View.VISIBLE);
    }

    @Override
    public void showMovieTitle(String title) {
        this.txtMovieTitle.setText(title);
        this.txtMovieTitle.setMaxLines(TXT_TITLE_MAX_LINES);
    }

    @Override
    public void showMovieYear(String year) {
        this.txtMovieYear.setText(year);
        this.txtMovieYear.setVisibility(View.VISIBLE);
    }

    @Override
    public void showMovieRuntime(String runtime) {
        this.txtMovieRuntime.setText(runtime);
        this.txtMovieRuntime.setVisibility(View.VISIBLE);
    }

    @Override
    public void showRating(String rating, float ratingFloat) {
        this.layoutMovieRating.setVisibility(View.VISIBLE);
        this.txtMovieRating.setText(rating);
        this.rbMovieRating.setRating(ratingFloat);
    }

    @Override
    public void showOverview(String overview) {
        this.txtMovieOverview.setText(overview);
    }

    @Override
    public void showHomepage(Uri homepage) {
        this.imgHomePage.setVisibility(View.VISIBLE);
        this.imgHomePage.setOnClickListener(v -> {
            callback.openMovieHomepage(homepage);
        });
    }

    @Override
    public void showGenres(String genres) {
        this.txtMovieGenres.setText(genres);
        this.txtMovieGenres.setVisibility(View.VISIBLE);
    }

    @Override
    public void showCastAndCrew(List<Cast> castList, List<Crew> crewList) {
        this.rvMovieCastCrew.setLayoutManager(getHorizontalLayoutManager());
        this.rvMovieCastCrew.addItemDecoration(this.dividerItemDecoration);
        CastCrewAdapter adapter = new CastCrewAdapter(context(), castList, crewList, this);
        this.rvMovieCastCrew.setAdapter(adapter);
        this.layoutCastCrew.setVisibility(View.VISIBLE);
    }

    @Override
    public void showSimilarMovies(List<Movie> movieList) {
        this.rvSimilarMovies.setLayoutManager(getHorizontalLayoutManager());
        this.rvSimilarMoviesAdapter = new FeedAdapter(context(), this);
        rvSimilarMoviesAdapter.setMovies(movieList);
        this.rvSimilarMovies.setAdapter(rvSimilarMoviesAdapter);
        this.layoutSimilarMovies.setVisibility(View.VISIBLE);
    }

    @Override
    public void showReviews(List<Review> reviewList) {
        this.rvReviews.setLayoutManager(getHorizontalLayoutManager());
        this.rvReviewsAdapter = new ReviewsAdapter(context(), reviewList);
        this.rvReviews.setAdapter(rvReviewsAdapter);
        this.layoutReviews.setVisibility(View.VISIBLE);
    }

    @Override
    public void showVideos(List<Video> videoList) {
        this.rvMovieVideos.setLayoutManager(getHorizontalLayoutManager());
        this.rvMovieVideos.addItemDecoration(this.dividerItemDecoration);
        this.rvVideosAdapter = new MovieVideosAdapter(context(), videoList);
        this.rvMovieVideos.setAdapter(rvVideosAdapter);
        this.layoutVideos.setVisibility(View.VISIBLE);
    }

    @Override
    public void showImages(List<MovieBackdrop> imageList) {
        this.rvMovieImagesLayoutManager = new CustomLinearLayoutManager(context(), LinearLayoutManager.HORIZONTAL);
        this.rvMovieImages.setLayoutManager(this.rvMovieImagesLayoutManager);
        this.rvMovieImages.setAdapter(new MovieImagesAdapter(context(), imageList, this));
    }

    @Override
    public void updateToolbarTitle(String title) {
        super.updateToolbarTitle(this.toolbar, title);
    }

    @Override
    public String getToolbarTitle() {
        return super.getToolbarTitle(this.toolbar);
    }

    @Override
    public int getToolbarHeight() {
        return super.getToolbarHeight(this.toolbar);
    }

    @Override
    public void updateToolbarBackground(int alpha) {
        super.updateToolbarBackground(this.toolbar, alpha);
    }

    @Override
    public void openVideoUrl(Uri url) {
        this.callback.openVideoUrl(url);
    }

    @Override
    public void openMovieDetail(int movieId) {
        this.callback.openMovieDetail(movieId);
    }

    @Override
    public void openPersonDetails(int personID) {
        this.callback.openPersonDetails(personID);
    }


    @Override
    public void openReview(Uri url) {
        this.callback.openReviewUrl(url);
    }


    @Override
    public void onResume() {
        super.onResume();
        this.presenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.presenter.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.presenter.destroy();
    }

    @Override
    public void onDestroyView() {
        this.unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.callback = null;
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.recyclerViewMovieVideos:
                Video video = rvVideosAdapter.getItem(position);
                this.presenter.onVideoClicked(video);
                break;
            case R.id.recyclerViewReviews:
                Review review = rvReviewsAdapter.getItem(position);
                this.presenter.onReviewClicked(review);
                break;
        }
    }

    @Override
    public void onFeedItemClicked(Movie movie) {
        this.presenter.onSimilarMovieClicked(movie);
    }

    @Override
    public void onBtnUpdateFeedClicked() {
        //do nothing.
    }


    @OnClick(R.id.txtTryAgain)
    public void onClickTxtError(View v) {
        this.presenter.tryAgain();
    }

    public RecyclerView.LayoutManager getHorizontalLayoutManager() {
        return new LinearLayoutManager(context(), LinearLayoutManager.HORIZONTAL, false);
    }


    private void hideAllViews() {
        this.progressLoadingMovies.setVisibility(View.GONE);
        this.appBarLayout.setVisibility(View.GONE);
        this.nestedScrollView.setVisibility(View.GONE);
        this.layoutError.setVisibility(View.GONE);
    }

    @Override
    public void animImagePoster(ImageView imageView, int height, ImageView.ScaleType scaleType) {
        super.animImagePoster(this.viewRoot, imageView, this.appBarLayout, height, scaleType);
    }

    @Override
    public void setScrollsEnable(boolean enable) {
        super.setScrollsEnable(enable, this.appBarLayout, this.rvMovieImagesLayoutManager);
    }

    @Override
    public void onImageViewClicked(ImageView imageView) {
        this.presenter.onImagePosterClicked(imageView);
    }

    @Override
    public void onCastCrewItemClicked(int id) {
        this.presenter.onCastCrewClicked(id);
    }
}
