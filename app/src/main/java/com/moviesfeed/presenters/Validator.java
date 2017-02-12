package com.moviesfeed.presenters;

import android.text.TextUtils;

import com.moviesfeed.models.Movie;
import com.moviesfeed.models.Video;

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
}
