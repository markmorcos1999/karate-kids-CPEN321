package com.karatekids.wikipediarace;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class AfterSinglePlayerGameTest {

    @Rule
    public IntentsTestRule<ResultsActivity> activityRule =
            new IntentsTestRule<>(ResultsActivity.class, false, false);

    @Test
    public void afterSinglePlayerGameTest() throws JSONException {
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
        onView(withId(R.id.shortest_path_text))
                .perform(pressBack());
        intended(hasComponent(MainActivity.class.getName()));
    }
}
