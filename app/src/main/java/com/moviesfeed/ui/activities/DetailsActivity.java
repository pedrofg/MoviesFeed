package com.moviesfeed.ui.activities;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by Pedro on 2017-04-17.
 */

public abstract class DetailsActivity extends AnimatedTransitionActivity {

    void setFragmentNegativeMarginTop(int frameLayoutId) {
        //Set the status bar height as negative margin top of the fragment container.
        View frameLayout = this.findViewById(frameLayoutId);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) frameLayout.getLayoutParams();
        layoutParams.setMargins(0, -getStatusBarHeight(), 0, 0);
        frameLayout.setLayoutParams(layoutParams);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    void setToolbarSize(Toolbar toolbar) {
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
