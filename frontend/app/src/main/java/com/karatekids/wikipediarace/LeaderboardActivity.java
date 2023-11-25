package com.karatekids.wikipediarace;

import static android.widget.Toast.*;

import static androidx.core.content.ContextCompat.startActivity;
import static com.google.android.material.internal.ContextUtils.getActivity;
import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;

public class LeaderboardActivity extends AppCompatActivity {

    private final static String TAG = "LeaderboardActivity";

    public static String JSON_String = "";

    private static final int[] TABLE_HEADERS = {R.string.table_ranking_st, R.string.table_name_st, R.string.table_score_st};

    //ChatGPT usage: No
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard_layout_table_view);

        //TODO: set up leaderboard table and show the rankings of top users
        Bundle b = getIntent().getExtras();

        // https://github.com/ISchwarz23/SortableTableView
        // add the headers for the table
        TableView<String[]> tableView = (TableView<String[]>) findViewById(R.id.leaderboard_table);
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(this, TABLE_HEADERS));
        tableView.addDataClickListener(new ProfileClickListener());

        // create rows of alternating colors
        int colorEvenRows = getResources().getColor(R.color.white);
        int colorOddRows = getResources().getColor(R.color.LightGrey);
        tableView.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));

        JSON_String = b.getString("leaderboardData");

        int count = 1;

        try {
            // turn the JSON String into an array
            JSONArray arr = new JSONArray(JSON_String);
            String[][] leaderboardData = new String[Math.min(arr.length(), 10)][3];

            // iterate through the array and add each row of data to leaderboardData
            for (int i = 0; i <= Math.min(arr.length() - 1, 9); i++) {
                String[] rowData = new String[3];

                JSONObject element = arr.getJSONObject(i);

                rowData[0] = String.valueOf(count);
                rowData[1] = element.getString("name");
                rowData[2] = element.getString("elo");

                leaderboardData[count - 1] = rowData;
                count++;
            }

            // add all of the extracted rows to the leaderboard
            tableView.setDataAdapter(new SimpleTableDataAdapter(this, leaderboardData));

        } catch (JSONException e) {
            Log.d(TAG, "Unable to parse leaderboard data from server");
        }
    }


    // https://github.com/ISchwarz23/SortableTableView/tree/master
    private class ProfileClickListener implements TableDataClickListener<String[]> {
        @Override
        public void onDataClicked(int rowIndex, String[] player) {
            try {
                // turn the JSON String into an array
                JSONArray arr = new JSONArray(LeaderboardActivity.JSON_String);

                JSONObject element = arr.getJSONObject(rowIndex);

                Intent leaderboardStatisticsIntent = new Intent(LeaderboardActivity.this, LeaderboardStatisticsActivity.class);
                leaderboardStatisticsIntent.putExtra("playerStats", element.toString());
                startActivity(leaderboardStatisticsIntent);


            } catch (JSONException e) {
                Log.d(TAG, "Unable to parse leaderboard data from server");
            }
        }
    }
}
