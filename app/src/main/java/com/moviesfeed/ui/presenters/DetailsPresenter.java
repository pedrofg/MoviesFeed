package com.moviesfeed.ui.presenters;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.moviesfeed.R;
import com.moviesfeed.ui.components.AppBarStateChangeListener;

/**
 * Created by Pedro on 2017-04-17.
 */

public abstract class DetailsPresenter implements Presenter {

    public interface DetailsPresenterCallback {

        void updateToolbarTitle(String title);

        String getToolbarTitle();

        int getToolbarHeight();

        void updateToolbarBackground(int alpha);

        Context context();

        void animImagePoster(ImageView imageView, int height, ImageView.ScaleType scaleType);

        void setScrollsEnable(boolean blocked);
    }


    private DetailsPresenterCallback callback;
    private static int MAX_ALPHA = 255;

    public void setCallback(DetailsPresenterCallback callback) {
        this.callback = callback;
    }

    void appBarLayoutStateChanged(AppBarLayout appBarLayout, AppBarStateChangeListener.State state, int verticalOffset, String title) {
        if (state == AppBarStateChangeListener.State.COLLAPSED)
            callback.updateToolbarTitle(title);
        else if (!TextUtils.isEmpty(callback.getToolbarTitle())) {
            callback.updateToolbarTitle("");
        }
        //measuring for alpha
        int toolBarHeight = callback.getToolbarHeight();
        int appBarHeight = appBarLayout.getMeasuredHeight();
        Float f = ((((float) appBarHeight - toolBarHeight) + verticalOffset) / ((float) appBarHeight - toolBarHeight)) * MAX_ALPHA;
        callback.updateToolbarBackground(MAX_ALPHA - Math.round(f));
    }

    public void onImagePosterClicked(ImageView imageView) {

        boolean isZoomed;
        int imgDefaultHeight = (int) callback.context().getResources().getDimension(R.dimen.movie_detail_img_backdrop_height);

        isZoomed = imageView.getScaleType() == ImageView.ScaleType.CENTER_CROP;

        int height = isZoomed ? imgDefaultHeight
                : ViewGroup.LayoutParams.MATCH_PARENT;

        ImageView.ScaleType scaleType = isZoomed ? ImageView.ScaleType.FIT_XY
                : ImageView.ScaleType.CENTER_CROP;


        callback.animImagePoster(imageView, height, scaleType);

        callback.setScrollsEnable(isZoomed);
    }

    @Override
    abstract public void resume();

    @Override
    abstract public void pause();

    @Override
    abstract public void destroy();
}
