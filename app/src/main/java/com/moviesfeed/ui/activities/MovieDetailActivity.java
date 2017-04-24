package com.moviesfeed.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.moviesfeed.R;
import com.moviesfeed.ui.fragments.MovieDetailFragment;

import static com.moviesfeed.ui.activities.FeedActivity.INTENT_MOVIE_DETAIL_ID;
import static com.moviesfeed.ui.activities.PersonDetailsActivity.INTENT_PERSON_DETAILS_ID;


public class MovieDetailActivity extends DetailsActivity implements MovieDetailFragment.MovieDetailFragmentCallback {

    private MovieDetailFragment movieDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        if (savedInstanceState == null) {
            this.movieDetailFragment = new MovieDetailFragment();
            this.movieDetailFragment.setArguments(getIntent().getExtras());
            addFragment(R.id.fragmentContainer, this.movieDetailFragment);
        }
    }

    @Override
    public void setNegativeMarginToolbar() {
        setFragmentNegativeMarginTop(R.id.fragmentContainer);
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
    public void openMovieHomepage(Uri url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, url);
        startActivity(intent);
    }

    @Override
    public void openPersonDetails(int personID) {
        Intent i = new Intent(this, PersonDetailsActivity.class);
        i.putExtra(INTENT_PERSON_DETAILS_ID, personID);
        startActivity(i);
    }

    @Override
    public void setToolbar(Toolbar toolbar) {
        setToolbarSize(toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }
}
