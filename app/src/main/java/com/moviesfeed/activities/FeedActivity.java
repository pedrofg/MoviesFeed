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
import nucleus.factory.RequiresPresenter;
import nucleus.view.NucleusAppCompatActivity;

@RequiresPresenter(FeedPresenter.class)
public class FeedActivity extends NucleusAppCompatActivity<FeedPresenter> implements NavigationView.OnNavigationItemSelectedListener, EndlessScrollListener.RefreshList, RecyclerItemClickListener.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
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

        if (savedInstanceState == null)
            requestMoviesFeed(Filters.POPULARITY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Filters filter = null;

        if (id == R.id.nav_popularity) {
            filter = Filters.POPULARITY;
        } else if (id == R.id.nav_release_date) {
            filter = Filters.RELEASE_DATE;
        } else if (id == R.id.nav_revenue) {
            filter = Filters.REVENUE;
        } else if (id == R.id.nav_top_rated) {
            filter = Filters.TOP_RATED;
        } else if (id == R.id.nav_upcoming) {
            filter = Filters.UPCOMING;
        }
        Log.i(FeedActivity.class.getName(), "onNavigationItemSelected() filter: " + filter.toString());
        drawerLayout.closeDrawer(GravityCompat.START);
        refreshGrid();
        requestMoviesFeed(filter);
        return true;
    }


    private void requestMoviesFeed(Filters filter) {
        this.swipeRefreshLayout.setVisibility(View.GONE);
        this.progressBar.setVisibility(View.VISIBLE);

        getPresenter().requestMoviesFeed(this, filter);
    }

    public void requestMoviesFeedCallback(MoviesFeed moviesFeed, boolean isNextPage, boolean isUpdating, int insertedMoviesCount) {
        Log.i(FeedActivity.class.getName(), "requestMoviesFeedCallback() moviesFeed id: " + moviesFeed.getId() + " isNextPage: " + isNextPage);
        this.progressBar.setVisibility(View.GONE);
        this.swipeRefreshLayout.setVisibility(View.VISIBLE);

        this.rvAdapter.setMoviesFeed(moviesFeed);

        if (isNextPage) {
            int positionStart = this.rvAdapter.getItemCount() - insertedMoviesCount;
            this.rvAdapter.notifyItemRangeInserted(positionStart, insertedMoviesCount);
        } else {
            this.rvAdapter.notifyDataSetChanged();
        }

        if (isUpdating) {
            refreshGrid();
        }

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
        getPresenter().updateMoviesFeed();
    }
}
