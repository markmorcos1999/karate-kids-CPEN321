package com.karatekids.wikipediarace;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LobbyActivity extends AppCompatActivity {

    private final static String TAG = "LobbyActivity";

    private Handler handler;
    private final static String MULTIPLAYER_MODE = "multi";

    private final static String SINGLEPLAYER_MODE = "single";

    private final static String FRIEND_MODE = "friend";

    private final static String DAILY_MODE = "daily";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        if(MULTIPLAYER_MODE.equals(b.getString("game_mode"))) {
            setContentView(R.layout.activity_multi_player_lobby);
        }
        else if(SINGLEPLAYER_MODE.equals(b.getString("game_mode"))) {
            setContentView(R.layout.activity_single_player_lobby);
        }
        else if(FRIEND_MODE.equals(b.getString("game_mode"))) {
            setContentView(R.layout.activity_friend_lobby);
        }
        else if(DAILY_MODE.equals(b.getString("game_mode"))) {
            setContentView(R.layout.activity_daily_challenge_lobby);
        }

        //TODO: remove button for starting game and automatically start game when all players have joined
        //----- send request to join game----
        Networker.requestGame(b.getString("game_mode"), LobbyActivity.this);


        //---- receive request to join game -----

        //---- receive response that no other players are waiting in lobby ----


    }

    public void matchFound(String data){

    //Json string with list of players, ready to start game and intent
        try {
            JSONObject obj = new JSONObject(data);
            String startPageTitle =  obj.getString("startTitle");
            String endPageTitle = obj.getString("endTitle");
            String startPageUrl = obj.getString("startPage");
            String endPageUrl = obj.getString("endPage");
            ArrayList<String> playerNames = new ArrayList<String>();
            ArrayList<String> playerElos = new ArrayList<String>();
            JSONArray playerArray = obj.getJSONArray("players");

            StringBuilder sb = new StringBuilder();

            for(int i=0;i<playerArray.length();i++) {
                JSONObject playerObj = playerArray.getJSONObject(i);
                String playerName = playerObj.getString("username");
                String playerElo = playerObj.getString("elo");

                sb.append("Playername: ");
                sb.append(playerName);
                sb.append(" Elo: ");
                sb.append(playerElo);
                sb.append("\n");

                playerNames.add(playerName);
                playerElos.add(playerElo);
            }

            // https://stackoverflow.com/questions/7607410/finish-activity-after-toast-message-disappears
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //to hide loading progress bar:
                    findViewById(R.id.loading_pb).setVisibility(View.GONE);

                    Bundle b = getIntent().getExtras();
                    if(b.getString("game_mode").equals("multi")) {
                        TextView match_found = (TextView) findViewById(R.id.match_found_text);
                        match_found.setVisibility(View.VISIBLE);
                        match_found.setText("Match Found!");
                    }
                    else if(b.getString("game_mode").equals("single")) {
                        TextView pages_found = (TextView) findViewById(R.id.pages_found_text);
                        pages_found.setVisibility(View.VISIBLE);
                        pages_found.setText("Pages Found!");
                    }
                    else if(b.getString("game_mode").equals("daily")) {
                        TextView pages_found = (TextView) findViewById(R.id.daily_pages_found);
                        pages_found.setVisibility(View.VISIBLE);
                        pages_found.setText("Daily Pages Found!");
                    }
                    else if(b.getString("game_mode").equals("friend")) {
                        TextView friend_found = (TextView) findViewById(R.id.daily_pages_found);
                        friend_found.setVisibility(View.VISIBLE);
                        friend_found.setText("Friend Found!");
                    }

                    Toast.makeText(getApplicationContext(),sb.toString() +"Starting Page: "+startPageTitle+"\n"+"Destination Page: "+endPageTitle,Toast.LENGTH_LONG).show();

                    handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            Intent startGameIntent = new Intent(LobbyActivity.this, InGameActivity.class)
                                    .putExtra("start_page", startPageTitle)
                                    .putExtra("end_page", endPageTitle)
                                    .putExtra("start_url", startPageUrl)
                                    .putExtra("end_url", endPageUrl);
                            startActivity(startGameIntent);
                        }
                    };
                    handler.postDelayed(runnable, 3500);
                }
            });

            //TODO: add toasts for start page end page and list of opponents
        }
        catch (JSONException e){

        }
    }

    // https://stackoverflow.com/questions/18404271/android-back-button-to-specific-activity#:~:text=If%20you%20need%20to%20go%20back%20what%20ever,Your%20intent%20here%20%2F%2F%20%2F%2F%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2F%2F%20return%20true%3B%20%7D
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if(handler != null)
            handler.removeCallbacksAndMessages(null);
        startActivity(new Intent(LobbyActivity.this, MainActivity.class));
        finish();
    }

}
