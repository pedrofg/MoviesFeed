package com.moviesfeed;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.widget.AutoCompleteTextView;

import com.moviesfeed.ui.activities.FeedActivity;
import com.moviesfeed.ui.activities.MovieDetailActivity;
import com.moviesfeed.ui.activities.SettingsActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class FeedActivityTest {

    //    @Rule
    //    public ActivityTestRule<FeedActivity> mActivityRule = new ActivityTestRule<>(
    //            FeedActivity.class);

    @Rule
    public IntentsTestRule<FeedActivity> mActivityRule = new IntentsTestRule<>(
            FeedActivity.class);

    /*
        Open drawer.
        Click on settings button.
        Verify Settings Activity started.
     */
    @Test
    public void openSettingsActivity() {
        openDrawer();

        onView(withId(R.id.settingsIcon)).perform(click());
        intended(hasComponent(SettingsActivity.class.getName()));
    }

    /*
        Check recycler view is visible.
        Click on recycler view item.
        Verify Movie Details Activity started.
     */
    @Test
    public void openDetailsActivity() {
        onView(withId(R.id.rvMoviesFeed)).check(matches(isDisplayed()));
        onView(withId(R.id.rvMoviesFeed)).perform(RecyclerViewActions.actionOnItemAtPosition(3, click()));
        intended(hasComponent(MovieDetailActivity.class.getName()));
    }

    @Test
    public void searchMovie() throws InterruptedException {
        onView(withId(R.id.action_search)).perform(click());

        onView(isAssignableFrom(AutoCompleteTextView.class)).perform(typeText("batman"));
        onView(isAssignableFrom(AutoCompleteTextView.class))
                .perform(pressImeActionButton());

        onView(withId(R.id.progressBar)).check(matches(isDisplayed()));

        //Wait for the download finish and load the screen.
        Thread.sleep(2000);

        onView(withId(R.id.rvMoviesFeed)).check(matches(isDisplayed()));
        onView(withId(R.id.layoutError)).check(matches(not(isDisplayed())));
    }


    /*
        Open drawer -> Check if it is displayed
        Click on drawer sub menu,
        Verify toolbar title.
        Check if recycler view is visible.
        Check if layout error is not visible.
     */
    @Test
    public void selectNewCategory() {
        openDrawer();

        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_top_rated));


        String nowPlayingText = InstrumentationRegistry.getTargetContext()
                .getString(R.string.top_rated);

        matchToolbarTitle(nowPlayingText).check(matches(isDisplayed()));

        onView(withId(R.id.rvMoviesFeed)).check(matches(isDisplayed()));
        onView(withId(R.id.layoutError)).check(matches(not(isDisplayed())));
    }

    //Open drawer and check if it is displayed.
    private void openDrawer() {
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(open());

        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
    }

    //Check toolbar title from: https://github.com/chiuki/espresso-samples/tree/master/toolbar-title
    private static ViewInteraction matchToolbarTitle(
            CharSequence title) {
        return onView(isAssignableFrom(Toolbar.class))
                .check(matches(withToolbarTitle(is(title))));
    }

    private static Matcher<Object> withToolbarTitle(
            final Matcher<CharSequence> textMatcher) {
        return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
            @Override
            public boolean matchesSafely(Toolbar toolbar) {
                return textMatcher.matches(toolbar.getTitle());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with toolbar title: ");
                textMatcher.describeTo(description);
            }
        };
    }
}