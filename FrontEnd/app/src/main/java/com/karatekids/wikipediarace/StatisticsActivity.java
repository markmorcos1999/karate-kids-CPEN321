package com.karatekids.wikipediarace;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class StatisticsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        //TODO: display player statistics of games in lifetime
        TextView games_won = (TextView) findViewById(R.id.games_won_value_tv);
        TextView games_lost = (TextView) findViewById(R.id.games_lost_value_tv);
        TextView avg_traversal_length = (TextView) findViewById(R.id.avg_traversal_length_value_tv);
        TextView avg_time_length = (TextView) findViewById(R.id.avg_traversal_time_value_tv);

        Bundle b = getIntent().getExtras();

        String JSON_String = b.getString("playerStats");

        try{
            JSONObject obj = new JSONObject(JSON_String);
            games_won.setText(obj.getString("gamesWon"));
            games_lost.setText(obj.getString("gamesLost"));
            avg_time_length.setText(obj.getString("averageLength"));
            avg_traversal_length.setText(obj.getString("averageTime"));

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
}