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
import com.moviesfeed.adapters.MovieCastAdapter;
import com.moviesfeed.adapters.MovieCrewAdapter;
import com.moviesfeed.adapters.MovieImagesAdapter;
import com.moviesfeed.adapters.MovieVideosAdapter;
import com.moviesfeed.models.MovieBackdrop;
import com.moviesfeed.models.MovieDetail;
import com.moviesfeed.models.Video;
import com.moviesfeed.presenters.MovieDetailPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nucleus.factory.RequiresPresenter;
import nucleus.view.NucleusAppCompatActivity;

@RequiresPresenter(MovieDetailPresenter.class)
public class MovieDetailActivity extends NucleusAppCompatActivity<MovieDetailPresenter> implements RecyclerItemClickListener.OnItemClickListener {

    public static final String MINUTES = "m";
    @BindView(R.id.toolbar)
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
    @BindView(R.id.recyclerViewMovieCast)
    RecyclerView rvMovieCast;
    @BindView(R.id.layoutCast)
    View layoutCast;
    @BindView(R.id.recyclerViewMovieCrew)
    RecyclerView rvMovieCrew;
    @BindView(R.id.layoutCrew)
    View layoutCrew;

    private MovieVideosAdapter rvVideosAdapter;
    private MovieDetail movieDetail;
    private int movieId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        this.movieId = getIntent().getIntExtra(FeedActivity.INTENT_MOVIE_DETAIL_ID, 0);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        this.appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.COLLAPSED && movieDetail != null)
                    collapsingToolBar.setTitle(movieDetail.getTitle());
                else
                    collapsingToolBar.setTitle(" ");
            }
        });

        this.rvMovieVideos.addOnItemTouchListener(new RecyclerItemClickListener(this, this));
        this.rvMovieVideos.setHasFixedSize(true);

        requestMovieDetail();
    }

    private void updatingContent() {
        hideAllViews();
        this.progressLoadingMovies.setVisibility(View.VISIBLE);
    }


    private void contentUpdated(boolean error) {
        hideAllViews();
        if (error) {
            this.txtMovieDetailError.setVisibility(View.VISIBLE);
        } else {
            this.appBarLayout.setVisibility(View.VISIBLE);
            this.nestedScrollView.setVisibility(View.VISIBLE);
        }
    }

    private void hideAllViews() {
        this.progressLoadingMovies.setVisibility(View.GONE);
        this.appBarLayout.setVisibility(View.GONE);
        this.nestedScrollView.setVisibility(View.GONE);
        this.txtMovieDetailError.setVisibility(View.GONE);
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
        this.txtMovieHomepage.setText(this.movieDetail.getHomepage());

        //Fill Genres
        if (this.movieDetail.getGenres().size() > 0) {
            String[] genreNames = new String[this.movieDetail.getGenres().size()];
            for (int i = 0; i < this.movieDetail.getGenres().size(); i++) {
                genreNames[i] = this.movieDetail.getGenres().get(i).getName();
            }
            this.txtMovieGenres.setText(TextUtils.join("\n", genreNames));
            this.txtMovieGenres.setVisibility(View.VISIBLE);
        }

        contentUpdated(false);
        fillMovieImages();
        fillMovieVideos();
        fillMovieCast();
        fillMovieCrew();

    }

    private void fillMovieCrew() {
        if (this.movieDetail.getCredits() != null && this.movieDetail.getCredits().getCrew() != null && this.movieDetail.getCredits().getCrew().size() > 0) {
            this.rvMovieCrew.setLayoutManager(getHorizontalLayoutManager());
            this.rvMovieCrew.addItemDecoration(new DividerItemDecoration(getDrawable(R.drawable.list_separator), false, false));
            MovieCrewAdapter adapter = new MovieCrewAdapter(this, this.movieDetail.getCredits().getCrew());
            this.rvMovieCrew.setAdapter(adapter);
            this.layoutCrew.setVisibility(View.VISIBLE);
        }
    }

    private void fillMovieCast() {
        if (this.movieDetail.getCredits() != null && this.movieDetail.getCredits().getCast() != null && this.movieDetail.getCredits().getCast().size() > 0) {
            this.rvMovieCast.setLayoutManager(getHorizontalLayoutManager());
            this.rvMovieCast.addItemDecoration(new DividerItemDecoration(getDrawable(R.drawable.list_separator), false, false));
            MovieCastAdapter adapter = new MovieCastAdapter(this, this.movieDetail.getCredits().getCast());
            this.rvMovieCast.setAdapter(adapter);
            this.layoutCast.setVisibility(View.VISIBLE);
        }
    }


    private void fillMovieVideos() {
        if (this.movieDetail.getVideos() != null &&
                this.movieDetail.getVideos().getVideos() != null &&
                this.movieDetail.getVideos().getVideos().size() > 0) {


            this.rvMovieVideos.setLayoutManager(getHorizontalLayoutManager());
            this.rvMovieVideos.addItemDecoration(new DividerItemDecoration(getDrawable(R.drawable.list_separator), false, false));
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
        Video video = this.rvVideosAdapter.getItem(position);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(video.getYoutubeUrl()));
        startActivity(intent);
    }
}
