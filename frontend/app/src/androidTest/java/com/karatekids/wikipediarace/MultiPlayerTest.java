package com.karatekids.wikipediarace;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static androidx.test.espresso.web.assertion.WebViewAssertions.webContent;
import static androidx.test.espresso.web.assertion.WebViewAssertions.webMatches;
import static androidx.test.espresso.web.model.Atoms.getCurrentUrl;
import static androidx.test.espresso.web.sugar.Web.onWebView;
import static androidx.test.espresso.web.webdriver.DriverAtoms.findElement;
import static androidx.test.espresso.web.webdriver.DriverAtoms.webClick;

import static org.hamcrest.Matchers.containsString;
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
import androidx.test.espresso.web.webdriver.Locator;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;

import java.util.Date;

// test with two emulators simultaneously
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
    public void verifyMultiPlayerInGame() throws InterruptedException {
        Intent inGameIntent = new Intent();
        inGameIntent.putExtra("start_page","Taco")
                .putExtra("end_page", "Mexico")
                .putExtra("start_url", "https://en.wikipedia.org/wiki/Taco")
                .putExtra("end_url", "https://en.wikipedia.org/wiki/Mexico");
        Date beforeActivity = new Date();
        inGameActivityRule.launchActivity(inGameIntent);

        onView(withText("Destination Page: Mexico")).check(matches(isDisplayed()));

        onWebView()
                .check(webMatches(getCurrentUrl(), containsString("Taco")));

        onWebView()
                .withElement(findElement(Locator.CSS_SELECTOR, "a[href=\"/wiki/British_English\"]")); // similar to onView(withId(...)
//                .check(webContent((Matcher<Document>) webClick()));

        onWebView()
                .withElement(findElement(Locator.CSS_SELECTOR, "a[href=\"/wiki/British_English\"]")) // similar to onView(withId(...))
                .perform(webClick());

        onWebView()
                .check(webMatches(getCurrentUrl(), containsString("British_English")));

        while (true) {
            try {
                onView(withText("00:10")).perform().check(matches(isDisplayed()));
                break;
            } catch (Exception e) {
            }
        }

        onView(withId(R.id.wikipedia_page_view))
                .perform(pressBack());

        intended(hasComponent(PlayGameActivity.class.getName()));

    }

}
