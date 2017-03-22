package com.moviesfeed.ui.presenters;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by Pedro on 1/19/2017.
 */
public class Util {

    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
