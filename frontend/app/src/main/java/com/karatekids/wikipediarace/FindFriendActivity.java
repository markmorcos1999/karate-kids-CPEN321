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
                friendLobby.putExtra("friend_id", friend_id.getText().toString());
                startActivity(friendLobby);
            }
        });

        TextView player_id = (TextView) findViewById(R.id.user_id_value_tv);
        player_id.setText(Networker.getId());
    }

    // https://stackoverflow.com/questions/18404271/android-back-button-to-specific-activity#:~:text=If%20you%20need%20to%20go%20back%20what%20ever,Your%20intent%20here%20%2F%2F%20%2F%2F%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2F%2F%20return%20true%3B%20%7D
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(FindFriendActivity.this, PlayGameActivity.class));
        finish();

    }
}