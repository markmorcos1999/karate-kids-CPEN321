package com.karatekids.wikipediarace;

import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class StatisticsActivity extends AppCompatActivity {
    private static final int SILVER_THRESHOLD = 8;
    private static final int GOLD_THRESHOLD = 10;

    //ChatGPT usage: No
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        //TODO: display player statistics of games in lifetime
        TextView games_won = (TextView) findViewById(R.id.games_won_value_tv);
        TextView games_lost = (TextView) findViewById(R.id.games_lost_value_tv);
        TextView elo = (TextView) findViewById(R.id.elo_value_tv);
        TextView player_id = (TextView) findViewById(R.id.player_id_value_tv);

        Bundle b = getIntent().getExtras();

        String JSON_String = b.getString("playerStats");

        try{
            JSONObject obj = new JSONObject(JSON_String);
            games_won.setText(obj.getString("gamesWon"));
            games_lost.setText(obj.getString("gamesLost"));
            elo.setText(obj.getString("elo"));
            player_id.setText(obj.getString("_id"));

            int gamesWon = Integer.parseInt(obj.getString("gamesWon"));

            if(gamesWon >= GOLD_THRESHOLD) {
                ImageView image = (ImageView) findViewById(R.id.badge_iv);
                image.setImageResource(R.drawable.gold_badge);
            } else if (gamesWon < GOLD_THRESHOLD && gamesWon >= SILVER_THRESHOLD) {
                ImageView image = (ImageView) findViewById(R.id.badge_iv);
                image.setImageResource(R.drawable.silver_badge);
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
}