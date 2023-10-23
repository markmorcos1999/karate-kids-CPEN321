package com.karatekids.wikipediarace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class InGameActivity extends AppCompatActivity {

    public static int count;
    private final static String TAG = "MainActivity";

    // Followed along with: https://technotalkative.com/android-webviewclient-example/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);



        TextView textView = (TextView) findViewById(R.id.destination_page);

        //TODO: replace with the randomly determined destination page
        textView.append(" Mexico");

        WebView web;
        web = (WebView) findViewById(R.id.wikipedia_page_view);
        web.setWebViewClient(new myWebClient());
        web.getSettings().setJavaScriptEnabled(true);
        web.loadUrl("https://en.m.wikipedia.org/wiki/Taco");
        count = -1;

        //TODO: move to results activity when the player reaches the destination page
    }

    public class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        // display the information from the url embedded in the app instead of opening a web viewer external application
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(String.valueOf(request.getUrl()));
            return true;
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            count++;

            Log.d(TAG, "The number of clicked links is: " + count);

            //check if user reaches destination page
            //TODO: change this to take the destination page given from the server
            if(url.equals("https://en.m.wikipedia.org/wiki/Mexico")){
                Intent resultIntent = new Intent(InGameActivity.this, ResultsActivity.class);
                startActivity(resultIntent);
            }
        }
    }
}