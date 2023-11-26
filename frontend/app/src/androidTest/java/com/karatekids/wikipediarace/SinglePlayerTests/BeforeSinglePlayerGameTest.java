package com.karatekids.wikipediarace.SinglePlayerTests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.util.Log;
import android.view.View;

import androidx.test.espresso.intent.Intents;
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
        Intents.init();
        onView(withText("Sign in as a Guest")).check(matches(isDisplayed()));
        onView(withText("Sign in as a Guest")).perform(click());

        Thread.sleep(3000);

        onView(withText("Play a Game")).check(matches(isDisplayed()));
        onView(withText("Play a Game")).perform(click());

        onView(withText("Join a Single Player Game")).check(matches(isDisplayed()));
        onView(withText("Join a Single Player Game")).perform(click());

        //check loading animation is visible
        onView(ViewMatchers.withId(R.id.loading_pb))
                .check(matches(isDisplayed()))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withText("Finding Start and End Pages...")).check(matches(isDisplayed()));

        while (true) {
            try {
                isToastMessageDisplayed("Starting Page: ");
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        onView(withId(R.id.loading_pb))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        onView(withText("Pages Found!")).perform().check(matches(isDisplayed()));

        while (true) {
            try {
                onView(withSubstring("Destination Page: ")).perform().check(matches(isDisplayed()));
                break;
            } catch (Exception e) {
            }
        }
        intended(hasComponent(InGameActivity.class.getName()));
    }
}