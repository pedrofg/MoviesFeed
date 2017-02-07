package com.moviesfeed.activities;

import android.os.Bundle;

import com.moviesfeed.R;

import nucleus.presenter.Presenter;
import nucleus.view.NucleusAppCompatActivity;

/**
 * Created by Pedro on 2017-02-06.
 */

public class AnimatedActivity<P extends Presenter> extends NucleusAppCompatActivity<P> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.slide_in_activity, R.anim.feed_activity_no_anim);
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0, R.anim.slide_out_activity);
        super.onPause();
    }
}
