package com.moviesfeed.exceptions;

/**
 * Created by Pedro on 2017-02-26.
 */

public class NullResponseException extends Exception {

    public static final String SERVER_RETURNED_NULL_OBJECT = "Server returned null object";

    public NullResponseException() {
        super(SERVER_RETURNED_NULL_OBJECT);
    }
}
