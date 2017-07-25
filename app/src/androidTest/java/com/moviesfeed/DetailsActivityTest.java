package com.moviesfeed;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.moviesfeed.ui.activities.FeedActivity;
import com.moviesfeed.ui.activities.MovieDetailActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by Pedro on 2017-07-21.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class DetailsActivityTest {

    @Rule
    public IntentsTestRule<FeedActivity> mActivityRule = new IntentsTestRule<>(
            FeedActivity.class);

    @Before
    public void openDetailsActivity() {
        onView(withId(R.id.rvMoviesFeed)).check(matches(isDisplayed()));
        onView(withId(R.id.rvMoviesFeed)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        intended(hasComponent(MovieDetailActivity.class.getName()));
    }

    @Test
    public void validateMovieDetails() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.layoutError)).check(matches(not(isDisplayed())));
        onView(withId(R.id.layoutMovieRating)).check(matches(isDisplayed()));
        onView(withId(R.id.txtMovieOverview)).check(matches(isDisplayed()));
        onView(withId(R.id.cardViewVideos)).check(matches(isDisplayed()));
        onView(withId(R.id.viewRoot)).perform(ViewActions.swipeUp());
        Thread.sleep(500);
        onView(withId(R.id.cardViewCastCrew)).check(matches(isDisplayed()));
        onView(withId(R.id.cardViewSimilarMovies)).check(matches(isDisplayed()));
    }
}
