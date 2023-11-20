package com.karatekids.wikipediarace;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.test.espresso.Root;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MultiPlayerTest {
    @Rule
    public IntentsTestRule<SignInActivity> signInActivityRule =
            new IntentsTestRule<>(SignInActivity.class, false, false);

    @Rule
    public IntentsTestRule<InGameActivity> inGameActivityRule =
            new IntentsTestRule<>(InGameActivity.class, false, false);


    //https://stackoverflow.com/questions/28390574/checking-toast-message-in-android-espresso/33387980#33387980
    public String isToastMessageDisplayed(String text) {
        Matcher<View> textView = withSubstring(text);
        onView(textView).inRoot(MobileViewMatchers.isToast()).check(matches(isDisplayed()));
        return textView.toString();
    }
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.karatekids.wikipediarace", appContext.getPackageName());
    }


    @Test
    public void testJoinMultiPlayerGame() throws InterruptedException {

        //create sign in activity
        Intent signInIntent = new Intent();
        signInActivityRule.launchActivity(signInIntent);

        //verify sign in as guest button
        onView(withText("Sign in as a Guest")).check(matches(isDisplayed()));

        //move to the main activity
        onView(withText("Sign in as a Guest")).perform(click());

        Thread.sleep(3000);

        //check play game button is visible
        onView(withText("Play a Game")).check(matches(isDisplayed()));

        //move to play game activity
        onView(withText("Play a Game")).perform(click());

        //check that the play game button is viewable, is clickable, and has the correct text
        onView(withText("Join a Multi Player Game"))
                .check(matches(withText("Join a Multi Player Game")))
                .check(matches(isClickable()))
                .check(matches(isDisplayed()));

        //move to the lobby activity
        onView(withText("Join a Multi Player Game"))
                .perform(click());

        //check loading animation is visible
        onView(withId(R.id.loading_pb))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        //check match found text is invisible
//        onView(withId(R.id.match_found_text))
//                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

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

        //check start and end pages are different

    }

    @Test
    public void verifyRandomStartAndEndPage() throws InterruptedException {
        Intent intent = new Intent();
        inGameActivityRule.launchActivity(intent);

        Log.d("TEST",intent.getStringExtra("start_page"));
    }

}
