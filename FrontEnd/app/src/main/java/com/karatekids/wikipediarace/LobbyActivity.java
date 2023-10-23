package com.karatekids.wikipediarace;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class LobbyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        //----- send request to join game----

        //---- receive request to join game -----

        //---- receive response that no other players are waiting in lobby ----

        //to hide loading progress bar:
        // findViewById(R.id.loading_pb).setVisibility(View.GONE);
    }

}
