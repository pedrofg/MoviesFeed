package com.moviesfeed.di;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.moviesfeed.App;
import com.moviesfeed.models.DaoMaster;
import com.moviesfeed.models.DaoSession;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Pedro on 2017-02-25.
 */

@Module
public class DatabaseModule {

    private Context context;

    public DatabaseModule(Context context) {
        this.context = context;
    }

    public static final String DB_NAME = "moviesfeed-db";


    @Singleton
    @Provides
    public DaoSession getDaoSession() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        return daoMaster.newSession();
    }
}
