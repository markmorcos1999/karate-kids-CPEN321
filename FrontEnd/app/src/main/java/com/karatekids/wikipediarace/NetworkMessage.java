package com.karatekids.wikipediarace;

import org.json.JSONException;
import org.json.JSONObject;

//This class lets you send login data to the server.
//I don't know why I did this the hard way. @TODO
public final class NetworkMessage {

    private static JSONObject message;
    private static JSONObject data;


    public static String gameRequest(String _name, String _id) { // private constructor

       try {
           data = new JSONObject();
           data.put("name", _name);
           data.put("id", _id);
           message = new JSONObject();
           message.put("subject", "connect");
           message.put("data", data);
       }
       catch(JSONException e) {
       }
        return message.toString();
    }

    public static String statsRequest(String name,String _id){
        try {
            data = new JSONObject();
            data.put("name", name);
            data.put("id", _id);
            message = new JSONObject();
            message.put("subject", "connect");
            message.put("data", data);
        }
        catch(JSONException e) {
        }
        return message.toString();
    }

}
