package com.moviesfeed.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.moviesfeed.R;
import com.moviesfeed.activities.uicomponents.EndlessScrollListener;
import com.moviesfeed.activities.uicomponents.RecyclerItemClickListener;
import com.moviesfeed.adapters.FeedAdapter;
import com.moviesfeed.api.Filters;
import com.moviesfeed.models.Movie;
import com.moviesfeed.models.MoviesFeed;
import com.moviesfeed.presenters.FeedPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nucleus.factory.RequiresPresenter;
import nucleus.view.NucleusAppCompatActivity;

@RequiresPresenter(FeedPresenter.class)
public class FeedActivity extends NucleusAppCompatActivity<FeedPresenter> implements NavigationView.OnNavigationItemSelectedListener, EndlessScrollListener.RefreshList, RecyclerItemClickListener.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    public static final String INTENT_MOVIE_DETAIL_ID = "INTENT_MOVIE_DETAIL_ID";
    public static final int GRID_COLUMNS = 3;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.rvMoviesFeed)
    RecyclerView rvMoviesFeed;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.txtError)
    TextView txtError;
    View settingsIcon;

    private EndlessScrollListener endlessScrollListener;
    private FeedAdapter rvAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(FeedActivity.class.getName(), "onCreate()");
        setContentView(R.layout.activity_feed_root);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, GRID_COLUMNS);
        this.rvMoviesFeed.setLayoutManager(gridLayoutManager);

        this.endlessScrollListener = new EndlessScrollListener(gridLayoutManager, this);
        this.rvMoviesFeed.addOnScrollListener(endlessScrollListener);

        this.rvAdapter = new FeedAdapter(this);
        this.rvMoviesFeed.setAdapter(rvAdapter);

        this.rvMoviesFeed.addOnItemTouchListener(new RecyclerItemClickListener(this, this));
        this.rvMoviesFeed.setHasFixedSize(true);

        this.swipeRefreshLayout.setOnRefreshListener(this);
        this.swipeRefreshLayout.setColorSchemeResources(R.color.iconRed);

        View header = navigationView.getHeaderView(0);
        settingsIcon = header.findViewById(R.id.settingsIcon);
        settingsIcon.setOnClickListener(this);

        if (savedInstanceState == null)
            requestMoviesFeed(Filters.POPULARITY);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Filters filter = null;

        if (id == R.id.nav_popularity) {
            filter = Filters.POPULARITY;
        } else if (id == R.id.nav_now_playing) {
            filter = Filters.NOW_PLAYING;
        } else if (id == R.id.nav_revenue) {
            filter = Filters.REVENUE;
        } else if (id == R.id.nav_top_rated) {
            filter = Filters.TOP_RATED;
        } else if (id == R.id.nav_upcoming) {
            filter = Filters.UPCOMING;
        } else if (id == R.id.nav_comedy) {
            filter = Filters.COMEDY;
        } else if (id == R.id.nav_action) {
            filter = Filters.ACTION;
        } else if (id == R.id.nav_animation) {
            filter = Filters.ANIMATION;
        } else if (id == R.id.nav_comedy) {
            filter = Filters.COMEDY;
        } else if (id == R.id.nav_romance) {
            filter = Filters.ROMANCE;
        } else if (id == R.id.nav_drama) {
            filter = Filters.DRAMA;
        } else if (id == R.id.nav_science_fiction) {
            filter = Filters.SCIENCE_FICTION;
        } else if (id == R.id.nav_music) {
            filter = Filters.MUSIC;
        } else if (id == R.id.nav_thriller) {
            filter = Filters.THRILLER;
        } else if (id == R.id.nav_horror) {
            filter = Filters.HORROR;
        } else if (id == R.id.nav_documentary) {
            filter = Filters.DOCUMENTARY;
        } else if (id == R.id.nav_war) {
            filter = Filters.WAR;
        }
        Log.i(FeedActivity.class.getName(), "onNavigationItemSelected() filter: " + filter.toString());
        drawerLayout.closeDrawer(GravityCompat.START);

        if (filter == getPresenter().getCurrentFilter()) {
            return false;
        } else {
            refreshGrid();
            requestMoviesFeed(filter);
            return true;
        }
    }


    private void requestMoviesFeed(Filters filter) {
        updatingContent();

        getPresenter().requestMoviesFeed(filter);
    }


    private void refreshGrid() {
        Log.i(FeedActivity.class.getName(), "refreshGrid()");

        this.swipeRefreshLayout.setRefreshing(false);
        this.endlessScrollListener.reset();
        this.rvMoviesFeed.scrollToPosition(0);

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void requestNextPage(int currentPage) {
        Log.i(FeedActivity.class.getName(), "onRefresh() - page number: " + currentPage);
        getPresenter().requestNextPage();
    }

    //Grid item click listener
    @Override
    public void onItemClick(View view, int position) {
        Movie movie = this.rvAdapter.getItem(position);
        Log.i(FeedActivity.class.getName(), "onItemClick, Movie Title: " + movie.getTitle());
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(INTENT_MOVIE_DETAIL_ID, movie.getIdTmdb());
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        getPresenter().refreshMoviesFeed();
    }

    private void contentUpdated(boolean error) {
        this.progressBar.setVisibility(View.GONE);
        this.swipeRefreshLayout.setVisibility(View.VISIBLE);

        if (error) {
            this.rvMoviesFeed.setVisibility(View.GONE);
            this.txtError.setVisibility(View.VISIBLE);
        } else {
            this.rvMoviesFeed.setVisibility(View.VISIBLE);
            this.txtError.setVisibility(View.GONE);
        }
    }

    private void updatingContent() {
        this.progressBar.setVisibility(View.VISIBLE);
        this.swipeRefreshLayout.setVisibility(View.GONE);
    }

    @OnClick(R.id.txtError)
    public void txtErrorClickListener(View view) {
        updatingContent();
        getPresenter().requestMoviesFeed(getPresenter().getCurrentFilter());
    }

    public void requestMoviesFeedSuccess(MoviesFeed moviesFeed, boolean isNextPage, boolean isUpdating, int insertedMoviesCount) {
        Log.i(FeedActivity.class.getName(), "requestMoviesFeedSuccess() moviesFeed id: " + moviesFeed.getId() + " isNextPage: " + isNextPage);
        contentUpdated(false);

        int previousAdapterSize = this.rvAdapter.getItemCount();
        this.rvAdapter.setMoviesFeed(moviesFeed);

        if (isNextPage) {
            int positionStart = this.rvAdapter.getItemCount() - insertedMoviesCount;
            this.rvAdapter.notifyItemRangeInserted(positionStart, insertedMoviesCount);
        } else {
            this.rvAdapter.notifyItemRangeRemoved(0, previousAdapterSize);
            this.rvAdapter.notifyItemRangeInserted(0, insertedMoviesCount);
        }

        if (isUpdating) {
            refreshGrid();
        }

    }

    public void requestMoviesFeedError(boolean showInToast, boolean isNetworkError) {
        StringBuilder message = new StringBuilder();
        message.append(isNetworkError ? getString(R.string.error_request_feed_network) : getString(R.string.error_request_feed));
        if (showInToast) {
            message.append(getString(R.string.please_try_again));
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            message.append(getString(R.string.tap_here_try_again));
            this.txtError.setText(message);
            contentUpdated(true);
        }

        this.swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.settingsIcon) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
    }
}
