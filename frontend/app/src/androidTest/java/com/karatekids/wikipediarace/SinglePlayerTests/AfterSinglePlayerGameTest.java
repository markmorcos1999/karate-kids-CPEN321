package com.karatekids.wikipediarace.SinglePlayerTests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.karatekids.wikipediarace.MainActivity;
import com.karatekids.wikipediarace.R;
import com.karatekids.wikipediarace.ResultsActivity;

import org.json.JSONException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class AfterSinglePlayerGameTest {

    @Rule
    public IntentsTestRule<ResultsActivity> activityRule =
            new IntentsTestRule<>(ResultsActivity.class, false, false);

    //ChatGPT usage: No
    @Test
    public void afterSinglePlayerTacoMexicoWithBack() throws JSONException {
        afterSinglePlayerGameTacoMexicoTestCase();
        onView(ViewMatchers.withId(R.id.shortest_path_text))
                .perform(pressBack());
        intended(hasComponent(MainActivity.class.getName()));
    }

    //ChatGPT usage: No
    @Test
    public void afterSinglePlayerTacoMexicoWithButton () throws JSONException {
        afterSinglePlayerGameTacoMexicoTestCase();
        onView(withText("Return to Main Page"))
                .perform(click());
        intended(hasComponent(MainActivity.class.getName()));
    }

    //ChatGPT usage: No
    @Test
    public void afterSinglePlayerGameCneoridiumJuanWithButton () throws JSONException {
        afterSinglePlayerGameCneoridiumJuanTestCase();
        onView(withText("Return to Main Page"))
                .perform(ViewActions.scrollTo())
                .perform(click());
        intended(hasComponent(MainActivity.class.getName()));
    }

    //ChatGPT usage: No
    @Test
    public void afterSinglePlayerGameCneoridiumJuanWithBack () throws JSONException {
        afterSinglePlayerGameCneoridiumJuanTestCase();
        onView(withId(R.id.shortest_path_text))
                .perform(pressBack());
        intended(hasComponent(MainActivity.class.getName()));
    }

    //ChatGPT usage: No
    public void afterSinglePlayerGameTacoMexicoTestCase() throws JSONException {
        Intent test = new Intent();
        int pagesVisitedCount = 3;
        ArrayList<String> pagesVisited = new ArrayList<>();
        pagesVisited.add("Taco");
        pagesVisited.add("Mexican cuisine");
        pagesVisited.add("Mexico");

        test
                .putExtra("data", "{\"gamePosition\":1,\"shortestPath\":[\"Taco\",\"Mexico\"]}")
                .putExtra("count", pagesVisitedCount)
                .putExtra("visited_list", pagesVisited)
                .putExtra("time", "00:30");
        activityRule.launchActivity(test);

        onView(withText("Number of Links to Reach Destination: 3\n")).check(matches(isDisplayed()));
        onView(withText("Your Path Traversal:\n\nTaco\n|\nV\nMexican cuisine\n|\nV\nMexico\n"))
                .check(matches(isDisplayed()));
        onView(withText("Shortest Path Possible:\n\nTaco\n|\nV\nMexico\n"))//\n|\nV\nMexico\n"))
                .check(matches(isDisplayed()));
        onView(withText("Position: 1")).check(matches(isDisplayed()));
        onView(withText("Seconds: 30\n")).check(matches(isDisplayed()));
    }

    //ChatGPT usage: No
    public void afterSinglePlayerGameCneoridiumJuanTestCase() throws JSONException {
        Intent test = new Intent();
        int pagesVisitedCount = 5;
        ArrayList<String> pagesVisited = new ArrayList<>();
        pagesVisited.add("Cneoridium dumosum (Nuttall) Hooker F. Collected March 26, 1960, " +
                "at an Elevation of about 1450 Meters on Cerro Quemazón, 15 Miles South of Bahía " +
                "de Los Angeles, Baja California, México, Apparently for a Southeastward Range " +
                "Extension of Some 140 Miles");
        pagesVisited.add("Cneoridium");
        pagesVisited.add("California quail");
        pagesVisited.add("Coronado Islands");
        pagesVisited.add("Juan Rodríguez Cabrillo");


        test
                .putExtra("data", "{\"gamePosition\":1,\"shortestPath\":[\"Cneoridium" +
                        " dumosum (Nuttall) Hooker F. Collected March 26, 1960, at an Elevation" +
                        " of about 1450 Meters on Cerro Quemazón, 15 Miles South of Bahía de" +
                        " Los Angeles, Baja California, México, Apparently for a Southeastward" +
                        " Range Extension of Some 140 Miles\",\"Cneoridium\",\"California quail\"," +
                        "\"Coronado Islands\",\"Juan Rodríguez Cabrillo\"]}")
                .putExtra("count", pagesVisitedCount)
                .putExtra("visited_list", pagesVisited)
                .putExtra("time", "02:30");
        activityRule.launchActivity(test);

        onView(withText("Number of Links to Reach Destination: 5\n")).check(matches(isDisplayed()));
        onView(withText("Your Path Traversal:\n\nCneoridium dumosum (Nuttall) Hooker" +
                " F. Collected March 26, 1960, at an Elevation of about 1450 Meters on " +
                "Cerro Quemazón, 15 Miles South of Bahía de Los Angeles, Baja California, " +
                "México, Apparently for a Southeastward Range Extension of Some 140 Miles\n|\nV\n" +
                "Cneoridium\n|\nV\n" +
                "California quail\n|\nV\n" +
                "Coronado Islands\n|\nV\n" +
                "Juan Rodríguez Cabrillo\n"))
                .check(matches(isDisplayed()));
        onView(withText("Shortest Path Possible:\n\n" +
                "Cneoridium dumosum (Nuttall) Hooker" +
                " F. Collected March 26, 1960, at an Elevation of about 1450 Meters on " +
                "Cerro Quemazón, 15 Miles South of Bahía de Los Angeles, Baja California, " +
                "México, Apparently for a Southeastward Range Extension of Some 140 Miles\n|\nV\n" +
                "Cneoridium\n|\nV\n" +
                "California quail\n|\nV\n" +
                "Coronado Islands\n|\nV\n" +
                "Juan Rodríguez Cabrillo\n"))
                .check(matches(isDisplayed()));
        onView(withText("Position: 1")).perform(ViewActions.scrollTo())
                .check(matches(isDisplayed()));
        onView(withText("Seconds: 150\n")).perform(ViewActions.scrollTo())
                .check(matches(isDisplayed()));
    }
}
