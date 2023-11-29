package com.karatekids.wikipediarace.leaderboardtests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotClickable;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.karatekids.wikipediarace.SignInActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class BeforeLeaderboardTest {
    @Rule
    public ActivityScenarioRule<SignInActivity> activityRule =
            new ActivityScenarioRule<>(SignInActivity.class);

    //ChatGPT usage: No
    @Test
    public void navigationToLeaderboardTest() throws InterruptedException {

        //doesn't need espresso
        onView(withText("Sign in as a Guest")).check(matches(isDisplayed()))
                .check(matches(isClickable()));
        onView(withText("Sign in as a Guest")).perform(click());

        Thread.sleep(1000);

        onView(withText("View Leaderboard")).check(matches(isDisplayed()))
                .check(matches(isClickable()));
        onView(withText("View Leaderboard")).perform(click());

        onView(withText("Ranking")).check(matches(isDisplayed()))
                .check(matches(isNotClickable()));
    }
}