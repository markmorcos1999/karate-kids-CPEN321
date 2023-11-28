package com.karatekids.wikipediarace.MultiPlayerTests;


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
public class AfterMultiPlayerGameTest {

    @Rule
    public IntentsTestRule<ResultsActivity> activityRule =
            new IntentsTestRule<>(ResultsActivity.class, false, false);

    //ChatGPT usage: No
    @Test
    public void afterMultiPlayerTacoMexicoWithBackButton() throws JSONException {
        afterMultiPlayerGameTacoMexicoTestCase();
        onView(ViewMatchers.withId(R.id.shortest_path_text))
                .perform(pressBack());
        intended(hasComponent(MainActivity.class.getName()));
    }

    //ChatGPT usage: No
    @Test
    public void aftermMultiPlayerTacoMexicoWithButtonToMain () throws JSONException {
        afterMultiPlayerGameTacoMexicoTestCase();
        onView(withText("Return to Main Page"))
                .perform(click());
        intended(hasComponent(MainActivity.class.getName()));
    }

    //ChatGPT usage: No
    public void afterMultiPlayerGameTacoMexicoTestCase() throws JSONException {
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
                .putExtra("time", "00:30")
                .putExtra("gamePosition",2);
        activityRule.launchActivity(test);

        onView(withText("Number of Links to Reach Destination: 3\n")).check(matches(isDisplayed()));
        onView(withText("Your Path Traversal:\n\nTaco\n|\nV\nMexican cuisine\n|\nV\nMexico\n"))
                .check(matches(isDisplayed()));
        onView(withText("Shortest Path Possible:\n\nTaco\n|\nV\nMexico\n"))
                .check(matches(isDisplayed()));
        onView(withText("Position: 1")).check(matches(isDisplayed()));
        onView(withText("Seconds: 30\n")).check(matches(isDisplayed()));
    }
}
