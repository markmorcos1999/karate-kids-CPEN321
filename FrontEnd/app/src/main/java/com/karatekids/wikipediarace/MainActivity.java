package com.karatekids.wikipediarace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    final static String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.game_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lobbyIntent = new Intent(MainActivity.this, LobbyActivity.class);
                startActivity(lobbyIntent);
            }
        });

        findViewById(R.id.stats_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent statisticsIntent = new Intent(MainActivity.this, StatisticsActivity.class);
                startActivity(statisticsIntent);
            }
        });

        findViewById(R.id.leaderboard_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent leaderboardIntent = new Intent(MainActivity.this, LeaderboardActivity.class);
                startActivity(leaderboardIntent);
            }
        });
    }
}