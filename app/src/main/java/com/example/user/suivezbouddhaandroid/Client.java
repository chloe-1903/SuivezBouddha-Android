package com.example.user.suivezbouddhaandroid;

import android.util.Log;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by user on 16/12/16.
 */
public class Client {
    private Socket mSocket;
    private Boolean isConnected;
    private final String serverAddress = "http://10.212.109.188:8080";//.111.29
    private String message ;

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
}
