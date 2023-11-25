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
import android.widget.ImageView;
import android.widget.TextView;
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

    private static final int SILVER_THRESHOLD = 8;
    private static final int GOLD_THRESHOLD = 10;

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

                JSONObject obj = arr.getJSONObject(rowIndex);

                // https://stackoverflow.com/questions/16191562/display-textview-in-alert-dialog
                // create a dialog and set teh custom view
                Dialog dialog = new Dialog(LeaderboardActivity.this);
                dialog.setContentView(R.layout.activity_statistics_dialog);

                // find and set all the text values based on the person chosen from the leaderboard
                TextView games_won = (TextView) dialog.findViewById(R.id.games_won_value_tv);
                TextView games_lost = (TextView) dialog.findViewById(R.id.games_lost_value_tv);
                TextView elo = (TextView) dialog.findViewById(R.id.elo_value_tv);
                TextView player_id = (TextView) dialog.findViewById(R.id.player_id_value_tv);
                TextView player_name = (TextView) dialog.findViewById(R.id.player_name_tv);

                games_won.setText(obj.getString("gamesWon"));
                games_lost.setText(obj.getString("gamesLost"));
                elo.setText(obj.getString("elo"));
                player_id.setText(obj.getString("_id"));
                player_name.setText(obj.getString("name"));

                int gamesWon = Integer.parseInt(obj.getString("gamesWon"));

                if(gamesWon >= GOLD_THRESHOLD) {
                    ImageView image = (ImageView) findViewById(R.id.badge_iv);
                    image.setImageResource(R.drawable.gold_badge);
                } else if (gamesWon < GOLD_THRESHOLD && gamesWon >= SILVER_THRESHOLD) {
                    ImageView image = (ImageView) findViewById(R.id.badge_iv);
                    image.setImageResource(R.drawable.silver_badge);
                }

                // show the dialog
                dialog.show();

            } catch (JSONException e) {
                Log.d(TAG, "Unable to parse leaderboard data from server");
            }
        }
    }
}
