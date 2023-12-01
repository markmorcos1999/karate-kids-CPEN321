package com.karatekids.wikipediarace.leaderboardtests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;

import com.karatekids.wikipediarace.LeaderboardActivity;
import com.karatekids.wikipediarace.R;

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
                .put("name","julian kennedy")
                .put("elo", 10)
                .put("gamesWon",5)
                .put("gamesLost",1);

        JSONArray playerArray = new JSONArray();
        playerArray.put(player1);

        return playerArray.toString();
    }

    //ChatGPT usage: No
    public String mockElevenElementLeaderboard() throws JSONException {
        // mock leaderboard with 11 players
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

        JSONObject player5 = new JSONObject()
                .put("_id",5)
                .put("name","Player 5")
                .put("elo", 4)
                .put("gamesWon",1)
                .put("gamesLost",2);

        JSONObject player6 = new JSONObject()
                .put("_id",6)
                .put("name","Player 6")
                .put("elo", 3)
                .put("gamesWon",2)
                .put("gamesLost",2);

        JSONObject player7 = new JSONObject()
                .put("_id",7)
                .put("name","Player 7")
                .put("elo", 3)
                .put("gamesWon",1)
                .put("gamesLost",2);

        JSONObject player8 = new JSONObject()
                .put("_id",8)
                .put("name","Player 8")
                .put("elo", 2)
                .put("gamesWon",2)
                .put("gamesLost",2);

        JSONObject player9 = new JSONObject()
                .put("_id",9)
                .put("name","Player 9")
                .put("elo", 1)
                .put("gamesWon",1)
                .put("gamesLost",2);

        JSONObject player10 = new JSONObject()
                .put("_id",10)
                .put("name","Player 10")
                .put("elo", 0)
                .put("gamesWon",0)
                .put("gamesLost",2);

        JSONObject player11 = new JSONObject()
                .put("_id",11)
                .put("name","Player 11")
                .put("elo", 0)
                .put("gamesWon",0)
                .put("gamesLost",2);


        JSONArray playerArray = new JSONArray();
        playerArray.put(player1)
                .put(player2)
                .put(player3)
                .put(player4)
                .put(player5)
                .put(player6)
                .put(player7)
                .put(player8)
                .put(player9)
                .put(player10)
                .put(player11);

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

        //check that the achievement badge does not exist
        onView(withId(R.id.badge_iv)).check(doesNotExist());

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
        onView(withText("julian kennedy")).check(matches(isDisplayed()));

        //check that the achievement badge is visible
        onView(withId(R.id.badge_iv)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

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

        //check that player julian kennedy is displayed
        onView(withText("julian kennedy")).check(matches(isDisplayed()));

        //check that the table has 1 entry and it is clickable
        onView(withChild(withChild(withText("julian kennedy"))))
                .check(matches(isClickable()))
                .check(matches(hasChildCount(1)));

        //click on the player julian kennedy
        onView(withText("julian kennedy")).perform(ViewActions.click());

        // the statistics page for julian kennedy appears
        onView(withText("Number of Games Won:")).check(matches(isDisplayed()));
        onView(withText("julian kennedy")).check(matches(isDisplayed()));

        //check that the achievement badge is visible
        onView(withId(R.id.badge_iv)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    //ChatGPT usage: No
    //maximum test as the maximum number of people in the leaderboard at one time is 10
    @Test
    public void leaderboardWithElevenElementsTest() throws JSONException {
        // start in the leaderboard activity
        Intent leaderboardTest = new Intent();
        leaderboardTest
                .putExtra("leaderboardData", mockElevenElementLeaderboard());
        activityRule.launchActivity(leaderboardTest);

        //check that julian kennedy is displayed
        onView(withText("julian kennedy")).check(matches(isDisplayed()));

        //check that Player 11 is not displayed
        onView(withText("Player 11")).check(doesNotExist());

        //check that the table has 1 entry and it is clickable
        onView(withChild(withChild(withText("julian kennedy"))))
                .check(matches(isClickable()))
                .check(matches(hasChildCount(10)));

        //click on the player julian kennedy
        onView(withText("julian kennedy")).perform(ViewActions.click());

        // the statistics page for julian kennedy appears
        onView(withText("Number of Games Won:")).check(matches(isDisplayed()));
        onView(withText("julian kennedy")).check(matches(isDisplayed()));

        //check that the achievement badge is visible
        onView(withId(R.id.badge_iv)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        // the rows of all the players do not exist with pop up
        onView(withText("Mark Morcos")).check(doesNotExist());
        onView(withText("Mark Mekhail")).check(doesNotExist());
        onView(withText("Kyle van Winkoop")).check(doesNotExist());
        onView(withText("Player 5")).check(doesNotExist());
        onView(withText("Player 6")).check(doesNotExist());
        onView(withText("Player 7")).check(doesNotExist());
        onView(withText("Player 8")).check(doesNotExist());
        onView(withText("Player 9")).check(doesNotExist());
        onView(withText("Player 10")).check(doesNotExist());
    }


}
