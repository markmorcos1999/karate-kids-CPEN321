package com.karatekids.wikipediarace;

import org.json.JSONException;
import org.json.JSONObject;

//This class lets you send login data to the server.
//I don't know why I did this the hard way. @TODO
public final class NetworkMessage {

    private static JSONObject message;
    private static JSONObject data;

    //ChatGPT usage: No
    public static String gameRequest(String name, String id, String gameMode) { // private constructor

       try {
           data = new JSONObject();
           data.put("name", name);
           data.put("id", id);
           data.put("mode", gameMode);
           data.put("subject", "game");
           data.put("method", "POST");
       }
       catch(JSONException e) {
       }
        return data.toString();
    }
    //ChatGPT usage: No
    public static String friendGameRequest(String name, String id, String friendId) { // private constructor

        try {
            data = new JSONObject();
            data.put("name", name);
            data.put("id", id);
            data.put("friendId", friendId);
            data.put("mode", "friend");
            data.put("subject", "game");
            data.put("method", "POST");
        }
        catch(JSONException e) {
        }
        return data.toString();
    }
    //ChatGPT usage: No
    public static String statsRequest(String name, String id){
        try {
            data = new JSONObject();
            data.put("name", name);
            data.put("id", id);
            data.put("subject", "player");
            data.put("method", "GET");
        }
        catch(JSONException e) {
        }
        return data.toString();
    }
    //ChatGPT usage: No
    public static String pagePost(String name, String id, String URL){
        try {
            data = new JSONObject();
            data.put("name", name);
            data.put("id", id);
            data.put("URL", URL);
            data.put("subject", "game");
            data.put("method", "GET");
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        return message.toString();
    }
    //ChatGPT usage: No
    public static String leaderboardRequest(){
        try {
            data = new JSONObject();
            data.put("subject", "leaderboard");
            data.put("method","GET");
        }
        catch(JSONException e) {
        }
        return data.toString();
    }

    //ChatGPT usage: No
    public static String signInMessage(String name, String id, String token){
        try {
            data = new JSONObject();
            data.put("name", name);
            data.put("id", id);
            data.put("token", token);
            data.put("subject", "signIn");
            data.put("method","GET");
        }
        catch(JSONException e) {
        }
        return data.toString();
    }

    //ChatGPT usage: No
    public static String endGame(String name, String id){
        try {
            data = new JSONObject();
            data.put("name", name);
            data.put("id", id);
            message = new JSONObject();
            message.put("subject", "endGame");
            message.put("data", data);
        }
        catch(JSONException e) {
        }
        return message.toString();
    }

}
