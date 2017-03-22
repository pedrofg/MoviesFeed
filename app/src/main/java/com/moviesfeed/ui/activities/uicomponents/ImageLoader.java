package com.moviesfeed.ui.activities.uicomponents;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.request.RequestListener;

/**
 * Created by Pedro on 2017-02-07.
 */

public abstract class ImageLoader {

    public static void loadImageGlide(final Context context, final String url, final ImageView imageView,
                                      final Transformation transformation, final int idDrawableError,
                                      final boolean crop, final boolean animate, final RequestListener callback) {


        DrawableTypeRequest<String> request = Glide.with(context).load(url);

        if (crop) {
            request.centerCrop();
        }

        if (transformation != null) {
            request.bitmapTransform(transformation);
        }

        if (idDrawableError != 0) {
            request.error(idDrawableError);
        }

        if (!animate)
            request.dontAnimate();

        if (callback != null)
            request.listener(callback);

        request.into(imageView);

    }
}

