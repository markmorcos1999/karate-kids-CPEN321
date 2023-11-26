package com.karatekids.wikipediarace;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

    private static CustomWebViewClient web;

    //ChatGPT usage: No
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

        web = (CustomWebViewClient) findViewById(R.id.wikipedia_page_view);
        web.setWebViewClient(new MyWebClient());
        web.getSettings().setJavaScriptEnabled(true);
        CustomWebViewClient.setBundle(b, clock, InGameActivity.this);
        web.loadUrl(b.getString("start_url"));
        count = -1;
        pagesVisited = new ArrayList<>();
        lastVisitedPages = new Stack<>();

        //https://stackoverflow.com/questions/18221728/handle-network-issues-in-webview
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(final WebView view, int errorCode, String description,
                                        final String failingUrl) {
                //control you layout, show something like a retry button, and
                //call view.loadUrl(failingUrl) to reload.
                displayLostConnectionPopup(failingUrl);
            }
        });

        // https://medium.com/native-mobile-bits/easily-build-a-chronometer-a-simple-stopwatch-1f83aa361ee7
        clock = (Chronometer) findViewById(R.id.chronometer);
        clock.setBase(SystemClock.elapsedRealtime());
        clock.start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState ) {
        super.onSaveInstanceState(outState);
        web.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        web.restoreState(savedInstanceState);
    }

    public class MyWebClient extends WebViewClient {
        //ChatGPT usage: No
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        //ChatGPT usage: Nothe url embedded
        //        // display the information from  in the app instead of opening a web viewer external application
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if(!request.getUrl().getHost().equals("en.m.wikipedia.org")){
               return true;
            }
            Log.d(TAG, "URL host: "+request.getUrl());
            Networker.sendPage(String.valueOf(request.getUrl()));
            view.loadUrl(String.valueOf(request.getUrl()));
            if(request.getUrl().equals(b.getString("end_url"))){
                clock.stop();
                endGame(InGameActivity.this);
            }
            return true;
        }

        //ChatGPT usage: No
        @Override
        public void onPageCommitVisible(WebView view, String url) {
            count++;

            Log.d(TAG, "The number of clicked links is: " + count);

            pagesVisited.add(view.getTitle().substring(0, view.getTitle().indexOf("-")-1));
            lastVisitedPages.push(view.getUrl());

            b = getIntent().getExtras();
            CustomWebViewClient.setBundle(b, clock, InGameActivity.this);
            //check if user reaches destination page
            //TODO: change this to take the destination page given from the server

        }
    }

    //ChatGPT usage: No
    public static void endGame(Context context){
        Networker.endGame(context);
    }

    //ChatGPT usage: No
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

    //ChatGPT usage: No
    // https://stackoverflow.com/questions/18404271/android-back-button-to-specific-activity#:~:text=If%20you%20need%20to%20go%20back%20what%20ever,Your%20intent%20here%20%2F%2F%20%2F%2F%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2F%2F%20return%20true%3B%20%7D
    @Override
    public void onBackPressed()
    {
        if(lastVisitedPages.size()>1) {
            lastVisitedPages.pop();
            String s = lastVisitedPages.pop();
            web.loadUrl(s);
        }
        else {
            startActivity(new Intent(InGameActivity.this, PlayGameActivity.class));
            finish();
        }
    }

    private void displayLostConnectionPopup (String url) {
        ContextCompat.getMainExecutor(InGameActivity.gameContext).execute(() -> {
            AlertDialog.Builder  builder = new AlertDialog.Builder(InGameActivity.gameContext);
            builder.setTitle("Network Issue");
            builder.setMessage("Please check your internet connection and try again later");
            builder.setCancelable(false);
            builder.setNegativeButton("Retry", (DialogInterface.OnClickListener) (dialog, which) -> {
                web.loadUrl(url);
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }
}

