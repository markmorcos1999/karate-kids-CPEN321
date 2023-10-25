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


//This code uses some references from stack ovefrflow:
//using a new thread to do the http request, as per suggestion in https://stackoverflow.com/questions/6343166/how-can-i-fix-android-os-networkonmainthreadexception

public final class Networker {

    private static String URL = "https://milestone1.canadacentral.cloudapp.azure.com:8081";
    private static String networkId;

    private static String id = "0";
    private static String name = "";

    final static String TAG = "Networker";
    private Networker () { // private constructor

    }


    //Here we want to take the sign in info from google and put it in here @TODO
    public static void serverSignIn(String _id){
        id = _id;
    }

    public static void requestGame() {
         makePost(NetworkMessage.gameRequest(name, id));

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String ret = executePost(URL, NetworkMessage.gameRequest(name, id));
                //Here change to the player game activity @TODO
            }
        });

        thread.start();

    }

    public static void getPlayerStats(){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String ret = executePost(URL, NetworkMessage.statsRequest(name, id));
                //Here change to the player stats activity @TODO
            }
        });

        thread.start();
    }

    private static void makePost(String data){



    }

    //This method comes from https://stackoverflow.com/questions/1359689/how-to-send-http-request-in-java
    //To help with executing a post
    private static String executePost(String targetURL, String urlParameters) {
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
