package com.moviesfeed;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by Pedro on 2017-02-26.
 */

@RunWith(Suite.class)

@Suite.SuiteClasses({
        FeedPresenterTest.class,
        MovieDetailPresenterTest.class
})
public class MoviesFeedTestSuite {

}
