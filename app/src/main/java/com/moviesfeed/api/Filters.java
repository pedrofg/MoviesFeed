package com.moviesfeed.api;

/**
 * Created by Pedro on 8/17/2016.
 */
public enum Filters {
    POPULARITY("popularity"), REVENUE("revenue"), RELEASE_DATE("release_date"),
    TOP_RATED("top_rated"), UPCOMING("upcoming");

    private final String filter;

    private Filters(String filter) {
        this.filter = filter;
    }

    public String toString() {
        return this.filter;
    }

}
