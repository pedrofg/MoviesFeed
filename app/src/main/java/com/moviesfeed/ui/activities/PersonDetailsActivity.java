package com.moviesfeed.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.moviesfeed.R;
import com.moviesfeed.ui.fragments.PersonDetailsFragment;

import static com.moviesfeed.ui.activities.FeedActivity.INTENT_MOVIE_DETAIL_ID;


public class PersonDetailsActivity extends DetailsActivity implements PersonDetailsFragment.PersonDetailsFragmentCallback {

    public static final String INTENT_PERSON_DETAILS_ID = "INTENT_PERSON_DETAILS_ID";
    private PersonDetailsFragment personFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_details);

        setFragmentNegativeMarginTop(R.id.personFragmentContainer);

        if (savedInstanceState == null) {
            this.personFragment = new PersonDetailsFragment();
            this.personFragment.setArguments(getIntent().getExtras());
            addFragment(R.id.personFragmentContainer, this.personFragment);
        }
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
    public void setToolbar(Toolbar toolbar) {
        setToolbarSize(toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void openPersonHomepage(Uri url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, url);
        startActivity(intent);
    }
}
