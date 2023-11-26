package com.karatekids.wikipediarace;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.Chronometer;

import static com.karatekids.wikipediarace.InGameActivity.endGame;

public class CustomWebViewClient extends WebView {

    public static Bundle b;
    public static Chronometer c;

    public static Activity a;

    public CustomWebViewClient(Context context) {
        super(context);
    }

    public CustomWebViewClient(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomWebViewClient(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void loadUrl(String str) {
        if(str.equals(b.getString("end_url"))){
            c.stop();
            endGame(a);
        }
        super.loadUrl(str);
    }

    public static void setBundle(Bundle bundle, Chronometer clock, Activity activity) {
        a = activity;
        b = bundle;
        c = clock;
    }
}
