package com.karatekids.wikipediarace;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Stack;

public class InGameActivity extends AppCompatActivity {

    public static Context gameContext;
    private static int count;

    private static ArrayList<String> pagesVisited;

    private static Stack<String> lastVisitedPages;

    private final static String TAG = "InGameActivity";

    public Bundle b;

    private static Chronometer clock;

    private WebView web;

    //ChatGPT usage: No
    // Followed along with: https://technotalkative.com/android-webviewclient-example/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);
        gameContext = InGameActivity.this;

        Bundle b = getIntent().getExtras();

        //TODO: use server to randomly get pages

        Button quit_game = (Button) findViewById(R.id.quit_game_bt);

        quit_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endGame(InGameActivity.this);
            }
        });

        //TODO: replace with the randomly determined destination page
        TextView destination = (TextView)  findViewById(R.id.destination_page);
        destination.append(" "+b.getString("end_page"));

        web = (WebView) findViewById(R.id.wikipedia_page_view);
        MyWebClient c = new MyWebClient();
        web.setWebViewClient(c);
        web.getSettings().setJavaScriptEnabled(true);
        web.setVisibility(View.INVISIBLE);
        web.loadUrl(b.getString("start_url"));

        count = -1;
        pagesVisited = new ArrayList<>();
        lastVisitedPages = new Stack<>();

        // https://medium.com/native-mobile-bits/easily-build-a-chronometer-a-simple-stopwatch-1f83aa361ee7
        clock = (Chronometer) findViewById(R.id.chronometer);
        clock.setBase(SystemClock.elapsedRealtime());
        clock.start();
    }

    //ChatGPT usage: No
    private void displayLostConnectionPopup (WebView view,WebResourceRequest request) {
        ContextCompat.getMainExecutor(InGameActivity.this).execute(() -> {
            AlertDialog.Builder  builder = new AlertDialog.Builder(InGameActivity.this);
            builder.setTitle("Network Issue");
            builder.setMessage("Please check your internet connection and try again later");
            builder.setCancelable(false);
            builder.setNegativeButton("Retry", (DialogInterface.OnClickListener) (dialog, which) -> {
                view.getWebViewClient().shouldOverrideUrlLoading(view, request);
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }

    public class MyWebClient extends WebViewClient {
        //ChatGPT usage: No
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            web.loadUrl("javascript:(function() { " +
                    "document.getElementsByClassName ('header-container header-chrome')[0].style.display='none';" +"})()");
        }

        //ChatGPT usage: No
        //        // display the information from  in the app instead of opening a web viewer external application
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (!DetectConnection.checkInternetConnection(InGameActivity.this)) {
                displayLostConnectionPopup(view, request);
            }
            else {
                if (!request.getUrl().getHost().equals("en.m.wikipedia.org")) {
                    return true;
                }
                Log.d(TAG, "URL host: " + request.getUrl());
                view.setVisibility(View.INVISIBLE);
                view.loadUrl(String.valueOf(request.getUrl()));}
            return true;
        }

        //ChatGPT usage: No
        @Override
        public void onPageCommitVisible(WebView view, String url) {
            Networker.sendPage(url, InGameActivity.this);

            count++;

            Log.d(TAG, "The number of clicked links is: " + count);

            pagesVisited.add(view.getTitle().substring(0, view.getTitle().indexOf("-")-1));
            lastVisitedPages.push(view.getUrl());

            b = getIntent().getExtras();
            //check if user reaches destination page
            //TODO: change this to take the destination page given from the server
            if(url.equals(b.getString("end_url"))){
            }
        }

        //ChatGPT usage: No
        @Override
        public void onPageFinished(WebView view, String url) {
            view.loadUrl("javascript:(function(){" +
                    "$('.header-container.header-chrome').remove();" +
                    "})()");
            super.onPageFinished(view, url);
            view.setVisibility(View.VISIBLE);
        }
    }

    //ChatGPT usage: No
    public static void endGame(Context context){
        Networker.endGame(context);
    }

    //ChatGPT usage: No
    public static void updateResults(Context context, String data){
        clock.stop();
        Intent resultIntent = new Intent(context, ResultsActivity.class)
                .putExtra("count", count)
                .putExtra("time", clock.getText().toString())
                .putExtra("visited_list", pagesVisited)
                .putExtra("data",data);
        Log.d(TAG, "Time is: " + clock.getText().toString());
        Log.d(TAG, data);
        context.startActivity(resultIntent);
    }

    //ChatGPT usage: No
    // https://stackoverflow.com/questions/18404271/android-back-button-to-specific-activity#:~:text=If%20you%20need%20to%20go%20back%20what%20ever,Your%20intent%20here%20%2F%2F%20%2F%2F%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2F%2F%20return%20true%3B%20%7D
    @Override
    public void onBackPressed()
    {
        if(lastVisitedPages.size()>1) {
            lastVisitedPages.pop();
            String s = lastVisitedPages.pop();
            web.setVisibility(View.INVISIBLE);
            web.loadUrl(s);
        }
        else {
            startActivity(new Intent(InGameActivity.this, PlayGameActivity.class));
            finish();
        }
    }
}

