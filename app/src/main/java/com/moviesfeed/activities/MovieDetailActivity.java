package com.moviesfeed.activities;

import android.content.Intent;
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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.moviesfeed.R;
import com.moviesfeed.activities.uicomponents.AppBarStateChangeListener;
import com.moviesfeed.adapters.MovieImagesAdapter;
import com.moviesfeed.api.MoviesApi;
import com.moviesfeed.models.MovieBackdrop;
import com.moviesfeed.models.MovieDetail;
import com.moviesfeed.presenters.MovieDetailPresenter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

import butterknife.BindView;
import butterknife.ButterKnife;
import nucleus.factory.RequiresPresenter;
import nucleus.view.NucleusAppCompatActivity;

@RequiresPresenter(MovieDetailPresenter.class)
public class MovieDetailActivity extends NucleusAppCompatActivity<MovieDetailPresenter> {

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


    private MovieDetail movieDetail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        int movieId = intent.getIntExtra(FeedActivity.INTENT_MOVIE_DETAIL_ID, 0);

        //Fill Toolbar
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

        getPresenter().requestMoviedetail(movieId);
    }

    private void showLayoutContent() {
        this.progressLoadingMovies.setVisibility(View.GONE);
        this.appBarLayout.setVisibility(View.VISIBLE);
        this.nestedScrollView.setVisibility(View.VISIBLE);
    }

    public void requestMovieDetailCallbackSuccess(final MovieDetail movieDetail) {
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
            this.txtMovieRating.setText(String.valueOf(this.movieDetail.getVoteAverage()));
            this.txtMovieRating.setVisibility(View.VISIBLE);
            float rating = (float) ((this.movieDetail.getVoteAverage() * 5) / 10);
            this.rbMovieRating.setRating(rating);
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

        showLayoutContent();
        fillMovieImages();

    }

    private void fillMovieImages() {
        //if there is no backdrop insert the posterPath as a backdrop so the reciclerView will contain an image to show.
        if (this.movieDetail.getBackdrops() == null) {
            MovieBackdrop mb = new MovieBackdrop();
            mb.setFilePath(this.movieDetail.getPosterPath());

            List<MovieBackdrop> listBackdrops = new ArrayList<MovieBackdrop>();
            listBackdrops.add(mb);

            this.movieDetail.setBackdrops(listBackdrops);
        }

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        this.rvMovieImages.setLayoutManager(layoutManager);
        this.rvMovieImages.setAdapter(new MovieImagesAdapter(this, this.movieDetail.getBackdrops()));
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
}
