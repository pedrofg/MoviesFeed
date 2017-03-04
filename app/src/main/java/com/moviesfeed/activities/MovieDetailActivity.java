package com.moviesfeed.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.moviesfeed.R;
import com.moviesfeed.activities.uicomponents.AppBarStateChangeListener;
import com.moviesfeed.activities.uicomponents.DividerItemDecoration;
import com.moviesfeed.activities.uicomponents.RecyclerItemClickListener;
import com.moviesfeed.adapters.CastCrewAdapter;
import com.moviesfeed.adapters.FeedAdapter;
import com.moviesfeed.adapters.MovieImagesAdapter;
import com.moviesfeed.adapters.MovieVideosAdapter;
import com.moviesfeed.adapters.ReviewsAdapter;
import com.moviesfeed.models.Movie;
import com.moviesfeed.models.MovieBackdrop;
import com.moviesfeed.models.MovieDetail;
import com.moviesfeed.models.Video;
import com.moviesfeed.presenters.MovieDetailPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nucleus5.factory.RequiresPresenter;

import static com.moviesfeed.activities.FeedActivity.INTENT_MOVIE_DETAIL_ID;


@RequiresPresenter(MovieDetailPresenter.class)
public class MovieDetailActivity extends AnimatedTransitionActivity<MovieDetailPresenter> implements RecyclerItemClickListener.OnItemClickListener {

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
    @BindView(R.id.txtMovieHomepage)
    TextView txtMovieHomepage;
    @BindView(R.id.txtMovieGenres)
    TextView txtMovieGenres;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.collapsingToolBar)
    CollapsingToolbarLayout collapsingToolBar;
    @BindView(R.id.recyclerViewMovieImages)
    RecyclerView rvMovieImages;
    @BindView(R.id.progressLoadingMovies)
    ProgressBar progressLoadingMovies;
    @BindView(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;
    @BindView(R.id.rbMovieRating)
    RatingBar rbMovieRating;
    @BindView(R.id.layoutMovieRating)
    View layoutMovieRating;
    @BindView(R.id.txtMovieDetailError)
    TextView txtMovieDetailError;
    @BindView(R.id.recyclerViewMovieVideos)
    RecyclerView rvMovieVideos;
    @BindView(R.id.layoutVideos)
    View layoutVideos;
    @BindView(R.id.recyclerViewMovieCastCrew)
    RecyclerView rvMovieCastCrew;
    @BindView(R.id.layoutCastCrew)
    View layoutCastCrew;
    @BindView(R.id.layoutDetailsError)
    View layoutError;
    @BindView(R.id.recyclerViewSimilarMovies)
    RecyclerView rvSimilarMovies;
    @BindView(R.id.layoutSimilarMovies)
    View layoutSimilarMovies;
    @BindView(R.id.recyclerViewReviews)
    RecyclerView rvReviews;
    @BindView(R.id.layoutReviews)
    View layoutReviews;

    public static final String MINUTES = "m";
    public static final String EMPTY_STRING = " ";
    public static final int MAX_ALPHA = 255;
    private MovieVideosAdapter rvVideosAdapter;
    private FeedAdapter rvSimilarMoviesAdapter;
    private MovieDetail movieDetail;
    private int movieId;
    private DividerItemDecoration dividerItemDecoration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
        getPresenter().init(this);

        this.movieId = getIntent().getIntExtra(INTENT_MOVIE_DETAIL_ID, 0);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        this.appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state, int verticalOffset) {
                if (state == State.COLLAPSED && movieDetail != null)
                    toolbar.setTitle(movieDetail.getTitle());
                else if (!toolbar.getTitle().equals(EMPTY_STRING))
                    toolbar.setTitle(EMPTY_STRING);

                //measuring for alpha
                int toolBarHeight = toolbar.getMeasuredHeight();
                int appBarHeight = appBarLayout.getMeasuredHeight();
                Float f = ((((float) appBarHeight - toolBarHeight) + verticalOffset) / ((float) appBarHeight - toolBarHeight)) * MAX_ALPHA;
                toolbar.getBackground().mutate().setAlpha(MAX_ALPHA - Math.round(f));
            }
        });


        RecyclerItemClickListener itemClickListener = new RecyclerItemClickListener(this, this);
        this.rvMovieVideos.addOnItemTouchListener(itemClickListener);
        this.rvSimilarMovies.addOnItemTouchListener(itemClickListener);

        this.rvMovieVideos.setHasFixedSize(true);
        this.rvSimilarMovies.setHasFixedSize(true);
        this.rvMovieImages.setHasFixedSize(true);
        this.rvMovieCastCrew.setHasFixedSize(true);

        this.dividerItemDecoration = new DividerItemDecoration(getDrawable(R.drawable.list_separator), false, false);
        requestMovieDetail();
    }

    private void updatingContent() {
        hideAllViews();
        this.progressLoadingMovies.setVisibility(View.VISIBLE);
    }


    private void contentUpdated(boolean error) {
        hideAllViews();
        if (error) {
            this.layoutError.setVisibility(View.VISIBLE);
        } else {
            this.appBarLayout.setVisibility(View.VISIBLE);
            this.nestedScrollView.setVisibility(View.VISIBLE);
        }
    }

    private void hideAllViews() {
        this.progressLoadingMovies.setVisibility(View.GONE);
        this.appBarLayout.setVisibility(View.GONE);
        this.nestedScrollView.setVisibility(View.GONE);
        this.layoutError.setVisibility(View.GONE);
    }


    private float calculateRatingByVotes(double votes) {
        return (float) votes * 5 / 10;
    }

    private void requestMovieDetail() {
        updatingContent();
        getPresenter().requestMovieDetail(this.movieId);
    }

    @OnClick(R.id.txtMovieDetailError)
    public void txtErrorClickListener(View view) {
        requestMovieDetail();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void requestMovieDetailSuccess(final MovieDetail movieDetail) {
        this.movieDetail = movieDetail;

        //Fill movie info
        this.txtMovieTitle.setText(this.movieDetail.getTitle());

        if (this.movieDetail.getReleaseDate() != null && !this.movieDetail.getReleaseDate().isEmpty()) {
            this.txtMovieYear.setText(this.movieDetail.getReleaseDate().substring(0, 4));
            this.txtMovieYear.setVisibility(View.VISIBLE);
        }
        if (this.movieDetail.getRuntime() > 0) {
            this.txtMovieRuntime.setText(this.movieDetail.getRuntime() + MINUTES);
            this.txtMovieRuntime.setVisibility(View.VISIBLE);
        }
        if (this.movieDetail.getVoteAverage() > 0) {
            this.layoutMovieRating.setVisibility(View.VISIBLE);
            this.txtMovieRating.setText(String.valueOf(this.movieDetail.getVoteAverage()));
            this.rbMovieRating.setRating(calculateRatingByVotes(this.movieDetail.getVoteAverage()));
        }

        this.txtMovieOverview.setText(this.movieDetail.getOverview());

        if (!TextUtils.isEmpty(this.movieDetail.getHomepage())) {
            this.txtMovieHomepage.setText(this.movieDetail.getHomepage());
            this.txtMovieHomepage.setVisibility(View.VISIBLE);
        }

        fillGenresText();
        contentUpdated(false);
        fillMovieImages();
        fillMovieVideos();
        fillMovieCastAndCrew();
        fillSimilarMovies();
        fillReviews();
    }


    private void fillGenresText() {
        //Fill Genres
        if (this.movieDetail.getGenres().size() > 0) {
            String jumpLine = "\n";
            String[] genreNames = new String[this.movieDetail.getGenres().size()];
            for (int i = 0; i < this.movieDetail.getGenres().size(); i++) {
                genreNames[i] = this.movieDetail.getGenres().get(i).getName().replaceAll(" ", "-");
            }
            this.txtMovieGenres.setText(TextUtils.join(jumpLine, genreNames));
            this.txtMovieGenres.setVisibility(View.VISIBLE);
        }
    }

    private void fillMovieCastAndCrew() {
        if (this.movieDetail.getCredits() != null &&
                this.movieDetail.getCredits().getCast() != null &&
                this.movieDetail.getCredits().getCrew() != null &&
                (this.movieDetail.getCredits().getCast().size() > 0 || this.movieDetail.getCredits().getCrew().size() > 0)) {
            this.rvMovieCastCrew.setLayoutManager(getHorizontalLayoutManager());
            this.rvMovieCastCrew.addItemDecoration(this.dividerItemDecoration);
            CastCrewAdapter adapter = new CastCrewAdapter(this, this.movieDetail.getCredits().getCast(), this.movieDetail.getCredits().getCrew());
            this.rvMovieCastCrew.setAdapter(adapter);
            this.layoutCastCrew.setVisibility(View.VISIBLE);
        }
    }

    private void fillSimilarMovies() {
        if (this.movieDetail.getSimilarMovies() != null &&
                this.movieDetail.getSimilarMovies().getMovies() != null &&
                this.movieDetail.getSimilarMovies().getMovies().size() > 0) {
            this.rvSimilarMovies.setLayoutManager(getHorizontalLayoutManager());
            this.rvSimilarMovies.addItemDecoration(this.dividerItemDecoration);
            this.rvSimilarMoviesAdapter = new FeedAdapter(this);
            this.rvSimilarMoviesAdapter.setMovies(this.movieDetail.getSimilarMovies().getMovies());
            this.rvSimilarMovies.setAdapter(this.rvSimilarMoviesAdapter);
            this.layoutSimilarMovies.setVisibility(View.VISIBLE);

        }
    }

    private void fillReviews() {
        if (this.movieDetail.getMovieReviews() != null &&
                this.movieDetail.getMovieReviews().getReviews() != null &&
                this.movieDetail.getMovieReviews().getReviews().size() > 0) {
            this.rvReviews.setLayoutManager(getHorizontalLayoutManager());
            this.rvReviews.addItemDecoration(this.dividerItemDecoration);
            ReviewsAdapter reviewsAdapter = new ReviewsAdapter(this, this.movieDetail.getMovieReviews().getReviews());
            this.rvReviews.setAdapter(reviewsAdapter);
            this.layoutReviews.setVisibility(View.VISIBLE);

        }
    }


    private void fillMovieVideos() {
        if (this.movieDetail.getVideos() != null &&
                this.movieDetail.getVideos().getVideos() != null &&
                this.movieDetail.getVideos().getVideos().size() > 0) {
            this.rvMovieVideos.setLayoutManager(getHorizontalLayoutManager());
            this.rvMovieVideos.addItemDecoration(this.dividerItemDecoration);
            this.rvVideosAdapter = new MovieVideosAdapter(this, this.movieDetail.getVideos().getVideos());
            this.rvMovieVideos.setAdapter(this.rvVideosAdapter);
            this.layoutVideos.setVisibility(View.VISIBLE);
        }
    }

    private void fillMovieImages() {
        //if there is no backdrop insert the posterPath as a backdrop so the reciclerView will contain an image to show.
        if (this.movieDetail.getImages().getBackdrops() == null || this.movieDetail.getImages().getBackdrops().size() == 0) {
            MovieBackdrop mb = new MovieBackdrop();
            mb.setFilePath(this.movieDetail.getPosterPath());

            List<MovieBackdrop> listBackdrops = new ArrayList<MovieBackdrop>();
            listBackdrops.add(mb);

            this.movieDetail.getImages().setBackdrops(listBackdrops);
        }


        this.rvMovieImages.setLayoutManager(getHorizontalLayoutManager());
        this.rvMovieImages.setAdapter(new MovieImagesAdapter(this, this.movieDetail.getImages().getBackdrops()));
    }

    public RecyclerView.LayoutManager getHorizontalLayoutManager() {
        return new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
    }

    public void requestMovieDetailError(boolean isNetworkError) {
        StringBuilder message = new StringBuilder();
        message.append(isNetworkError ? getString(R.string.error_request_feed_network) : getString(R.string.error_request_feed));
        message.append(getString(R.string.tap_here_try_again));
        this.txtMovieDetailError.setText(message);
        contentUpdated(true);
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.recyclerViewMovieVideos:
                Video video = this.rvVideosAdapter.getItem(position);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(video.getYoutubeUrl()));
                startActivity(intent);
                break;
            case R.id.recyclerViewSimilarMovies:
                Movie movie = this.rvSimilarMoviesAdapter.getItem(position);
                Intent i = new Intent(this, MovieDetailActivity.class);
                i.putExtra(INTENT_MOVIE_DETAIL_ID, movie.getIdTmdb());
                startActivity(i);
                break;
        }


    }
}
