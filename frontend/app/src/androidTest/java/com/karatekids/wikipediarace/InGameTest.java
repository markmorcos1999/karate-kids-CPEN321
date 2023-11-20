package com.karatekids.wikipediarace;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.web.assertion.WebViewAssertions.webMatches;
import static androidx.test.espresso.web.model.Atoms.getCurrentUrl;
import static androidx.test.espresso.web.sugar.Web.onWebView;
import static androidx.test.espresso.web.webdriver.DriverAtoms.findElement;
import static androidx.test.espresso.web.webdriver.DriverAtoms.webClick;

import static org.hamcrest.Matchers.containsString;

import android.content.Intent;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.web.webdriver.Locator;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class InGameTest {
    @Rule
    public IntentsTestRule<InGameActivity> activityRule =
            new IntentsTestRule<>(InGameActivity.class, false, false);

    @Test
    public void testPage() throws InterruptedException {
        Intent test = new Intent();
        test
            .putExtra("start_page", "Taco")
            .putExtra("end_page", "Mexico")
            .putExtra("start_url", "https://en.wikipedia.org/wiki/Taco")
            .putExtra("end_url", "https://en.wikipedia.org/wiki/Mexico");
        Date beforeActivity = new Date();
        activityRule.launchActivity(test);

        onView(withText("Destination Page: Mexico")).check(matches(isDisplayed()));
        onWebView()
                .check(webMatches(getCurrentUrl(), containsString("Taco")));

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


        //requires changes to simulator (in developer options)
//        onView(withId(R.id.wikipedia_page_view))
//                .perform(scrollTo())
//                .check(matches(isDisplayed()));
    }
}
