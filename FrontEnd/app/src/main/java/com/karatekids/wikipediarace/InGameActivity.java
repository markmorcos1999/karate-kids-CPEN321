package com.karatekids.wikipediarace;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Chronometer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class InGameActivity extends AppCompatActivity {

    public static Context gameContext;
    private static int count;

    private static ArrayList<String> pagesVisited;

    private final static String TAG = "InGameActivity";

    private static Chronometer clock;

    // Followed along with: https://technotalkative.com/android-webviewclient-example/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);
        gameContext = InGameActivity.this;

        Bundle b = getIntent().getExtras();

        //TODO: use server to randomly get pages

        //TODO: replace with the randomly determined destination page
        TextView destination = (TextView)  findViewById(R.id.destination_page);
        destination.append(" "+b.getString("end_page"));

        WebView web;
        web = (WebView) findViewById(R.id.wikipedia_page_view);
        web.setWebViewClient(new myWebClient());
        web.getSettings().setJavaScriptEnabled(true);
        web.loadUrl(b.getString("start_url"));
        count = -1;
        pagesVisited = new ArrayList<>();

        // https://medium.com/native-mobile-bits/easily-build-a-chronometer-a-simple-stopwatch-1f83aa361ee7
        clock = (Chronometer) findViewById(R.id.chronometer);
        clock.setBase(SystemClock.elapsedRealtime());
        clock.start();
    }

    public class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        // display the information from the url embedded in the app instead of opening a web viewer external application
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if(!request.getUrl().getHost().equals("en.m.wikipedia.org")){
               return true;
            }
            Log.d(TAG, "URL host: "+request.getUrl().getHost());
            Networker.sendPage(String.valueOf(request.getUrl()));
            view.loadUrl(String.valueOf(request.getUrl()));
            return true;
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            count++;

            Log.d(TAG, "The number of clicked links is: " + count);

            pagesVisited.add(view.getTitle().substring(0, view.getTitle().indexOf("-")));

            Bundle b = getIntent().getExtras();

            //check if user reaches destination page
            //TODO: change this to take the destination page given from the server
            if(url.equals(b.getString("end_url"))){
                clock.stop();
                endGame(InGameActivity.this);
            }
        }
    }

    public static void endGame(Context context){
        Networker.endGame(context);
    }

    public static void updateResults(Context context, String data){
        Intent resultIntent = new Intent(context, ResultsActivity.class)
                .putExtra("count", count)
                .putExtra("time", clock.getText().toString())
                .putExtra("visited_list", pagesVisited)
                .putExtra("data",data);
        Log.d(TAG, "Time is: " + clock.getText().toString());
        Log.d(TAG, data);
        context.startActivity(resultIntent);
    }

    // https://stackoverflow.com/questions/18404271/android-back-button-to-specific-activity#:~:text=If%20you%20need%20to%20go%20back%20what%20ever,Your%20intent%20here%20%2F%2F%20%2F%2F%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2F%2F%20return%20true%3B%20%7D
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(InGameActivity.this, MainActivity.class));
        finish();

    }

}