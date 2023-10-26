package com.karatekids.wikipediarace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "MainActivity";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
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
}