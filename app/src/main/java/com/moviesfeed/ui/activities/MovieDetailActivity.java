package com.moviesfeed.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.moviesfeed.R;
import com.moviesfeed.ui.fragments.MovieDetailFragment;

import static com.moviesfeed.ui.activities.FeedActivity.INTENT_MOVIE_DETAIL_ID;


public class MovieDetailActivity extends AnimatedTransitionActivity implements MovieDetailFragment.MovieDetailFragmentCallback {

    private MovieDetailFragment movieDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        setFragmentNegativeMarginTop();

        if (savedInstanceState == null) {
            this.movieDetailFragment = new MovieDetailFragment();
            this.movieDetailFragment.setArguments(getIntent().getExtras());
            addFragment(R.id.fragmentContainer, this.movieDetailFragment);
        }
    }

    private void setFragmentNegativeMarginTop() {
        //Set the status bar height as negative margin top of the fragment container.
        View frameLayout = this.findViewById(R.id.fragmentContainer);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) frameLayout.getLayoutParams();
        layoutParams.setMargins(0, -getStatusBarHeight(), 0, 0);
        frameLayout.setLayoutParams(layoutParams);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void addFragment(int containerViewId, Fragment fragment) {
        final FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(containerViewId, fragment);
        fragmentTransaction.commit();
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

    @Override
    public void openMovieDetail(int movieId) {
        Intent i = new Intent(this, MovieDetailActivity.class);
        i.putExtra(INTENT_MOVIE_DETAIL_ID, movieId);
        startActivity(i);
    }

    @Override
    public void openVideoUrl(Uri url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, url);
        startActivity(intent);
    }

    @Override
    public void openReviewUrl(Uri url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, url);
        startActivity(intent);
    }

    @Override
    public void setToolbar(Toolbar toolbar) {
        setToolbarSize(toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setToolbarSize(Toolbar toolbar) {
        CollapsingToolbarLayout.LayoutParams layoutParams = (CollapsingToolbarLayout.LayoutParams) toolbar.getLayoutParams();

        //get default toolbar height android value. ?attr/actionBarSize.
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());

        }
        int statusBarHeight = getStatusBarHeight();

        //set toolbar height: default + status bar height.
        layoutParams.height = actionBarHeight + statusBarHeight;
        toolbar.setLayoutParams(layoutParams);

        //set padding top to adjust views positions inside the toolbar.
        toolbar.setPadding(0, statusBarHeight, 0, 0);
    }
}
