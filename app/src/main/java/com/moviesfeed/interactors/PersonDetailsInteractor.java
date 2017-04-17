package com.moviesfeed.interactors;

import android.content.Context;
import android.util.Log;

import com.moviesfeed.di.DaggerSchedulersComponent;
import com.moviesfeed.di.SchedulersModule;
import com.moviesfeed.models.persondetails.Person;
import com.moviesfeed.models.persondetails.PersonCast;
import com.moviesfeed.models.persondetails.PersonCrew;
import com.moviesfeed.models.persondetails.PersonImage;
import com.moviesfeed.models.persondetails.PersonImages;
import com.moviesfeed.repository.PersonDetailsRepository;
import com.moviesfeed.ui.presenters.Util;
import com.moviesfeed.ui.presenters.Validator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

import static com.moviesfeed.repository.PersonDetailsRepository.PERSON_NOT_FOUND;

/**
 * Created by Pedro on 2017-03-23.
 */

public class PersonDetailsInteractor {

    @Inject
    @Named(SchedulersModule.IO_SCHEDULER)
    public Scheduler ioScheduler;
    @Inject
    @Named(SchedulersModule.MAIN_THREAD_SCHEDULER)
    public Scheduler mainThreadScheduler;

    private Context context;
    private PersonDetailsInteractorCallback callback;
    private final CompositeDisposable disposables;
    private int personID;
    private Person personDetailsCache;
    private PersonDetailsRepository personDetailsRepository;


    public PersonDetailsInteractor(Context context, PersonDetailsInteractorCallback callback) {
        DaggerSchedulersComponent.builder()
                .schedulersModule(new SchedulersModule())
                .build().inject(this);

        this.context = context;
        this.callback = callback;
        this.disposables = new CompositeDisposable();
    }

    public void init() {
        this.personDetailsRepository = new PersonDetailsRepository(this.context);
    }

    public void requestPersonDetails(int personID) {
        Log.d(PersonDetailsInteractor.class.getName(), "requestPersonDetails() personID: " + personID);
        this.personID = personID;
        loadPersonDetailsDb();
    }

    public boolean hasCache() {
        return this.personDetailsCache != null;
    }

    public String getPersonName() {
        return this.personDetailsCache.getName();
    }

    private void loadPersonDetailsDb() {
        Observable<Person> observable = Observable.fromCallable(() -> this.personDetailsRepository.loadPersonDetailsDB((long) this.personID))
                .subscribeOn(ioScheduler)
                .observeOn(mainThreadScheduler);

        DisposableObserver<Person> observer = new DisposableObserver<Person>() {
            @Override
            public void onNext(Person person) {
                if (person == PERSON_NOT_FOUND) {
                    loadPersonDetailsApi();
                } else {
                    updateMemoryCache(person, true);
                    callback.onLoadSuccess(personDetailsCache);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onLoadError(throwable, false);
            }

            @Override
            public void onComplete() {
            }
        };

        addDisposable(observable.subscribeWith(observer));
    }

    private void updateMemoryCache(Person person, boolean isFromDb) {
        if (!isFromDb) {

            person.setPersonImagesID(person.getId());
            if (person.getPersonImages() != null) {
                person.getPersonImages().setId(person.getId());
            }

            person.setPersonMovieCreditsID(person.getId());
            if (person.getPersonMovieCredits() != null) {
                person.getPersonMovieCredits().setId(person.getId());
            }

            if (person.getPersonImages() != null && person.getPersonImages().getPersonImageList() != null) {
                for (PersonImage personImage : person.getPersonImages().getPersonImageList()) {
                    personImage.setPersonImagesId(person.getPersonImagesID());
                }
            } else {
                //add the image the comes in person.
                PersonImages personImages = new PersonImages();
                PersonImage image = new PersonImage();
                image.setPersonImagesId(person.getPersonImagesID());
                image.setFilePath(person.getImageProfilePath());
                person.setPersonImages(personImages);
            }

            if (person.getPersonMovieCredits() != null) {

                List<PersonCast> personCastList = person.getPersonMovieCredits().getPersonCasts();
                if (personCastList != null) {
                    List<PersonCast> filteredCastList = new ArrayList<>();
                    for (PersonCast personCast : personCastList) {
                        if (Validator.validatePersonCast(personCast)) {
                            personCast.setPersonCreditsKey(person.getPersonMovieCreditsID());
                            filteredCastList.add(personCast);
                        }
                    }
                    person.getPersonMovieCredits().setPersonCasts(filteredCastList);
                }


                List<PersonCrew> personCrewList = person.getPersonMovieCredits().getPersonCrews();
                if (personCrewList != null) {
                    List<PersonCrew> filteredCrewList = new ArrayList<>();
                    for (PersonCrew personCrew : personCrewList) {
                        if (Validator.validatePersonCrew(personCrew)) {
                            personCrew.setPersonCreditsKey(person.getPersonMovieCreditsID());
                            filteredCrewList.add(personCrew);
                        }
                    }
                    person.getPersonMovieCredits().setPersonCrews(filteredCrewList);
                }
            }
        }

        this.personDetailsCache = person;
    }

    private void loadPersonDetailsApi() {

        Observable<Person> observable = personDetailsRepository.loadPersonDetailsApi(this.personID)
                .subscribeOn(ioScheduler).observeOn(mainThreadScheduler);

        DisposableObserver<Person> observer = new DisposableObserver<Person>() {
            @Override
            public void onNext(Person person) {
                updateMemoryCache(person, false);
                callback.onLoadSuccess(personDetailsCache);
                updateDiskCache(personDetailsCache);
            }

            @Override
            public void onError(Throwable throwable) {

                boolean isNetworkError = false;

                if (throwable instanceof IOException && !Util.isNetworkAvailable(context))
                    isNetworkError = true;

                callback.onLoadError(throwable, isNetworkError);
            }

            @Override
            public void onComplete() {
            }
        };

        addDisposable(observable.subscribeWith(observer));
    }

    private void updateDiskCache(Person person) {
        Observable<Person> observable = Observable.fromCallable(() -> {
            personDetailsRepository.updatePersonDetailsDB(person);
            return PERSON_NOT_FOUND;
        }).subscribeOn(ioScheduler)
                .observeOn(mainThreadScheduler);


        addDisposable(observable.subscribe());
    }

    public void tryAgain() {
        loadPersonDetailsApi();
    }

    private void addDisposable(Disposable disposable) {
        disposables.add(disposable);
    }

    public void dispose() {
        if (!disposables.isDisposed()) {
            disposables.dispose();
        }
    }
}
