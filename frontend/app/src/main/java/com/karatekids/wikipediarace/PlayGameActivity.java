package com.karatekids.wikipediarace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PlayGameActivity extends AppCompatActivity {

    //ChatGPT usage: No
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        findViewById(R.id.daily_challenge_bt).setOnClickListener(new View.OnClickListener() {
            //ChatGPT usage: No
            @Override
            public void onClick(View v) {
                Intent lobbyIntent = new Intent(PlayGameActivity.this, LobbyActivity.class);
                lobbyIntent.putExtra("game_mode","daily");
                startActivity(lobbyIntent);
            }
        });

        findViewById(R.id.single_player_game_bt).setOnClickListener(new View.OnClickListener() {
            //ChatGPT usage: No
            @Override
            public void onClick(View v) {
                Intent lobbyIntent = new Intent(PlayGameActivity.this, LobbyActivity.class);
                lobbyIntent.putExtra("game_mode","single");
                startActivity(lobbyIntent);
            }
        });

        findViewById(R.id.multi_player_game_st).setOnClickListener(new View.OnClickListener() {
            //ChatGPT usage: No
            @Override
            public void onClick(View view) {
                Intent lobbyIntent = new Intent(PlayGameActivity.this, LobbyActivity.class);
                lobbyIntent.putExtra("game_mode","multi");
                startActivity(lobbyIntent);
            }
        });

        findViewById(R.id.friend_bt).setOnClickListener(new View.OnClickListener() {
            //ChatGPT usage: No
            @Override
            public void onClick(View view) {
                Networker.getFriends();
                Intent findFriendIntent = new Intent(PlayGameActivity.this, FindFriendActivity.class);
                startActivity(findFriendIntent);
            }
        });
    }

    //ChatGPT usage: No
    // https://stackoverflow.com/questions/18404271/android-back-button-to-specific-activity#:~:text=If%20you%20need%20to%20go%20back%20what%20ever,Your%20intent%20here%20%2F%2F%20%2F%2F%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2F%2F%20return%20true%3B%20%7D
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(PlayGameActivity.this, MainActivity.class));
        finish();
    }
}