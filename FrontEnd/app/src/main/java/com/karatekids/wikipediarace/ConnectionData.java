package com.karatekids.wikipediarace;

import android.net.Network;

import org.json.JSONException;
import org.json.JSONObject;

//This class lets you send login data to the server.
//I don't know why I did this the hard way. @TODO
public class ConnectionData implements NetworkableMessage {

    public JSONObject message;
    public JSONObject data;
    public ConnectionData (String _name) { // private constructor

       try {
           data = new JSONObject();
           data.put("name", _name);
           message = new JSONObject();
           message.put("subject", "connect");
           message.put("data", data);
       }
       catch(JSONException e) {
       }

    }

    public String getMessage(){
        return message.toString();
    };
}
