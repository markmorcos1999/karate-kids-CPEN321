package com.karatekids.wikipediarace;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

//This class lets you send login data to the server.
//I don't know why I did this the hard way. @TODO
public final class NetworkMessage {

    private static JSONObject message;
    private static JSONObject data;

    //ChatGPT usage: No
    public static JSONObject gameRequest(String name, String id, String gameMode) { // private constructor

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
        return data;
    }
    //ChatGPT usage: No
    public static JSONObject friendGameRequest(String name, String id, String friendId) { // private constructor

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
        return data;
    }
    //
    public static JSONObject friendListRequest(String id) { // private constructor

        try {
            data = new JSONObject();
            data.put("id", id);
        }
        catch(JSONException e) {
        }
        return data;
    }
    //ChatGPT usage: No
    public static JSONObject statsRequest(String name, String id){
        try {
            data = new JSONObject();
            data.put("name", name);
            data.put("id", id);
            StringBuilder str = new StringBuilder();
            str.append("player/");
            str.append(id);
            data.put("subject", str.toString());
            data.put("method", "GET");
        }
        catch(JSONException e) {
        }
        return data;
    }
    //ChatGPT usage: No
    public static JSONObject pagePost(String name, String id, String URL){
        try {
            data = new JSONObject();
            data.put("name", name);
            data.put("id", id);
            data.put("URL", URL);
            data.put("subject", "game");
            data.put("method", "PUT");
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
    //ChatGPT usage: No
    public static JSONObject leaderboardRequest(){
        try {
            data = new JSONObject();
            data.put("subject", "leaderboard");
            data.put("method","GET");
        }
        catch(JSONException e) {
        }
        return data;
    }

    //ChatGPT usage: No
    public static JSONObject signInMessage(String name, String id, String token){
        try {
            data = new JSONObject();
            data.put("name", name);
            data.put("id", id);
            data.put("token", token);
            StringBuilder str = new StringBuilder();
            str.append("signIn/");
            str.append(id);
            data.put("subject", str.toString());
            data.put("method","POST");
        }
        catch(JSONException e) {
        }
        return data;
    }

    //ChatGPT usage: No
    public static JSONObject endGame(String name, String id){
        try {
            data = new JSONObject();
            data.put("name", name);
            data.put("id", id);
            StringBuilder str = new StringBuilder();
            str.append("game/");
            str.append(id);
            data.put("subject", str.toString());
            data.put("method", "GET");
        }
        catch(JSONException e) {
        }
        return data;
    }

    public static JSONObject addFriend(String name, String id, String friendId){
        try {
            data = new JSONObject();
            data.put("name", name);
            data.put("id", id);
            StringBuilder str = new StringBuilder();
            str.append("player/");
            str.append(id);
            str.append("/friend/");
            str.append(friendId);
            data.put("subject", str.toString());
            data.put("method","POST");
        }
        catch(JSONException e) {
        }
        return data;
    }
    public static JSONObject removeFriend(String name, String id, String friendId){
        try {
            data = new JSONObject();
            data.put("name", name);
            data.put("id", id);
            StringBuilder str = new StringBuilder();
            str.append("player/");
            str.append(id);
            str.append("/friend/");
            str.append(friendId);
            data.put("subject", str.toString());
            data.put("method","DELETE");
        }
        catch(JSONException e) {
        }
        return data;
    }

    public static JSONObject getFriends(String name, String id){
        try {
            data = new JSONObject();
            data.put("name", name);
            data.put("id", id);
            StringBuilder str = new StringBuilder();
            str.append("player/");
            str.append(id);
            str.append("/friend");
            data.put("subject", str.toString());
            data.put("method","GET");
        }
        catch(JSONException e) {
        }
        return data;
    }

}
