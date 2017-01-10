package com.moviesfeed.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.moviesfeed.R;
import com.moviesfeed.activities.uicomponents.EndlessScrollListener;
import com.moviesfeed.adapters.FeedAdapter;
import com.moviesfeed.api.Filters;

import android.support.design.widget.NavigationView;

import com.moviesfeed.models.Movie;
import com.moviesfeed.models.MoviesFeed;
import com.moviesfeed.presenters.FeedPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import nucleus.factory.RequiresPresenter;
import nucleus.view.NucleusAppCompatActivity;

@RequiresPresenter(FeedPresenter.class)
public class FeedActivity extends NucleusAppCompatActivity<FeedPresenter> implements NavigationView.OnNavigationItemSelectedListener, EndlessScrollListener.RefreshList {
    public static final String INTENT_MOVIE_DETAIL_ID = "INTENT_MOVIE_DETAIL_ID";
    @BindView(R.id.gridMoviesFeed)
    GridView gridMoviesFeed;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    private FeedAdapter adapter;
    private EndlessScrollListener scrollListener;

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

        setUpAdapter();

        this.scrollListener = new EndlessScrollListener(this.gridMoviesFeed, this);
        this.gridMoviesFeed.setOnScrollListener(scrollListener);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null)
            requestMoviesFeed(Filters.POPULARITY);
    }

    private void setUpAdapter() {
        Log.i(FeedActivity.class.getName(), "setUpAdapter()");
        this.adapter = new FeedAdapter(this);
        this.gridMoviesFeed.setAdapter(adapter);
    }

    public void requestMoviesFeedCallback(MoviesFeed moviesFeed, boolean isNextPage) {
        Log.i(FeedActivity.class.getName(), "requestMoviesFeedCallback() moviesFeed id: " + moviesFeed.getId() + " isNextPage: " + isNextPage);
        this.progressBar.setVisibility(View.GONE);
        this.gridMoviesFeed.setVisibility(View.VISIBLE);

        this.adapter.setMoviesFeed(moviesFeed);
        this.adapter.notifyDataSetChanged();

        if (isNextPage) {
            Log.i(FeedActivity.class.getName(), "requestMoviesFeedCallback() notifyMorePages");
            this.scrollListener.notifyMorePages();
        }
    }

    private void requestMoviesFeed(Filters filter) {
        this.gridMoviesFeed.setVisibility(View.GONE);
        this.progressBar.setVisibility(View.VISIBLE);

        getPresenter().requestMoviesFeed(this, filter);
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
        setUpAdapter();
        requestMoviesFeed(filter);
        return true;
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
    public void onRefresh(int pageNumber) {
        Log.i(FeedActivity.class.getName(), "onRefresh() - page number: " + pageNumber);
        getPresenter().requestNextPage();
    }

    @OnItemClick(R.id.gridMoviesFeed)
    public void onItemClick(int position) {
        Movie movie = this.adapter.getItem(position);
        Log.i(FeedActivity.class.getName(), "onItemClick, Movie Title: " + movie.getTitle());
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(INTENT_MOVIE_DETAIL_ID, movie.getIdTmdb());
        startActivity(intent);
    }


}
