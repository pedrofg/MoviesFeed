package com.moviesfeed.activities.uicomponents;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;

/**
 * Created by Pedro on 2017-02-07.
 */

public abstract class ImageLoader {

    public static void loadImage(final Context context, final String url, final ImageView imageView,
                                 final Transformation transformation, final int widthDimenId, final int heightDimenId,
                                 final int idDrawableError, final boolean crop, final Callback callback) {

        RequestCreator rcCache = getRequestCreator(context, url, idDrawableError, true, transformation, widthDimenId, heightDimenId, crop);

        rcCache.into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }

            @Override
            public void onError() {
                RequestCreator rcDownload = getRequestCreator(context, url, idDrawableError, false, transformation, widthDimenId, heightDimenId, crop);
                rcDownload.into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        callback.onSuccess();
                    }

                    @Override
                    public void onError() {
                        if (idDrawableError != 0) {
                            RequestCreator rcErrorImg = getRequestCreator(context, null, idDrawableError, true, transformation, widthDimenId, heightDimenId, crop);
                            rcErrorImg.into(imageView, callback);
                        } else {
                            callback.onError();
                        }
                    }
                });
            }
        });

    }

    private static RequestCreator getRequestCreator(Context context, String url, int resourceError, boolean useCache, Transformation transformation, int widthDimenId, int heightDimenId, boolean crop) {
        RequestCreator requestCreator;

        if (url != null)
            requestCreator = Picasso.with(context).load(url);
        else
            requestCreator = Picasso.with(context).load(resourceError);

        if (widthDimenId != 0 && heightDimenId != 0) {
            requestCreator.resizeDimen(widthDimenId, heightDimenId);
        }
        if (transformation != null) {
            requestCreator.transform(transformation);
        }
        if (crop) {
            requestCreator.centerCrop();
        }
        if (useCache) {
            requestCreator.networkPolicy(NetworkPolicy.OFFLINE);
        }
        return requestCreator;
    }
}
