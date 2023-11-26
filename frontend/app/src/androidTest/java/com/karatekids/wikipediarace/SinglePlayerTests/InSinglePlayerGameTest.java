package com.karatekids.wikipediarace.SinglePlayerTests;

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
import static androidx.test.espresso.web.webdriver.DriverAtoms.getText;
import static androidx.test.espresso.web.webdriver.DriverAtoms.webClick;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.util.Log;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.web.webdriver.Locator;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.karatekids.wikipediarace.InGameActivity;
import com.karatekids.wikipediarace.PlayGameActivity;
import com.karatekids.wikipediarace.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class InSinglePlayerGameTest {
    private static final String TAG = "InSinglePlayerGameTest";
    @Rule
    public IntentsTestRule<InGameActivity> activityRule =
            new IntentsTestRule<>(InGameActivity.class, false, false);

    //https://newsletter.automationhacks.io/p/hello-espresso-part-5-automating
    @Test
    public void inSinglePlayerGameDelayTest() {
        //https://wikiroulette.co/?p=Shane_Morrison
        String [] [] pagesToVisit = {
            {"Rekha (1943 film)", "https://en.wikipedia.org/wiki/Rekha_(1943_film)"},
            {"John Morrison Forbes", "https://en.wikipedia.org/wiki/John_Morrison_Forbes"},
            {"States of Jersey Customs and Immigration Service", "https://en.wikipedia.org/wiki/States_of_Jersey_Customs_and_Immigration_Service"},
            {"Yo Soy (Peruvian TV series)", "https://en.wikipedia.org/wiki/Yo_Soy_(Peruvian_TV_series)"},
            {"Bulgarian National-Patriotic Party", "https://en.wikipedia.org/wiki/Bulgarian_National-Patriotic_Party"},
            {"Backshore", "https://en.wikipedia.org/wiki/Backshore"},
            {"Totten Key", "https://en.wikipedia.org/wiki/Totten_Key"},
            {"Mary of the Movies", "https://en.wikipedia.org/wiki/Mary_of_the_Movies"},
            {"Empress Dowager Bo", "https://en.wikipedia.org/wiki/Empress_Dowager_Bo"},
            {"Shane Morrison", "https://en.wikipedia.org/wiki/Shane_Morrison"},
        };
        float [] pagesDelay = new float[pagesToVisit.length];
        float avg = 0;

        for (int i = 0; i < pagesToVisit.length; i++) {
            Intent test = new Intent();
            test
                    .putExtra("start_page", pagesToVisit[i][0])
                    .putExtra("end_page", "Destination Article")
                    .putExtra("start_url", pagesToVisit[i][1])
                    .putExtra("end_url", "Destination Article URL");

            activityRule.launchActivity(test);

            Date beforeLoading = new Date();
            onWebView()
                    .withElement(findElement(Locator.ID, "firstHeading"))
                    .check(webMatches(getText(), containsString(pagesToVisit[i][0])));
            Date afterLoading = new Date();
            pagesDelay[i] = (afterLoading.getTime() - beforeLoading.getTime())/1000f;

            Log.d(TAG, "Elapsed Time: " + pagesDelay[i]+ "\n");
            activityRule.finishActivity();

            avg += pagesDelay[i];

            assertTrue(pagesDelay[i] < 2f);
        }

        assertTrue(avg/10f < 1f);
    }

    @Test
    public void inSinglePlayerGameTest() throws InterruptedException {
        Intent test = new Intent();
        test
            .putExtra("start_page", "Taco")
            .putExtra("end_page", "Mexico")
            .putExtra("start_url", "https://en.wikipedia.org/wiki/Taco")
            .putExtra("end_url", "https://en.wikipedia.org/wiki/Mexico");
        activityRule.launchActivity(test);

        onView(withText("Destination Page: Mexico")).check(matches(isDisplayed()));
        onWebView()
                .check(webMatches(getCurrentUrl(), containsString("Taco")));

        onWebView()
                .withElement(findElement(Locator.CSS_SELECTOR, "a[href=\"/wiki/British_English\"]")) // similar to onView(withId(...))
                .perform(webClick());

        onWebView()
                .withElement(findElement(Locator.ID, "firstHeading"))
                .check(webMatches(getText(), containsString("British English")));

        onWebView()
                .withElement(findElement(Locator.CSS_SELECTOR, "a[href=\"/wiki/English_language\"]")) // similar to onView(withId(...))
                .perform(webClick());

        onWebView()
                .withElement(findElement(Locator.ID, "firstHeading"))
                .check(webMatches(getText(), containsString("English language")));

        ///wiki/English_language
        while (true) {
            try {
                onView(withText("00:05")).perform().check(matches(isDisplayed()));
                break;
            } catch (Exception e) {
            }
        }
        while (true) {
            try {
                onView(withText("00:10")).perform().check(matches(isDisplayed()));
                break;
            } catch (Exception e) {
            }
        }

        //check back button functionality
        onView(ViewMatchers.withId(R.id.wikipedia_page_view))
                .perform(pressBack());
        onWebView()
                .withElement(findElement(Locator.ID, "firstHeading"))
                .check(webMatches(getText(), containsString("British English")));

        onView(withId(R.id.wikipedia_page_view))
                .perform(pressBack());
        onWebView()
                .withElement(findElement(Locator.ID, "firstHeading"))
                .check(webMatches(getText(), containsString("Taco")));

        onView(withId(R.id.wikipedia_page_view))
                .perform(pressBack());
        intended(hasComponent(PlayGameActivity.class.getName()));
    }
}
