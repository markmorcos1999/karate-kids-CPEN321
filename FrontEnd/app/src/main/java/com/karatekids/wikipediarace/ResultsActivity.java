package com.karatekids.wikipediarace;

import static com.karatekids.wikipediarace.InGameActivity.count;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        findViewById(R.id.return_main_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(ResultsActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });

        TextView num_links = (TextView) findViewById(R.id.links_num_text);
        num_links.append(" "+String.valueOf(count));

        //TODO: show the results from the page and how the player fared
    }
}