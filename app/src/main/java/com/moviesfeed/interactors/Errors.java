package com.moviesfeed.interactors;

/**
 * Created by Pedro on 2017-04-08.
 */

public enum Errors {
    NETWORK(0), TOO_MANY_REQUEST(1), LIMIT_MOVIES_REACHED(3), GENERIC(4);

    private final long id;

    Errors(int id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
