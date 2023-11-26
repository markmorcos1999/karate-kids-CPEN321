package com.karatekids.wikipediarace.LeaderboardTests;

import android.os.Bundle;
import android.util.Log;

import androidx.constraintlayout.utils.widget.MockView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.karatekids.wikipediarace.InGameActivity;
import com.karatekids.wikipediarace.LeaderboardActivity;
import com.karatekids.wikipediarace.R;
import com.karatekids.wikipediarace.SignInActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;

public class MockLeaderboard {

    //ChatGPT usage: No
    @Rule
    public IntentsTestRule<InGameActivity> activityRule =
            new IntentsTestRule<>(InGameActivity.class, false, false);


}
