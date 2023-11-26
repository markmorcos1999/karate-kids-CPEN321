package com.karatekids.wikipediarace.LeaderboardTests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.karatekids.wikipediarace.InGameActivity;
import com.karatekids.wikipediarace.LeaderboardActivity;

import org.junit.Rule;
import org.junit.Test;

public class LeaderboardTests {
        @Rule
        public IntentsTestRule<LeaderboardActivity> activityRule =
                new IntentsTestRule<>(LeaderboardActivity.class, false, false);

        public String mockLeaderboard() {

        }

        @Test
        public void leaderboardViewTest() throws InterruptedException {
            Intent leaderboardTest = new Intent();
            leaderboardTest
                    .putExtra("start_page", "Taco")
                    .putExtra("end_page", "Mexico")
                    .putExtra("start_url", "https://en.wikipedia.org/wiki/Taco")
                    .putExtra("end_url", "https://en.wikipedia.org/wiki/Mexico");
            activityRule.launchActivity(test);
        }
}
