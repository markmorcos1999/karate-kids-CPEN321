package com.karatekids.wikipediarace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {

    private final static String TAG = "ResultsActivity";

    //ChatGPT usage: No
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Bundle b = getIntent().getExtras();

        Log.d(TAG, b.getString("data"));

        findViewById(R.id.return_main_bt).setOnClickListener(new View.OnClickListener() {
            //ChatGPT usage: No
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
        if(pagesVisited != null) {
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
        }

        paths_taken.append("\n");

        TextView position = (TextView) findViewById(R.id.placement_text);

        String JSON_String = b.getString("data");

        try {
            JSONObject obj = new JSONObject(JSON_String);
            String pos = obj.getString("gamePosition");
            position.append(" "+pos);

            JSONArray shortest_path = obj.getJSONArray("shortestPath");
            TextView shortest_path_text = (TextView) findViewById(R.id.shortest_path_text);

            shortest_path_text.append("\n");

            if(shortest_path != null) {
                for(int i=0;i<shortest_path.length();i++){
                    shortest_path_text.append("\n");
                    shortest_path_text.append(shortest_path.getString(i).toString());
                    if(i+1==shortest_path.length()){
                        break;
                    }
                    shortest_path_text.append("\n");
                    shortest_path_text.append("|");
                    shortest_path_text.append("\n");
                    shortest_path_text.append("V");
                }
            }

            shortest_path_text.append("\n");

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        TextView time_taken = (TextView) findViewById(R.id.time_text);

        String time = b.getString("time");

        int seconds = Integer.parseInt(time.substring(0,2))*60+Integer.parseInt(time.substring(3,5));

        time_taken.append(" "+seconds);

        time_taken.append("\n");



        //TODO: show the results from the page and how the player fared
    }

    //ChatGPT usage: No
    // https://stackoverflow.com/questions/18404271/android-back-button-to-specific-activity#:~:text=If%20you%20need%20to%20go%20back%20what%20ever,Your%20intent%20here%20%2F%2F%20%2F%2F%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2F%2F%20return%20true%3B%20%7D
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(ResultsActivity.this, MainActivity.class));
        finish();

    }
}