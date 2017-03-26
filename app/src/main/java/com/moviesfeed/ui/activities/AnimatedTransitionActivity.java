package com.moviesfeed.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.moviesfeed.R;

/**
 * Created by Pedro on 2017-02-06.
 */

public class AnimatedTransitionActivity extends AppCompatActivity {

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
