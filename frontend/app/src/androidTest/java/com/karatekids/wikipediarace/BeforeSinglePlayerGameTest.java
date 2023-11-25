package com.karatekids.wikipediarace;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.util.Log;
import android.view.View;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class BeforeSinglePlayerGameTest {
    @Rule
    public ActivityScenarioRule<SignInActivity> activityRule =
            new ActivityScenarioRule<>(SignInActivity.class);

    //https://stackoverflow.com/questions/28390574/checking-toast-message-in-android-espresso/33387980#33387980
    public String isToastMessageDisplayed(String text) {
        Matcher<View> textView = withSubstring(text);
        onView(textView).inRoot(MobileViewMatchers.isToast()).check(matches(isDisplayed()));
        return textView.toString();
    }

    @Test
    public void beforeSinglePlayerGameTest() throws InterruptedException {

        //doesn't need espresso
        onView(withText("Sign in as a Guest")).check(matches(isDisplayed()));
        onView(withText("Sign in as a Guest")).perform(click());

        Thread.sleep(1000);

        //check play game button is visible
        onView(withText("Play a Game")).check(matches(isDisplayed()));
        onView(withText("Play a Game")).perform(click());

        //check single player game button is visible
        onView(withText("Join a Single Player Game")).check(matches(isDisplayed()));
        onView(withText("Join a Single Player Game")).perform(click());

        //check loading animation is visible
        onView(withId(R.id.loading_pb))
                .check(matches(isDisplayed()))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        //check single player game button is visible
        onView(withText("Finding Start and End Pages...")).check(matches(isDisplayed()));
//        onView(withText("Finding Start and End Pages...")).perform(click());

        StringBuilder str;
        while (true) {
            try {
                str = new StringBuilder(isToastMessageDisplayed("Starting Page: "));//).inRoot(withDecorView(not(activityRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Log.e("TEST", str.toString());
        //check loading animation is not visible
        onView(withId(R.id.loading_pb))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        onView(withText("Pages Found!")).perform().check(matches(isDisplayed()));
        //check text is gone
//        onView(withText("Finding Start and End Pages...")).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        while (true) {
            try {
                onView(withSubstring("Destination Page: ")).perform().check(matches(isDisplayed()));
                break;
            } catch (Exception e) {
            }
        }

        //check that wikipedia page is displayed

    }
}