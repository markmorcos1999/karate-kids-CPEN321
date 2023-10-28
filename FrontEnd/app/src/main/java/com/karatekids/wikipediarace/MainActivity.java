package com.karatekids.wikipediarace;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "MainActivity";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.daily_challenge_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lobbyIntent = new Intent(MainActivity.this, LobbyActivity.class);
                lobbyIntent.putExtra("game_mode","daily");
                startActivity(lobbyIntent);
            }
        });

        findViewById(R.id.single_player_game_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lobbyIntent = new Intent(MainActivity.this, LobbyActivity.class);
                lobbyIntent.putExtra("game_mode","single");
                startActivity(lobbyIntent);
            }
        });

        findViewById(R.id.multi_player_game_st).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lobbyIntent = new Intent(MainActivity.this, LobbyActivity.class);
                lobbyIntent.putExtra("game_mode","multi");
                startActivity(lobbyIntent);
            }
        });

        findViewById(R.id.stats_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Networker.getPlayerStats(MainActivity.this);

            }
        });

        findViewById(R.id.leaderboard_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Networker.getLeaderboard(MainActivity.this);
            }
        });
    }

    public void goToStats(String data){
        Intent statisticsIntent = new Intent(MainActivity.this, StatisticsActivity.class);
        statisticsIntent.putExtra("playerStats", data);
        Log.d(TAG, data);
        startActivity(statisticsIntent);
    }

    public void goToLeaderboard(String data){
        Intent leaderboardIntent = new Intent(MainActivity.this, LeaderboardActivity.class);
        leaderboardIntent.putExtra("leaderboardData", data);
        Log.d(TAG, data);
        startActivity(leaderboardIntent);
    }

    // https://stackoverflow.com/questions/18404271/android-back-button-to-specific-activity#:~:text=If%20you%20need%20to%20go%20back%20what%20ever,Your%20intent%20here%20%2F%2F%20%2F%2F%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2F%2F%20return%20true%3B%20%7D
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(MainActivity.this, MainActivity.class));
        finish();

    }
}