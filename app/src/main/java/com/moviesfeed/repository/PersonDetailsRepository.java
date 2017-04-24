package com.moviesfeed.repository;

import android.content.Context;
import android.util.Log;

import com.moviesfeed.api.MoviesApi;
import com.moviesfeed.di.ApiModule;
import com.moviesfeed.di.DaggerAppComponent;
import com.moviesfeed.di.DatabaseModule;
import com.moviesfeed.models.DaoSession;
import com.moviesfeed.models.persondetails.Person;
import com.moviesfeed.ui.presenters.MovieDetailPresenter;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by Pedro on 2017-04-16.
 */

public class PersonDetailsRepository {

    @Inject
    public MoviesApi api;
    @Inject
    public DaoSession daoSession;

    //create to supply rxjava/android 2.0 which null is not a valid return.
    public static final Person PERSON_NOT_FOUND = new Person();


    public PersonDetailsRepository(Context context) {
        DaggerAppComponent.builder()
                .apiModule(new ApiModule())
                .databaseModule(new DatabaseModule(context))
                .build().inject(this);
    }

    public Observable<Person> loadPersonDetailsApi(int personID) {
        return this.api.getPersonDetails(personID);

    }

    public void updatePersonDetailsDB(Person person) {
        if (verifyDb()) {
            Log.d(PersonDetailsRepository.class.getName(), "daoSession != null");
            daoSession.getPersonCreditsScreenDao().insertOrReplaceInTx(person.getPersonMovieCredits().getPersonCreditsScreenList());
            daoSession.getPersonMovieCreditsDao().insertOrReplace(person.getPersonMovieCredits());
            daoSession.getPersonImageDao().insertOrReplaceInTx(person.getPersonImages().getPersonImageList());
            daoSession.getPersonImagesDao().insertOrReplaceInTx(person.getPersonImages());
            daoSession.getPersonDao().insertOrReplaceInTx(person);
        } else {
            Log.d(PersonDetailsRepository.class.getName(), "daoSession null");
        }
    }

    public Person loadPersonDetailsDB(Long personID) {
        if (verifyDb()) {
            Person person = daoSession.getPersonDao().loadDeep(personID);
            //Doing that the GreenDao gets() will load the objects while still in another thread.
            if (person != null) {
                Log.d(MovieDetailPresenter.class.getName(), "loadPersonDetailsDB() personID: " + personID);
                person.getPersonImages().getPersonImageList();
                person.getPersonMovieCredits().getPersonCreditsScreenList();
                return person;
            } else {
                return PERSON_NOT_FOUND;
            }
        }
        return PERSON_NOT_FOUND;
    }

    private boolean verifyDb() {
        boolean isDbValid = daoSession != null &&
                daoSession.getPersonDao() != null &&
                daoSession.getPersonCastDao() != null &&
                daoSession.getPersonCrewDao() != null &&
                daoSession.getPersonImageDao() != null &&
                daoSession.getPersonImagesDao() != null &&
                daoSession.getPersonMovieCreditsDao() != null;

        Log.d(PersonDetailsRepository.class.getName(), "verifyDb() = " + isDbValid);
        return isDbValid;
    }
}
