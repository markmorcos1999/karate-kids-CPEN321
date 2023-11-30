package com.karatekids.wikipediarace.multiplayertests;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotClickable;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.web.assertion.WebViewAssertions.webMatches;
import static androidx.test.espresso.web.sugar.Web.onWebView;
import static androidx.test.espresso.web.webdriver.DriverAtoms.findElement;
import static androidx.test.espresso.web.webdriver.DriverAtoms.getText;
import static androidx.test.espresso.web.webdriver.DriverAtoms.webClick;
import static androidx.test.espresso.web.webdriver.DriverAtoms.webScrollIntoView;
import static org.hamcrest.Matchers.containsString;

import android.content.Intent;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.web.webdriver.Locator;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.karatekids.wikipediarace.InGameActivity;
import com.karatekids.wikipediarace.PlayGameActivity;
import com.karatekids.wikipediarace.R;
import com.karatekids.wikipediarace.SignInActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

//run with two emulators simultaneously
@RunWith(AndroidJUnit4.class)
public class InMultiPlayerGameTest {
    @Rule
    public IntentsTestRule<InGameActivity> activityRule =
            new IntentsTestRule<>(InGameActivity.class, false, false);

    //ChatGPT usage: No
    @Test
    public void inMultiPlayerGameTestLeaveGame() throws InterruptedException {
        Intent test = new Intent();
        test
                .putExtra("start_page", "Taco")
                .putExtra("end_page", "Mexico")
                .putExtra("start_url", "https://en.wikipedia.org/wiki/Taco")
                .putExtra("end_url", "https://en.wikipedia.org/wiki/Mexico");
        activityRule.launchActivity(test);

        // check that the destination page is displayed and not clickable
        onView(withText("Destination Page: Mexico"))
                .check(matches(isDisplayed()))
                .check(matches(isNotClickable()));

        // check that Taco is displayed as the first heading
        onWebView()
                .withElement(findElement(Locator.ID, "firstHeading"))
                .check(webMatches(getText(), containsString("Taco")));

        //click on the Tortilla hyperlink
        onWebView()
                .withElement(findElement(Locator.CSS_SELECTOR, "a[href=\"/wiki/Tortilla\"]")) // similar to onView(withId(...))
                .perform(webClick());

        //check that it is the Tortilla wiki page
        onWebView()
                .withElement(findElement(Locator.ID, "firstHeading"))
                .check(webMatches(getText(), containsString("Tortilla")));

        // click on teh Mesoamerica hyperlink
        onWebView()
                .withElement(findElement(Locator.CSS_SELECTOR, "a[href=\"/wiki/Mesoamerica\"]")) // similar to onView(withId(...))
                .perform(webClick());

        //check that it is the Mesoamerica wiki page
        onWebView()
                .withElement(findElement(Locator.ID, "firstHeading"))
                .check(webMatches(getText(), containsString("Mesoamerica")));

        //click on the external links element and show that it does not navigate away from wikipedia on external links
        onWebView()
                .withElement(findElement(Locator.ID, "External_links"))
                .perform(webScrollIntoView())
                .perform(webClick())
                .withElement(findElement(Locator.CSS_SELECTOR, "a[href=\"https://web.archive.org/web/20101205233059/http://authenticmaya.com/maya_culture.htm\"]"))
                .perform(webScrollIntoView())
                .perform(webClick())
                .withElement(findElement(Locator.ID, "firstHeading"))
                .check(webMatches(getText(), containsString("Mesoamerica")));

        //check the time is displayed correctly
        while (true) {
            try {
                onView(withText("00:10")).perform().check(matches(isDisplayed()));
                break;
            } catch (Exception e) {
            }
        }
        while (true) {
            try {
                onView(withText("00:20")).perform().check(matches(isDisplayed()));
                break;
            } catch (Exception e) {
            }
        }

        //check back button functionality
        onView(withId(R.id.wikipedia_page_view))
                .perform(pressBack());
        onWebView()
                .withElement(findElement(Locator.ID, "firstHeading"))
                .check(webMatches(getText(), containsString("Tortilla")));

        onView(withId(R.id.wikipedia_page_view))
                .perform(pressBack());
        onWebView()
                .withElement(findElement(Locator.ID, "firstHeading"))
                .check(webMatches(getText(), containsString("Taco")));

        //exit the game and go back to play game activity
        onView(withId(R.id.wikipedia_page_view))
                .perform(pressBack());
        intended(hasComponent(PlayGameActivity.class.getName()));
    }

    @Rule
    public ActivityScenarioRule<SignInActivity> signInActivityRule =
            new ActivityScenarioRule<>(SignInActivity.class);

    //ChatGPT usage: No
    //run with two emulators
    @Test
    public void inMultiPlayerGameTestQuit() throws InterruptedException {
        //sign in as a guest and check that the button is displayed and clickable
        onView(withText("Sign in as a Guest")).check(matches(isDisplayed()));
        onView(withText("Sign in as a Guest")).perform(click());

        Thread.sleep(3000);

        //check that the play a game button is displayed and clickable
        onView(withText("Play a Game")).check(matches(isDisplayed()));
        onView(withText("Play a Game")).perform(click());

        //check that the join a multi player game is displayed and clickable
        onView(withText("Join a Multi Player Game")).check(matches(isDisplayed()));
        onView(withText("Join a Multi Player Game")).perform(click());

        //check loading animation is visible
        onView(withId(R.id.loading_pb))
                .check(matches(isDisplayed()))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        //wait until the destination page string is displayed (wait until the in game activity begins)
        while (true) {
            try {
                onView(withSubstring("Destination Page: ")).perform().check(matches(isDisplayed()));
                break;
            } catch (Exception e) {
            }
        }

        //check that the time is displayed
        while (true) {
            try {
                onView(withText("00:07")).perform().check(matches(isDisplayed()));
                break;
            } catch (Exception e) {
            }
        }

        //check that the quit game button exists and is clickable
        onView(withText("Quit Game"))
                .check(matches(isDisplayed()))
                .check(matches(isClickable()));
        onView(withText("Quit Game")).perform(click());

        //check that we move to the results activity and the return to main page button is displayed
        onView(withText("Return to Main Page")).check(matches(isDisplayed()));
    }
}
