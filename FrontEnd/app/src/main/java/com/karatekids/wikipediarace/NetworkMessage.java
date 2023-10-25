package com.karatekids.wikipediarace;

import org.json.JSONException;
import org.json.JSONObject;

//This class lets you send login data to the server.
//I don't know why I did this the hard way. @TODO
public final class NetworkMessage {

    private static JSONObject message;
    private static JSONObject data;


    public static String gameRequest(String name, String id) { // private constructor

       try {
           data = new JSONObject();
           data.put("name", name);
           data.put("id", id);
           message = new JSONObject();
           message.put("subject", "gameRequest");
           message.put("data", data);
       }
       catch(JSONException e) {
       }
        return message.toString();
    }

    public static String statsRequest(String name, String id){
        try {
            data = new JSONObject();
            data.put("name", name);
            data.put("id", id);
            message = new JSONObject();
            message.put("subject", "statsRequest");
            message.put("data", data);
        }
        catch(JSONException e) {
        }
        return message.toString();
    }

    public static String pagePost(String name, String id, String URL){
        try {
            data = new JSONObject();
            data.put("name", name);
            data.put("id", id);
            data.put("URL", URL);
            message = new JSONObject();
            message.put("subject", "page");
            message.put("data", data);
        }
        catch(JSONException e) {
        }
        return message.toString();
    }

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
