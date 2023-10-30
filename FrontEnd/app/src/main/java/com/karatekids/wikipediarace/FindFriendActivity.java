package com.karatekids.wikipediarace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class FindFriendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);

        findViewById(R.id.validate_friend_id_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView friend_id = (TextView) findViewById(R.id.friend_id_bar);

                //TODO: check if friend id is valid and friend is playing
                Intent friendLobby = new Intent(FindFriendActivity.this, LobbyActivity.class);
                friendLobby.putExtra("game_mode","friend");
                startActivity(friendLobby);
                //else
                //friend_id.setError("Please enter a valid friend id string.");
            }
        });
    }
}