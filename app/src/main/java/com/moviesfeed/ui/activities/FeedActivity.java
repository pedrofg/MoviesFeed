package com.moviesfeed.ui.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.moviesfeed.R;
import com.moviesfeed.api.Filters;
import com.moviesfeed.models.Movie;
import com.moviesfeed.ui.fragments.FeedFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedActivity extends AppCompatActivity implements FeedFragment.FeedFragmentCallback, NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    public static final String INTENT_MOVIE_DETAIL_ID = "INTENT_MOVIE_DETAIL_ID";
    public static final int REQUEST_CODE_SETTINGS_ACTIVITY = 0;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    View settingsIcon;
    private int checkedMenuItem;
    private FeedFragment feedFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(FeedActivity.class.getName(), "onCreate()");
        setContentView(R.layout.activity_feed_root);
        ButterKnife.bind(this);

        this.toolbar.setTitle(getString(R.string.popularity));
        setSupportActionBar(this.toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        this.settingsIcon = header.findViewById(R.id.settingsIcon);
        this.settingsIcon.setOnClickListener(this);

        this.checkedMenuItem = R.id.nav_popularity;

        if (savedInstanceState == null) {
            feedFragment = new FeedFragment();
            addFragment(R.id.fragmentContainer, feedFragment);
        }
    }

    private void addFragment(int containerViewId, Fragment fragment) {
        final FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(containerViewId, fragment);
        fragmentTransaction.commit();
    }


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

            feedFragment.searchMovieFeed(query);
            this.navigationView.getMenu().findItem(this.checkedMenuItem).setChecked(false);
            this.toolbar.setTitle(query);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Filters filter = null;
        if (id != this.checkedMenuItem) {
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

            this.toolbar.setTitle(menuItem.getTitle());
            this.feedFragment.navigationItemSelected(filter);
        }

        drawerLayout.closeDrawer(GravityCompat.START);

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


    @Override
    public void onMovieClicked(Movie movie) {
        Log.i(FeedActivity.class.getName(), "onItemClick, Movie Title: " + movie.getTitle());
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(INTENT_MOVIE_DETAIL_ID, movie.getIdTmdb());
        startActivity(intent);
    }
}
