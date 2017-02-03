package com.example.user.suivezbouddhaandroid;

import android.util.Log;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 * Created by user on 16/12/16.
 */

public class Client extends Observable {
    private Socket mSocket;
    private Boolean isConnected;
    private final String serverAddress = "http://10.212.126.143:8080/";//http://10.212.109.188:8080/
    private String message ;
    private JSONObject position;
    private JSONObject directions;
    private boolean finish;
    //private HashMap<String, ArrayList<String>> rooms;
    private JSONArray rooms;

    public Client(){
        isConnected = false;
        try {
            mSocket = IO.socket(serverAddress);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        position = new JSONObject();
        try {
            position.put("id", new Integer(0));
            position.put("position", "0-0");
            position.put("floor", new Integer(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.on(Socket.EVENT_CONNECT,onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("message", onMessage);
        mSocket.on("newPosition", onNewPosition);
        mSocket.on("newDirection", onNewDirection);
        mSocket.on("roomWithEvent", onAllRooms);
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            isConnected = true;
            Log.d("->","---------> Connect");
            setChanged();
            notifyObservers();
            clearChanged();
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            isConnected = false;
            Log.d("->","---------> Disconnect");
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            isConnected = false;
            Log.d("->", "Error or timeout");
        }
    };

    private Emitter.Listener onNewPosition = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("->", "---------> New position : " + args[0]);
            try {
                position = new JSONObject(args[0].toString());
                setChanged();
                notifyObservers();
                clearChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    private Emitter.Listener onNewDirection = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("->","---------> New direction : "+ args[0]);
            try {
                directions = new JSONObject(args[0].toString());
                finish = (boolean) args[1];
                setChanged();
                notifyObservers();
                clearChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onAllRooms = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("->","---------> New message : "+ args[0]);
            message = args[0].toString();
            JSONArray jsonAnswer = null;
            try {
                jsonAnswer = new JSONArray(args[0].toString());
                rooms = jsonAnswer;
                setChanged();
                notifyObservers();
                clearChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    private Emitter.Listener onMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("->","---------> New message : "+ args[0]);
            message = args[0].toString();
        }
    };

    public String getMessage() {
        return message;
    }

    public void sendMessage(String message)
    {
        if (isConnected)
        {
            Log.d("->","---------> Sending message : "+ message);
            mSocket.emit("message", message);
        }

    }

    public void askDirection(String idRomm, String id)
    {
        if (isConnected)
        {
            Log.d("->","---------> Asking direction for id : "+ id);
            mSocket.emit("askDirection", idRomm, id);
        }
    }

    public void askPosition(String id)
    {
        if (isConnected)
        {
            Log.d("->","---------> Asking position for id : "+ id);
            mSocket.emit("askPosition", id);
        }
    }

    public void askAllRooms()
    {
        if (isConnected)
        {
            Log.d("->","---------> Asking for all rooms");
            mSocket.emit("getRoomWithEvent");
        }
    }

    public void connect()
    {
        mSocket.connect();
    }

    public JSONArray getRooms(){ return rooms;}

    public boolean isConnected() { return isConnected;}

    public JSONObject getDirections() {
        return directions;
    }

    public JSONObject getPosition() {
        return position;
    }

    public boolean isFinish() {
        return finish;
    }
}