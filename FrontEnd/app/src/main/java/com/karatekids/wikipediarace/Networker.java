package com.karatekids.wikipediarace;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketImpl;
import java.net.Socket;
import java.net.URL;

public final class Networker {

    private static String URL = "https://milestone1.canadacentral.cloudapp.azure.com:8081";
    final static String TAG = "Networker";
    private Networker () { // private constructor

    }
    public static void connectToServer(ConnectionData dat) {
        //using a new thread to do the http request, as per suggestion in https://stackoverflow.com/questions/6343166/how-can-i-fix-android-os-networkonmainthreadexception
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, executePost(URL, dat.getMessage()));

                //Log.d(TAG, executePost("https://milestone1.canadacentral.cloudapp.azure.com:8081", "date"));
            }
        });

        thread.start();

    }

    //This method comes from https://stackoverflow.com/questions/1359689/how-to-send-http-request-in-java
    //To help with executing a post
    public static String executePost(String targetURL, String urlParameters) {
        HttpURLConnection connection = null;

        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length",
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }


    }

}
