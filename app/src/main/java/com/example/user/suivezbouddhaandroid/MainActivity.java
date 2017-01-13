package com.example.user.suivezbouddhaandroid;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer{
    private Client client;
    private HashMap<String, String> rooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() == null) {
            TextView textView = (TextView) findViewById(R.id.mainTextView);
            textView.setText("Veuillez vous connecter à internet.");
            ListView listRoomsView = (ListView) findViewById(R.id.listRoomsView);
            listRoomsView.setVisibility(View.INVISIBLE);
            textView.setTextSize(21);
        }
        client = new Client();
        client.addObserver(this);
        client.connect();
    }

    @Override
    public void update(Observable o, Object arg) {
        rooms = client.getRooms();
        if (rooms==null){ //notify de onConnect
            client.askAllRooms();
            return;
        }
        //notify de onNewPosition
        final ListView listRoomsView = (ListView) findViewById(R.id.listRoomsView);
        ArrayList<String> array = new ArrayList<>();
        for(String num : rooms.keySet()) {
            array.add(num + " - "+ rooms.get(num));
            Log.d("->"," ListItem " + num + " - "+ rooms.get(num) );
        }
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , array);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listRoomsView.setAdapter(arrayAdapter);
                listRoomsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent myIntent = new Intent(getApplicationContext(), Menu.class);
                        startActivity(myIntent);
                    }
                });
            }
        });

    }
}
