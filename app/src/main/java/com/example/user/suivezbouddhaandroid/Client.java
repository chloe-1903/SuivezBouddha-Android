package com.example.user.suivezbouddhaandroid;

import android.graphics.Point;
import android.util.Log;

import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import android.util.JsonReader;

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
    private final String serverAddress = "http://10.212.111.29:8080/";//http://192.168.1.95:8080/
    private String message ;
    private float x;
    private float y;
    private float speed;
    private int direction;
    private int delay;
    private HashMap<Integer, String> rooms;

    public Client(){
        isConnected = false;
        try {
            mSocket = IO.socket(serverAddress);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mSocket.on(Socket.EVENT_CONNECT,onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("message", onMessage);
        mSocket.on("newDirection", onNewDirection);
        mSocket.on("allRooms", onAllRooms);
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            isConnected = true;
            System.out.println("---------> Connect");
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            isConnected = false;
            System.out.println("---------> Disconnect");
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            isConnected = false;
            System.out.println("---------> Error or timeout");
        }
    };

    private Emitter.Listener onNewDirection = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("---------> New position : "+ args[0]);
            try {
                JSONObject jsonAnswer = new JSONObject(args[0].toString());
                String position = jsonAnswer.getString("position");
                System.out.println(jsonAnswer.getString("position"));
                x = Float.valueOf(position.split("-")[0]);
                y = Float.valueOf(position.split("-")[1]);
                JSONObject jsonDirection = jsonAnswer.getJSONObject("direction");
                speed = Float.valueOf(jsonDirection.getString("speed"));
                direction = jsonDirection.getInt("direction");
                delay = jsonDirection.getInt("delay");
                System.out.println("seed : " + speed);
                System.out.println("dir : " + direction);
                System.out.println("del : " + delay);
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
            System.out.println("---------> New message : "+ args[0]);
            message = args[0].toString();
            JSONObject jsonAnswer = null;
            try {
                jsonAnswer = new JSONObject(args[0].toString());
                rooms = new HashMap<>();
                JSONArray jsonRooms = jsonAnswer.getJSONArray("rooms");
                for (int i = 0; i< jsonRooms.length(); i++) {
                    JSONObject jsonRoom = jsonRooms.getJSONObject(i);
                    System.out.println(jsonRoom.getInt("number")+ "-"+ jsonRoom.getString("activity"));
                    rooms.put(jsonRoom.getInt("number"), jsonRoom.getString("activity"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    private Emitter.Listener onMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("---------> New message : "+ args[0]);
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
            System.out.println("---------> Sending message : "+ message);
            mSocket.emit("message", message);
        }

    }

    public void askDirection(String id)
    {
        if (isConnected)
        {
            System.out.println("---------> Asking position for id : "+ id);
            mSocket.emit("askDirection", id);
        }
    }

    public void askAllRooms()
    {
        if (isConnected)
        {
            System.out.println("---------> Asking for all rooms");
            mSocket.emit("askAllRooms");
        } else {
            System.out.println("NOT CONNECTED!");
        }
    }

    public void connect()
    {
        mSocket.connect();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getSpeed() {
        return speed;
    }

    public int getDirection() {
        return direction;
    }

    public int getDelay() {
        return delay;
    }

    public HashMap<Integer, String> getRooms(){ return rooms;}
}
