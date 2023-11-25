package com.karatekids.wikipediarace;

import android.content.Intent;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class AfterSinglePlayerGameTest {

    @Rule
    public IntentsTestRule<ResultsActivity> activityRule =
            new IntentsTestRule<>(ResultsActivity.class, false, false);

    @Test
    public void afterSinglePlayerGameTest() {
        Intent test = new Intent();
        test
                .putExtra("start_page", "Taco")
                .putExtra("end_page", "Mexico")
                .putExtra("start_url", "https://en.wikipedia.org/wiki/Taco")
                .putExtra("end_url", "https://en.wikipedia.org/wiki/Mexico");
        Date beforeActivity = new Date();
        activityRule.launchActivity(test);
    }
}
