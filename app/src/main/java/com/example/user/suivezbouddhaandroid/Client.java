package com.example.user.suivezbouddhaandroid;

import android.graphics.Point;
import android.util.Log;

import java.net.URISyntaxException;
import java.util.Observable;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by user on 16/12/16.
 */
public class Client extends Observable {
    private Socket mSocket;
    private Boolean isConnected;
    private final String serverAddress = "http://192.168.1.95:8080/";//http://192.168.1.95:8080/
    private String message ;
    private float x;
    private float y;

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
        mSocket.on("newPosition", onNewPosition);
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            isConnected = true;
            System.out.println("---------> Connect");
            mSocket.emit("who", "Chloé");
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

    private Emitter.Listener onNewPosition = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("---------> New position : "+ args[0]);
            x = Float.valueOf(args[0].toString().split(",")[0]);
            y = Float.valueOf(args[0].toString().split(",")[1]);
            setChanged();
            notifyObservers();
            clearChanged();
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
}
