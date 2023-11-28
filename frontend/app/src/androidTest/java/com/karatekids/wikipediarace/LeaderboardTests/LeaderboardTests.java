package com.karatekids.wikipediarace.LeaderboardTests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;

import com.karatekids.wikipediarace.LeaderboardActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;

public class LeaderboardTests {
        @Rule
        public IntentsTestRule<LeaderboardActivity> activityRule =
                new IntentsTestRule<>(LeaderboardActivity.class, false, false);

        //ChatGPT usage: No
        public String mockLeaderboard() throws JSONException {
            // mock leaderboard with 4 players
            JSONObject player1 = new JSONObject()
                    .put("_id",1)
                    .put("name","julian kennedy")
                    .put("elo", 10)
                    .put("gamesWon",5)
                    .put("gamesLost",1);

            JSONObject player2 = new JSONObject()
                    .put("_id",2)
                    .put("name","Mark Morcos")
                    .put("elo", 5)
                    .put("gamesWon",2)
                    .put("gamesLost",0);

            JSONObject player3 = new JSONObject()
                    .put("_id",3)
                    .put("name","Mark Mekhail")
                    .put("elo", 4)
                    .put("gamesWon",2)
                    .put("gamesLost",0);

            JSONObject player4 = new JSONObject()
                    .put("_id",4)
                    .put("name","Kyle van Winkoop")
                    .put("elo", 4)
                    .put("gamesWon",2)
                    .put("gamesLost",2);


            JSONArray playerArray = new JSONArray();
            playerArray.put(player1)
                    .put(player2)
                    .put(player3)
                    .put(player4);

            return playerArray.toString();
        }

    public String mockOneElementLeaderBoard() throws JSONException {
        // mock leaderboard with 1 player
        JSONObject player1 = new JSONObject()
                .put("_id",1)
                .put("name","Guest")
                .put("elo", 10)
                .put("gamesWon",5)
                .put("gamesLost",1);

        JSONArray playerArray = new JSONArray();
        playerArray.put(player1);

        return playerArray.toString();
    }

    //ChatGPT usage: No
    @Test
    public void leaderboardViewTest() throws InterruptedException, JSONException {
        // start in the leaderboard activity
        Intent leaderboardTest = new Intent();
        leaderboardTest
                .putExtra("leaderboardData", mockLeaderboard());
        activityRule.launchActivity(leaderboardTest);

        //check that player julian kennedy is displayed
        onView(withText("julian kennedy")).check(matches(isDisplayed()));

        // check that player Mark Morcos is displayed
        onView(withText("Mark Morcos")).check(matches(isDisplayed()));

        // check that player Mark Mekhail is displayed
        onView(withText("Mark Mekhail")).check(matches(isDisplayed()));

        // check that player Kyle van Winkoop is displayed
        onView(withText("Kyle van Winkoop")).check(matches(isDisplayed()));

        // check that stats view of player has not been created in the view
        onView(withText("Number of Games Won:")).check(doesNotExist());

        //check that the table has 3 columns
        ViewInteraction row = onView(withChild(withText("julian kennedy")));
        row.check(matches(hasChildCount(3)));

        //check that the table has 4 entries and each entry is clickable
        onView(withChild(withChild(withText("julian kennedy"))))
                .check(matches(isClickable()))
                .check(matches(hasChildCount(4)));

        //click on the player julian kennedy
        onView(withText("julian kennedy")).perform(ViewActions.click());

        // the statistics page for julian kennedy appears
        onView(withText("Number of Games Won:")).check(matches(isDisplayed()));

        // the rows of all the players do not exist with pop up
        onView(withText("Mark Morcos")).check(doesNotExist());
        onView(withText("Mark Mekhail")).check(doesNotExist());
        onView(withText("Kyle van Winkoop")).check(doesNotExist());
    }

    //ChatGPT usage: No
    //minimum test as the fewest number of people could only be yourself
    @Test
    public void leaderboardWithOneElementTest() throws JSONException {
        // start in the leaderboard activity
        Intent leaderboardTest = new Intent();
        leaderboardTest
                .putExtra("leaderboardData", mockOneElementLeaderBoard());
        activityRule.launchActivity(leaderboardTest);

        //check that player Guest is displayed
        onView(withText("Guest")).check(matches(isDisplayed()));

        //check that the table has 1 entry and it is clickable
        onView(withChild(withChild(withText("Guest"))))
                .check(matches(isClickable()))
                .check(matches(hasChildCount(1)));
    }


}
