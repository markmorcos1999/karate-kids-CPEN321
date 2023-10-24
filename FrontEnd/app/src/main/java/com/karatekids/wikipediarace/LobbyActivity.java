package com.karatekids.wikipediarace;

import static com.karatekids.wikipediarace.InGameActivity.onClickStart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class LobbyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        findViewById(R.id.start_game_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startGameIntent = new Intent(LobbyActivity.this, InGameActivity.class);
                onClickStart(v);
                startActivity(startGameIntent);
            }
        });

        //TODO: remove button for starting game and automatically start game when all players have joined
    }

}
