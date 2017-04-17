package com.moviesfeed.ui.presenters;

import android.text.TextUtils;

import com.moviesfeed.models.Cast;
import com.moviesfeed.models.Crew;
import com.moviesfeed.models.Movie;
import com.moviesfeed.models.Video;
import com.moviesfeed.models.persondetails.PersonCast;
import com.moviesfeed.models.persondetails.PersonCrew;

/**
 * Created by Pedro on 2017-02-11.
 */

public abstract class Validator {

    public static boolean validateMovie(Movie movie) {
        if (TextUtils.isEmpty(movie.getOverview()) ||
                TextUtils.isEmpty(movie.getTitle()) ||
                TextUtils.isEmpty(movie.getPosterPath()) ||
                TextUtils.isEmpty(movie.getBackdropPath()) ||
                movie.getVoteAverage() == 0) {
            return false;
        }
        return true;
    }

    public static boolean validateVideo(Video video) {
        if (TextUtils.isEmpty(video.getKey()) ||
                TextUtils.isEmpty(video.getSite()) ||
                !video.getSite().equals(Video.YOUTUBE_SITE)) {
            return false;
        }
        return true;
    }

    public static boolean validateCast(Cast cast) {
        if (TextUtils.isEmpty(cast.getProfilePath()) ||
                TextUtils.isEmpty(cast.getName()) ||
                TextUtils.isEmpty(cast.getCharacter())) {
            return false;
        }
        return true;
    }

    public static boolean validateCrew(Crew crew) {
        if (TextUtils.isEmpty(crew.getProfilePath()) ||
                TextUtils.isEmpty(crew.getName()) ||
                TextUtils.isEmpty(crew.getJob())) {
            return false;
        }
        return true;
    }

    public static boolean validatePersonCast(PersonCast personCast) {
        if (TextUtils.isEmpty(personCast.getPosterPath()) ||
                TextUtils.isEmpty(personCast.getCharacter()) ||
                TextUtils.isEmpty(personCast.getOriginalTitle())) {
            return false;
        }
        return true;
    }

    public static boolean validatePersonCrew(PersonCrew personCrew) {
        if (TextUtils.isEmpty(personCrew.getPosterPath()) ||
                TextUtils.isEmpty(personCrew.getJob()) ||
                TextUtils.isEmpty(personCrew.getOriginalTitle())) {
            return false;
        }
        return true;
    }
}
