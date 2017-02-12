package com.moviesfeed.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
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
    public static final int GRID_FILL_ALL_COLUMNS = 1;
    public static final int REQUEST_CODE_SETTINGS_ACTIVITY = 0;
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
    @BindView(R.id.layoutFeedError)
    View layoutError;
    View settingsIcon;

    private FeedAdapter rvAdapter;
    private int checkedMenuItem;
    private EndlessScrollListener endlessScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(FeedActivity.class.getName(), "onCreate()");
        setContentView(R.layout.activity_feed_root);
        ButterKnife.bind(this);

        this.toolbar.setTitle(getString(R.string.popularity));
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, GRID_COLUMNS);
        gridLayoutManager.setSpanSizeLookup(onSpanSizeLookup);
        this.rvMoviesFeed.setLayoutManager(gridLayoutManager);

        this.endlessScrollListener = new EndlessScrollListener(gridLayoutManager, this);
        this.rvMoviesFeed.addOnScrollListener(endlessScrollListener);

        setRvAdapter();
        this.rvMoviesFeed.addOnItemTouchListener(new RecyclerItemClickListener(this, this));
        this.rvMoviesFeed.setHasFixedSize(true);

        this.swipeRefreshLayout.setOnRefreshListener(this);
        this.swipeRefreshLayout.setColorSchemeResources(R.color.iconRed);

        View header = navigationView.getHeaderView(0);
        this.settingsIcon = header.findViewById(R.id.settingsIcon);
        this.settingsIcon.setOnClickListener(this);

        this.checkedMenuItem = R.id.nav_popularity;

        if (savedInstanceState == null)
            requestMoviesFeed(Filters.POPULARITY);
    }


    private void setRvAdapter() {
        this.rvAdapter = new FeedAdapter(this);
        this.rvMoviesFeed.setAdapter(rvAdapter);
    }

    GridLayoutManager.SpanSizeLookup onSpanSizeLookup = new GridLayoutManager.SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            return rvAdapter.getItemViewType(position) == FeedAdapter.VIEW_ITEM ? GRID_FILL_ALL_COLUMNS : GRID_COLUMNS;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i(FeedActivity.class.getName(), "onNewIntent() " + intent.getAction());
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.i(FeedActivity.class.getName(), "handleIntent() ACTION_SEARCH query: " + query);

            this.navigationView.getMenu().findItem(this.checkedMenuItem).setChecked(false);
            this.toolbar.setTitle(query);
            refreshGrid();
            updatingContent();
            getPresenter().requestSearchMoviesFeed(query);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Filters filter = null;
        this.checkedMenuItem = item.getItemId();
        MenuItem menuItem = this.navigationView.getMenu().findItem(this.checkedMenuItem);
        menuItem.setChecked(true);

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
            this.toolbar.setTitle(menuItem.getTitle());
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

        setRvAdapter();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Grid item click listener
    @Override
    public void onItemClick(View view, int position) {
        Movie movie = this.rvAdapter.getItem(position);
        if (movie != null) {
            Log.i(FeedActivity.class.getName(), "onItemClick, Movie Title: " + movie.getTitle());
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra(INTENT_MOVIE_DETAIL_ID, movie.getIdTmdb());
            startActivity(intent);
        } else {
            Log.i(FeedActivity.class.getName(), "onItemClick movie == null / update item clicked");
            //it means the user clicked on the update icon item
            getPresenter().requestNextPage();
        }
    }

    @Override
    public void onRefresh() {
        getPresenter().refreshMoviesFeed();
    }

    private void contentUpdated(boolean error) {
        this.progressBar.setVisibility(View.GONE);
        this.swipeRefreshLayout.setVisibility(View.GONE);
        this.layoutError.setVisibility(View.GONE);

        if (error) {
            this.layoutError.setVisibility(View.VISIBLE);
        } else {
            this.swipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

    private void updatingContent() {
        this.progressBar.setVisibility(View.VISIBLE);
        this.swipeRefreshLayout.setVisibility(View.GONE);
        this.layoutError.setVisibility(View.GONE);
    }

    @OnClick(R.id.txtError)
    public void txtErrorClickListener(View view) {
        updatingContent();
        if (getPresenter().getCurrentFilter() == Filters.SEARCH) {
            getPresenter().requestSearchMoviesFeed(getPresenter().getSearchQuery());
        } else {
            getPresenter().requestMoviesFeed(getPresenter().getCurrentFilter());
        }
    }

    public void requestMoviesFeedSuccess(MoviesFeed moviesFeed, boolean isNextPage, boolean isUpdating, int insertedMoviesCount) {
        Log.i(FeedActivity.class.getName(), "requestMoviesFeedSuccess() moviesFeed id: " + moviesFeed.getId() + " isNextPage: " + isNextPage);
        if (moviesFeed.getMovies().size() > 0) {

            if (isUpdating) {
                refreshGrid();
            }

            if (isNextPage) {
                removeProgressBottomGrid();

                this.rvAdapter.setMovies(moviesFeed.getMovies());

                int positionStart = this.rvAdapter.getItemCount() - insertedMoviesCount;
                this.rvAdapter.notifyItemRangeInserted(positionStart, insertedMoviesCount);
            } else {
                this.rvAdapter.setMovies(moviesFeed.getMovies());
            }


            contentUpdated(false);
        } else {
            contentUpdated(true);
            this.txtError.setText(getString(R.string.no_movies_found));
        }
    }

    public void requestMoviesFeedError(boolean thereIsCache, boolean isNetworkError) {
        Log.i(FeedActivity.class.getName(), "requestMoviesFeedError() - thereIsCache: " + thereIsCache + " isNetworkError: " + isNetworkError);
        StringBuilder message = new StringBuilder();
        message.append(isNetworkError ? getString(R.string.error_request_feed_network) : getString(R.string.error_request_feed));
        if (thereIsCache) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            removeProgressBottomGrid();
            addProgressBottomGrid(true);
        } else {
            message.append(getString(R.string.tap_here_try_again));
            this.txtError.setText(message);
            contentUpdated(true);
        }

        this.swipeRefreshLayout.setRefreshing(false);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SETTINGS_ACTIVITY) {
            Animation rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_rotation);
            this.settingsIcon.startAnimation(rotation);
        }
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.settingsIcon) {
            Animation rotation = AnimationUtils.loadAnimation(this, R.anim.anticlockwise_rotation);
            this.settingsIcon.startAnimation(rotation);

            rotation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Intent intent = new Intent(FeedActivity.this, SettingsActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_SETTINGS_ACTIVITY);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

        }
    }

    private void addProgressBottomGrid(boolean errorProgress) {
        this.endlessScrollListener.addProgressItem();
        this.rvAdapter.addProgress(errorProgress);
    }

    private void removeProgressBottomGrid() {
        this.endlessScrollListener.removeProgressItem();
        this.rvAdapter.removeProgress();
    }

    @Override
    public void requestNextPage(int currentPage) {
        Log.i(FeedActivity.class.getName(), "requestNextPage() - page number: " + currentPage);
        removeProgressBottomGrid();
        addProgressBottomGrid(false);
        getPresenter().requestNextPage();
    }

}
