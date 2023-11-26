package com.karatekids.wikipediarace.MultiPlayerTests;

//ChatGPT usage: No

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.karatekids.wikipediarace.MobileViewMatchers;
import com.karatekids.wikipediarace.R;
import com.karatekids.wikipediarace.SignInActivity;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
// test with two emulators simultaneously
@RunWith(AndroidJUnit4.class)
public class BeforeMultiPlayerGameTest {
    @Rule
    public ActivityScenarioRule<SignInActivity> activityRule =
            new ActivityScenarioRule<>(SignInActivity.class);

    //ChatGPT usage: No
    //https://stackoverflow.com/questions/28390574/checking-toast-message-in-android-espresso/33387980#33387980
    public String isToastMessageDisplayed(String text) {
        Matcher<View> textView = withSubstring(text);
        onView(textView).inRoot(MobileViewMatchers.isToast()).check(matches(isDisplayed()));
        return textView.toString();
    }

    //ChatGPT usage: No
    @Test
    public void beforeMultiPlayerGameTest() throws InterruptedException {
        //verify sign in as guest button
        onView(withText("Sign in as a Guest"))
                .check(matches(isDisplayed()))
                .check(matches(isClickable()));

        //move to main activity
        onView(withText("Sign in as a Guest")).perform(click());

        Thread.sleep(3000);

        //check play game button is visible and clickable
        onView(withText("Play a Game"))
                .check(matches(isDisplayed()))
                .check(matches(isClickable()));

        //move to play game activity
        onView(withText("Play a Game")).perform(click());

        //check join button is displayed and clickable
        onView(withText("Join a Multi Player Game"))
                .check(matches(isDisplayed()))
                .check(matches(isClickable()));

        //move to lobby activity
        onView(withText("Join a Multi Player Game")).perform(click());

        //check loading animation is visible
        onView(ViewMatchers.withId(R.id.loading_pb))
                .check(matches(isDisplayed()))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        //wait for another player to join the game and check that toast is displayed afterwards
        StringBuilder str;
        StringBuilder s;
        while(true) {
            try {
                str = new StringBuilder(isToastMessageDisplayed("Starting Page: "));//).inRoot(withDecorView(not(activityRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
                s = new StringBuilder(isToastMessageDisplayed("Destination Page: "));
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //check the toast contains the player name, starting page and destination page
        assert(str.toString().contains("Starting Page: "));
        assert(s.toString().contains("Destination Page: "));

        //check match found text is visible
        onView(withText("Match Found!"))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        //check loading animation is invisible
        onView(withId(R.id.loading_pb))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        //check that the destination page is displayed
        while (true) {
            try {
                onView(withSubstring("Destination Page: ")).perform().check(matches(isDisplayed()));
                break;
            } catch (Exception e) {
            }
        }
    }
}
