package com.moviesfeed.ui.fragments;

import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.moviesfeed.ui.components.CustomLinearLayoutManager;


/**
 * Created by Pedro on 2017-04-17.
 */
public class DetailsFragment extends Fragment {

    public void animImagePoster(ViewGroup viewRoot, ImageView imageView, AppBarLayout appBarLayout, int height, ImageView.ScaleType scaleType) {
        TransitionManager.beginDelayedTransition(viewRoot, new TransitionSet()
                .addTransition(new ChangeBounds())
                .addTransition(new ChangeImageTransform()));

        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        CoordinatorLayout.LayoutParams paramsContainer = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();


        params.height = height;
        paramsContainer.height = height;

        imageView.setLayoutParams(params);
        imageView.setScaleType(scaleType);
    }

    public void setScrollsEnable(boolean enable, AppBarLayout appBarLayout, CustomLinearLayoutManager customLinearLayoutManager) {
        CoordinatorLayout.LayoutParams paramsContainer = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();

        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) paramsContainer.getBehavior();
        if (behavior != null) {
            behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                @Override
                public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                    return enable;
                }
            });
        }

        customLinearLayoutManager.setScrollEnabled(enable);
    }

    public void updateToolbarTitle(Toolbar toolbar, String title) {
        toolbar.setTitle(title);
    }

    public String getToolbarTitle(Toolbar toolbar) {
        return toolbar.getTitle().toString();
    }

    public int getToolbarHeight(Toolbar toolbar) {
        return toolbar.getMeasuredHeight();
    }

    public void updateToolbarBackground(Toolbar toolbar, int alpha) {
        toolbar.getBackground().mutate().setAlpha(alpha);
    }
}
