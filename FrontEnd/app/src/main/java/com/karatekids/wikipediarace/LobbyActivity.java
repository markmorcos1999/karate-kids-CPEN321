package com.karatekids.wikipediarace;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LobbyActivity extends AppCompatActivity {

    private final static String TAG = "LobbyActivity";
    private final static String MULTIPLAYER_MODE = "multi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        if(MULTIPLAYER_MODE.equals(b.getString("game_mode"))) {
            setContentView(R.layout.activity_multi_player_lobby);
        }
        else {
            setContentView(R.layout.activity_single_player_lobby);
        }
        findViewById(R.id.start_game_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Networker.requestGame(MULTIPLAYER_MODE, LobbyActivity.this);
                Intent startGameIntent = new Intent(LobbyActivity.this, InGameActivity.class)
                        .putExtra("start_page","Taco")
                        .putExtra("end_page","Mexico")
                        .putExtra("start_url","https://en.m.wikipedia.org/wiki/Taco")
                        .putExtra("end_url","https://en.m.wikipedia.org/wiki/Mexico");
                startActivity(startGameIntent);
            }
        });
        //TODO: remove button for starting game and automatically start game when all players have joined
        //----- send request to join game----
        Networker.requestGame(b.getString("game_mode"), LobbyActivity.this);


        //---- receive request to join game -----

        //---- receive response that no other players are waiting in lobby ----

        //to hide loading progress bar:
        // findViewById(R.id.loading_pb).setVisibility(View.GONE);
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

                    //Toast.makeText(getApplicationContext(),"Players: "+playerNames.toString().substring(1,playerNames.toString().length()-1)+"\n"+"ELOs: "+ playerElos.toString().substring(1,playerNames.toString().length()-1)+"\n"+"Starting Page: "+startPageTitle+"\n"+"Destination Page: "+endPageTitle,Toast.LENGTH_LONG).show();

                    Toast.makeText(getApplicationContext(),sb.toString() +"Starting Page: "+startPageTitle+"\n"+"Destination Page: "+endPageTitle,Toast.LENGTH_LONG).show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent startGameIntent = new Intent(LobbyActivity.this, InGameActivity.class)
                                    .putExtra("start_page", startPageTitle)
                                    .putExtra("end_page", endPageTitle)
                                    .putExtra("start_url", startPageUrl)
                                    .putExtra("end_url", endPageUrl);
                            startActivity(startGameIntent);
                        }
                    }, 3500);
                }

            });

            //TODO: add toasts for start page end page and list of opponents
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    // https://stackoverflow.com/questions/18404271/android-back-button-to-specific-activity#:~:text=If%20you%20need%20to%20go%20back%20what%20ever,Your%20intent%20here%20%2F%2F%20%2F%2F%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2F%2F%20return%20true%3B%20%7D
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(LobbyActivity.this, MainActivity.class));
        finish();
    }

}
