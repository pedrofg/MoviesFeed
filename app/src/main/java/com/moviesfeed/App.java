package com.moviesfeed;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.moviesfeed.api.MoviesApi;
import com.moviesfeed.models.DaoMaster;
import com.moviesfeed.models.DaoSession;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Pedro on 13/08/2016.
 */
public class App extends Application {

    private static MoviesApi serverApi;
    private static DaoSession daoSession;
    public static final String DB_NAME = "moviesfeed-db";

    @Override
    public void onCreate() {
        super.onCreate();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MoviesApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        serverApi = retrofit.create(MoviesApi.class);

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, App.DB_NAME);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }

    public static MoviesApi getServerApi() {
        return serverApi;
    }
}
