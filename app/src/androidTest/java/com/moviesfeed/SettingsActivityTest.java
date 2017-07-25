package com.moviesfeed;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.moviesfeed.ui.activities.SettingsActivity;
import com.moviesfeed.ui.activities.ThirdPartyAPIsActivity;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Pedro on 2017-07-21.
 */
//Using name ascending to execute the test "tell a friend" last since it was buggind the previous tests because the ACTION_SEND
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
@LargeTest
public class SettingsActivityTest {

    @Rule
    public IntentsTestRule<SettingsActivity> mActivityRule = new IntentsTestRule<>(
            SettingsActivity.class);

    @Test
    public void testAGiveUsFeedback() throws InterruptedException {
        onView(withId(R.id.btnRateThisApp)).perform(click());
        intended(hasAction(Intent.ACTION_VIEW));
    }

    @Test
    public void testBThirdyPartyApis() {
        onView(withId(R.id.btnThirdApis)).perform(click());
        intended(hasComponent(ThirdPartyAPIsActivity.class.getName()));
    }

    @Test
    public void testCTellAFriend() {
        onView(withId(R.id.btnTellAFriend)).perform(click());
        intended(hasAction(Intent.ACTION_SEND));
    }


}
