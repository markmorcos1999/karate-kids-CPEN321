package com.karatekids.wikipediarace;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button connectButton;
    final static String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectButton = findViewById(R.id.network_button);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){


                Networker.connectToServer();

                Log.d(TAG, "Trying to network!");

            }
        });
    }
}