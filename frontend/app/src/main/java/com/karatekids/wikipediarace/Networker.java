package com.karatekids.wikipediarace;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


//This code uses some references from stack ovefrflow:
//using a new thread to do the http request, as per suggestion in https://stackoverflow.com/questions/6343166/how-can-i-fix-android-os-networkonmainthreadexception

public final class Networker {

    private static String URL = "https://milestone1.canadacentral.cloudapp.azure.com:8081";

    private static String id = "0";
    private static String name = "";

    final static String TAG = "Networker";
    //ChatGPT usage: No
    private Networker () { // private constructor

    }
    //ChatGPT usage: No
    public static String getId(){
        return id;
    }
    //ChatGPT usage: No
    //Here we want to take the sign in info from google and put it in here @TODO
    public static void serverSignIn(String _id, String _name, String token, SignInActivity UI){
        id = _id;
        name = _name;
        Thread thread = new Thread(new Runnable() {
            //ChatGPT usage: No
            @Override
            public void run() {
                executePost(URL, NetworkMessage.signInMessage(name, id, token));
                //Check return if correct
                UI.updateUI(name);
            }
        });

        thread.start();

    }
    //ChatGPT usage: No
    public static void getLeaderboard(MainActivity main){
        Thread thread = new Thread(new Runnable() {
            //ChatGPT usage: No
            @Override
            public void run() {
                String ret = executePost(URL, NetworkMessage.leaderboardRequest());
                main.goToLeaderboard(ret);
            }
        });

        thread.start();
    }
    //ChatGPT usage: No
    public static void requestGame(String gameMode, LobbyActivity lobby) {

        Thread thread = new Thread(new Runnable() {
            //ChatGPT usage: No
            @Override
            public void run() {
                String ret = executePost(URL, NetworkMessage.gameRequest(name, id, gameMode));
                Log.d(TAG, ret);
                lobby.matchFound(ret);
                //Here change to the player game activity @TODO

            }
        });

        thread.start();

    }
    //ChatGPT usage: No
    public static void joinWithFriend(LobbyActivity lobby, String friendId) {

        //Log.d(TAG, NetworkMessage.friendGameRequest(name, id, friendId));
        Thread thread = new Thread(new Runnable() {
            //ChatGPT usage: No
            @Override
            public void run() {
                String ret = executePost(URL, NetworkMessage.friendGameRequest(name, id, friendId));
                Log.d(TAG, ret);
                lobby.matchFound(ret);
                //Here change to the player game activity @TODO

            }
        });

        thread.start();

    }
    //ChatGPT usage: No
    public static void getPlayerStats(MainActivity main){

        Thread thread = new Thread(new Runnable() {
            //ChatGPT usage: No
            @Override
            public void run() {
                String ret = executePost(URL, NetworkMessage.statsRequest(name, id));
                main.goToStats(ret);
            }
        });

        thread.start();
    }
    //ChatGPT usage: No
    //Call this any time
    public static void sendPage(String url, Context context){

        Thread thread = new Thread(new Runnable() {
            //ChatGPT usage: No
            @Override
            public void run() {
                String ret = executePost(URL, NetworkMessage.pagePost(name, id, url));
                try {
                    JSONObject response = new JSONObject(ret);
                    if(response.getBoolean("done")){
                        InGameActivity.updateResults(context, ret);
                    }
                }
                catch (Throwable t){

                }
                //What to do after a post? status code returned?
            }
        });

        thread.start();

    }
    //ChatGPT usage: No
    public static void endGame(Context context){
        Thread thread = new Thread(new Runnable() {
            //ChatGPT usage: No
            @Override
            public void run() {
                String ret = executePost(URL, NetworkMessage.endGame(name, id));
                InGameActivity.updateResults(context, ret);
                //What to do after a post? status code returned?
            }
        });

        thread.start();
    }

    //ChatGPT usage: No
    //Adds a friend, takes in the friends ID
    public static void addFriend(String friendId){
        Thread thread = new Thread(new Runnable() {
            //ChatGPT usage: No
            @Override
            public void run() {
                executePost(URL, NetworkMessage.addFriend(name, id, friendId));
            }
        });

        thread.start();

    }

    //ChatGPT usage: No
    //removes a Friend, takes in the friends ID
    public static void removeFriend(String friendId){
        Thread thread = new Thread(new Runnable() {
            //ChatGPT usage: No
            @Override
            public void run() {
                executePost(URL, NetworkMessage.removeFriend(name, id, friendId));
            }
        });

        thread.start();
    }

    //ChatGPT usage: No
    public static void getFriends(){
        Thread thread = new Thread(new Runnable() {
            //ChatGPT usage: No
            @Override
            public void run() {
                String ret = executePost(URL, NetworkMessage.getFriends(name, id));

                FindFriendActivity.friends = ret;// or something like that.
            }
        });

        thread.start();
    }

    //ChatGPT usage: No
    //This method comes from https://stackoverflow.com/questions/1359689/how-to-send-http-request-in-java
    //To help with executing a post
    private static String executePost(String URL, JSONObject data) {
        HttpURLConnection connection = null;
        String method = "POST";
        String slug = "";
        String urlParameters = data.toString();
       Log.d(TAG, urlParameters);
        StringBuilder str = new StringBuilder();

        try {
            method = data.getString("method");
            slug = data.getString("subject");

            str.append(URL);
            str.append("/");
            str.append(slug);

        }
        catch(JSONException e) {
        }

        String targetURL = str.toString();
        Log.d(TAG, targetURL);
        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type",
                    "application/json");

            //From https://stackoverflow.com/questions/44305351/how-do-you-send-data-in-a-request-body-using-httpurlconnection
            if(!method.equals("GET")) {
                connection.setDoOutput(true);
                OutputStream os = connection.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                osw.write(data.toString());
                osw.flush();
                osw.close();
                os.close();
            }//don't forget to close the OutputStream
            connection.connect();

            Log.d(TAG, String.valueOf(connection.getResponseCode()));

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

            String res = response.toString();
            Log.d(TAG, res);
            //Checking if the response was "try again" in which we resend the request
            if(res.equals("604")){
                return executePost(targetURL, data);
            }

            return res;
        } catch (Exception e) {
            //e.printStackTrace();
            //return "error";
            try{
                Thread.sleep(3000);
            } catch (Exception exception) {}
            return executePost(URL, data);

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
