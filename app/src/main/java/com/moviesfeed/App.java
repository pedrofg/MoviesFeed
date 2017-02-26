package com.moviesfeed;

import android.app.Application;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

/**
 * Created by Pedro on 13/08/2016.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        loadPicasso();
    }


    private void loadPicasso() {
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this));
        Picasso built = builder.build();
//        built.setIndicatorsEnabled(true);
//        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }

}
