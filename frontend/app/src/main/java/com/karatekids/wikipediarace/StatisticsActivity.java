package com.karatekids.wikipediarace;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class StatisticsActivity extends AppCompatActivity {

    private final static String TAG = "StatisticsActivity";
    private static final int SILVER_THRESHOLD = 3;
    private static final int GOLD_THRESHOLD = 6;

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
            Log.d(TAG, "Unable to parse statistics data from server");
        }
    }

    //ChatGPT usage: No
    // https://www.geeksforgeeks.org/how-to-add-share-button-in-toolbar-in-android/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        // first parameter is the file for icon and second one is menu
        return super.onCreateOptionsMenu(menu);
    }

    //ChatGPT usage: No
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // We are using switch case because multiple icons can be kept
        switch (item.getItemId()) {
            case R.id.shareButton:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);

                // type of the content to be shared
                sharingIntent.setType("text/plain");

                // Body of the content
                String shareBody = "Share your stats with friends!";

                // subject of the content. you can share anything
                String shareSubject = "Share stats";

                // passing body of the content
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);

                // passing subject of the content
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
                break;

            default:
                Log.d(TAG, "Button could not be found.");
        }
        return super.onOptionsItemSelected(item);
    }
}