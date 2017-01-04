package com.example.user.suivezbouddhaandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class Main2Activity extends AppCompatActivity implements Observer{
    private Client client;
    private HashMap<Integer, String> rooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        client = new Client();
        client.addObserver(this);
        client.connect();
        System.out.println("before");
        client.askAllRooms();
        System.out.println("after");
    }

    @Override
    public void update(Observable o, Object arg) {
        rooms = client.getRooms();
        ListView listRoomsView = (ListView) findViewById(R.id.listRoomsView);
        ArrayList<String> array = new ArrayList<>();
        for(int num : rooms.keySet()) {
            array.add(num, rooms.get(num));
        }
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , array);
        listRoomsView.setAdapter(arrayAdapter);
    }
}
