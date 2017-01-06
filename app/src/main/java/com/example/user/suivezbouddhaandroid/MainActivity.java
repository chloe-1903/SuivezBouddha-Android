package com.example.user.suivezbouddhaandroid;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer{
    private Client client;
    private HashMap<Integer, String> rooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new Client();
        client.addObserver(this);
        client.connect();
    }

    @Override
    public void update(Observable o, Object arg) {
        Log.d("-->", "In update");
        rooms = client.getRooms();
        if (rooms==null){
            client.askAllRooms();
            return;
        }
        final ListView listRoomsView = (ListView) findViewById(R.id.listRoomsView);
        ArrayList<String> array = new ArrayList<>();
        for(int num : rooms.keySet()) {
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
