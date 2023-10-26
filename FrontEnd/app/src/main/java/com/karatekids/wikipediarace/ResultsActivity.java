package com.karatekids.wikipediarace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Bundle b = getIntent().getExtras();

        findViewById(R.id.return_main_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(ResultsActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });

        TextView num_links = (TextView) findViewById(R.id.links_num_text);
        num_links.append(" "+b.getInt("count"));
        num_links.append("\n");

        ArrayList<String> pagesVisited = b.getStringArrayList("visited_list");

        TextView paths_taken = (TextView) findViewById(R.id.path_taken_text);
        paths_taken.append("\n");
        for(int i=0;i<pagesVisited.size();i++){
            paths_taken.append("\n");
            paths_taken.append(pagesVisited.get(i));
            if(i+1==pagesVisited.size()){
                break;
            }
            paths_taken.append("\n");
            paths_taken.append("|");
            paths_taken.append("\n");
            paths_taken.append("V");
        }

        paths_taken.append("\n");

        TextView position = (TextView) findViewById(R.id.placement_text);

        position.append(" 3rd\n");

        TextView time_taken = (TextView) findViewById(R.id.time_text);

        int seconds = b.getInt("time");

        time_taken.append(" "+seconds);

        time_taken.append("\n");



        //TODO: show the results from the page and how the player fared
    }
}