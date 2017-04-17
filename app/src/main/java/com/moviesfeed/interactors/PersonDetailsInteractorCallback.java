package com.moviesfeed.interactors;

import com.moviesfeed.models.persondetails.Person;

/**
 * Created by Pedro on 2017-03-23.
 */

public interface PersonDetailsInteractorCallback {

    void onLoadSuccess(Person person);

    void onLoadError(Throwable throwable, boolean isNetworkError);
}
