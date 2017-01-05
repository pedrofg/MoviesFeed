package com.moviesfeed.api;

/**
 * Created by Pedro on 8/17/2016.
 */
public enum FiltersType {
    ASC(".asc"), DESC(".desc");

    private final String filterType;

    private FiltersType(String filter) {
        this.filterType = filter;
    }

    public String toString() {
        return this.filterType;
    }
}
