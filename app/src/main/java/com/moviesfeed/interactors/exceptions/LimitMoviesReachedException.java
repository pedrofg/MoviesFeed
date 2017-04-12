package com.moviesfeed.interactors.exceptions;

/**
 * Created by Pedro on 2017-04-11.
 */

public class LimitMoviesReachedException extends Exception {

    public LimitMoviesReachedException() {
        super();
    }

    public LimitMoviesReachedException(String message) {
        super(message);
    }

    public LimitMoviesReachedException(String message, Throwable cause) {
        super(message, cause);
    }

    public LimitMoviesReachedException(Throwable cause) {
        super(cause);
    }
}
