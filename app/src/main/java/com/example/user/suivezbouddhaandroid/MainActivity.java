package com.example.user.suivezbouddhaandroid;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {
    //private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startTestServeurActivity(View view){
        Intent myIntent = new Intent(getApplicationContext(), TestServeur.class);
        startActivity(myIntent);
    }

    public void startSphero(View view){
        Intent myIntent = new Intent(getApplicationContext(), Sphero.class);
        startActivity(myIntent);
    }

    public void startPlan(View view){
        Intent myIntent = new Intent(getApplicationContext(), Plan.class);
        startActivity(myIntent);
    }


    public void startMain(View view){
        Intent myIntent = new Intent(getApplicationContext(), Main2Activity.class);
        startActivity(myIntent);
    }

}
