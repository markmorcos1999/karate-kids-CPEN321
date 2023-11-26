package com.karatekids.wikipediarace;

import android.content.Context;
import android.net.ConnectivityManager;

//ChatGPT usage: No
// https://stackoverflow.com/questions/17959561/android-how-to-prevent-webview-to-load-when-no-internet-connection
public class DetectConnection {
    //ChatGPT usage: No
    public static boolean checkInternetConnection(Context context) {

        ConnectivityManager con_manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return (con_manager.getActiveNetworkInfo() != null
                && con_manager.getActiveNetworkInfo().isAvailable()
                && con_manager.getActiveNetworkInfo().isConnected());
    }
}
