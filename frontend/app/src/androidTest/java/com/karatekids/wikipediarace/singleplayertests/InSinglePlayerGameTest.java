package com.karatekids.wikipediarace.singleplayertests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
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
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.util.Log;

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

import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class InSinglePlayerGameTest {
    private static final String TAG = "InSinglePlayerGameTest";
    @Rule
    public IntentsTestRule<InGameActivity> activityRule =
            new IntentsTestRule<>(InGameActivity.class, false, false);

    @Rule
    public ActivityScenarioRule<SignInActivity> signInActivityRule =
            new ActivityScenarioRule<>(SignInActivity.class);

    //ChatGPT usage: No
    //https://newsletter.automationhacks.io/p/hello-espresso-part-5-automating
    @Test
    public void inSinglePlayerGameDelayTest() {
        //https://wikiroulette.co/?p=Shane_Morrison
        String [] [] pagesToVisit = {
            {"Rekha", "https://en.wikipedia.org/wiki/Rekha_(1943_film)"},
            {"John Morrison Forbes", "https://en.wikipedia.org/wiki/John_Morrison_Forbes"},
            {"States of Jersey Customs and Immigration Service", "https://en.wikipedia.org/wiki/States_of_Jersey_Customs_and_Immigration_Service"},
            {"Yo Soy", "https://en.wikipedia.org/wiki/Yo_Soy_(Peruvian_TV_series)"},
            {"Bulgarian National-Patriotic Party", "https://en.wikipedia.org/wiki/Bulgarian_National-Patriotic_Party"},
            {"backshore", "https://en.wikipedia.org/wiki/Backshore"},
            {"Totten Key", "https://en.wikipedia.org/wiki/Totten_Key"},
            {"Mary of the Movies", "https://en.wikipedia.org/wiki/Mary_of_the_Movies"},
            {"Empress Dowager Bo", "https://en.wikipedia.org/wiki/Empress_Dowager_Bo"},
            {"Shane Morrison", "https://en.wikipedia.org/wiki/Shane_Morrison"},
            {"Atlason", "https://en.wikipedia.org/wiki/Atlason"},
            {"Jewish National Front", "https://en.wikipedia.org/wiki/Jewish_National_Front"},
            {"Flight 185", "https://en.wikipedia.org/wiki/Flight_185"},
            {"Cathedral of the Assumption of the Virgin", "https://en.wikipedia.org/wiki/Cathedral_of_the_Assumption_of_the_Virgin,_Tashkent"},
            {"Eving", "https://en.wikipedia.org/wiki/Eving"},
            {"Leicester Mercury", "https://en.wikipedia.org/wiki/Leicester_Mercury"},
            {"Holly Lincoln", "https://en.wikipedia.org/wiki/Holly_Lincoln"},
            {"TV5", "https://en.wikipedia.org/wiki/TV5_(Algerian_TV_channel)"},
            {"Javier Díaz", "https://en.wikipedia.org/wiki/Javier_D%C3%ADaz_(swimmer)"},
            {"Hendrik Witbooi", "https://en.wikipedia.org/wiki/Hendrik_Witbooi"},
        };
        float [] pagesDelay = new float[pagesToVisit.length];
        float avg = 0;
        int aboveThresholdCount = 0;

        for (int i = 0; i < pagesToVisit.length; i++) {
            Intent test = new Intent();
            test
                    .putExtra("start_page", pagesToVisit[i][0])
                    .putExtra("end_page", "Destination Article")
                    .putExtra("start_url", pagesToVisit[i][1])
                    .putExtra("end_url", "Destination Article URL");

            Date beforeLoading = new Date();
            activityRule.launchActivity(test);
            if(pagesToVisit[i][0].indexOf('(') != -1) {
                onWebView()
                        .withElement(findElement(Locator.ID, "mw-content-text"))
                        .check(webMatches(getText(), containsString(pagesToVisit[i][0].substring(0, pagesToVisit[i][0].indexOf('(')))));
            } else {
                onWebView()
                        .withElement(findElement(Locator.ID, "mw-content-text"))
                        .check(webMatches(getText(), containsString(pagesToVisit[i][0])));
            }

            Date afterLoading = new Date();
            pagesDelay[i] = (afterLoading.getTime() - beforeLoading.getTime())/1000f;

            Log.d(TAG, "Elapsed Time: " + pagesDelay[i]+ "\n");
            activityRule.finishActivity();

            avg += pagesDelay[i];

            assertTrue(pagesDelay[i] <= 4f);

            if(pagesDelay[i] > 2.5f) aboveThresholdCount++;
        }

        assertTrue(aboveThresholdCount <= 1);
    }

    //ChatGPT usage: No
    @Test
    public void inSinglePlayerGameLargestDelayTest() throws InterruptedException {
        Intent test = new Intent();
        test
                .putExtra("start_page", "List of Joe Biden 2020 presidential campaign endorsements")
                .putExtra("end_page", "Mexico")
                .putExtra("start_url", "https://en.wikipedia.org/wiki/List_of_Joe_Biden_2020_presidential_campaign_endorsements")
                .putExtra("end_url", "https://en.wikipedia.org/wiki/Mexico");
        activityRule.launchActivity(test);
        Date beforeLoading = new Date();
        onWebView()
                .withElement(findElement(Locator.ID, "firstHeading"))
                .check(webMatches(getText(), containsString("List of Joe Biden 2020 presidential campaign endorsements")));
        Date afterLoading = new Date();
        float largestDelay = (afterLoading.getTime() - beforeLoading.getTime())/1000f;
        Log.d(TAG, "Elapsed Time: " + largestDelay + "\n");
        assertTrue(largestDelay < 2.5f);
        activityRule.finishActivity();
    }

    //ChatGPT usage: No
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
                .withElement(findElement(Locator.ID, "firstHeading"))
                .check(webMatches(getText(), containsString("Taco")));

        onWebView()
                .withElement(findElement(Locator.CSS_SELECTOR, "a[href=\"/wiki/British_English\"]"))
                .perform(webClick());

        onWebView()
                .withElement(findElement(Locator.ID, "firstHeading"))
                .check(webMatches(getText(), containsString("British English")));

        onWebView()
                .withElement(findElement(Locator.CSS_SELECTOR, "a[href=\"/wiki/English_language\"]"))
                .perform(webClick());

        onWebView()
                .withElement(findElement(Locator.ID, "firstHeading"))
                .check(webMatches(getText(), containsString("English language")));

        onWebView()
                .withElement(findElement(Locator.ID, "Bibliography"))
                .perform(webScrollIntoView())
                .perform(webClick())
                .withElement(findElement(Locator.CSS_SELECTOR, "a[href=\"http://www.oxfordlearnersdictionaries.com/\"]"))
                .perform(webScrollIntoView())
                .perform(webClick())
                .withElement(findElement(Locator.ID, "firstHeading"))
                .check(webMatches(getText(), containsString("English language")));

        while (true) {
            try {
                onView(withText("00:07")).perform().check(matches(isDisplayed()));
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
        onView(withId(R.id.wikipedia_page_view))
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


    //ChatGPT usage: No
    @Test
    public void inSinglePlayerGameTestQuit() throws InterruptedException {
        //sign in as a guest and check that the button is displayed and clickable
        onView(withText("Sign in as a Guest")).check(matches(isDisplayed()));
        onView(withText("Sign in as a Guest")).perform(click());

        Thread.sleep(3000);

        //check that the play a game button is displayed and clickable
        onView(withText("Play a Game")).check(matches(isDisplayed()));
        onView(withText("Play a Game")).perform(click());

        //check that the join a single player game is displayed and clickable
        onView(withText("Join a Single Player Game")).check(matches(isDisplayed()));
        onView(withText("Join a Single Player Game")).perform(click());

        //check loading animation is visible
        onView(withId(R.id.loading_pb))
                .check(matches(isDisplayed()))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        //check that the finding start and end pages string is displayed
        onView(withText("Finding Start and End Pages...")).check(matches(isDisplayed()));


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
