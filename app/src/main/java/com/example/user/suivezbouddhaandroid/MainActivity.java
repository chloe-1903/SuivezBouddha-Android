package com.example.user.suivezbouddhaandroid;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.SystemRequirementsChecker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer{
    private Client client;
    private Utils utils;
    private HashMap<String, ArrayList<String>> rooms;
    private ArrayList<String> array;

    private static final String[] PERMS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(PERMS, 15);

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

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

        Button scanButton = (Button)findViewById(R.id.scanButtonMain);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
                startActivityForResult(intent, 1);
            }
        });
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
        array = new ArrayList<>();
        for(String num : rooms.keySet()) {
            array.add(num);
           // Log.d("->salle"," ListItem " + num + " - "+ rooms.get(num).get(1) );
        }
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , array);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listRoomsView.setAdapter(arrayAdapter);
                listRoomsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        //Write the room selected on the file
                        String[] roomName = array.get(i).split(" - ");

                        try {
                            //nom;x-y;étage;(1 ou 2)qrcodeId;
                            //nom - android pos - étage - qr code
                            Utils.writeToFile(roomName[0]+";"+ rooms.get(roomName[0]).get(1) + ";" + rooms.get(roomName[0]).get(0) + ";" + rooms.get(roomName[0]).get(2) +";", "RoomSelected.txt");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Intent myIntent = new Intent(getApplicationContext(), Menu.class);
                        startActivity(myIntent);
                    }
                });
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                int dat = data.getIntExtra("index", -1);
                Log.d("QRCODE Test -Scanné:", ""+dat);

                String temp = Integer.toString(dat);
                Log.d("debug", temp);

                if(dat != -1 && rooms!=null) {
                    ArrayList<String> roomInfo = new ArrayList<>();
                    String roomName = "";
                    for(String num : rooms.keySet()) {
                        if (!rooms.get(num).get(2).equals("") && Integer.valueOf(rooms.get(num).get(2))==dat) { //si la salle a un qrcode=celui que l'on vient de scanner
                            roomInfo = rooms.get(num);
                            roomName = num;
                            Log.d("QRCODE Test : ","in equals");
                        }
                        Log.d("QRCODE Test - parcouru ", rooms.get(num).get(2));
                    }
                    if (roomInfo.isEmpty() && dat!=10) {//si dat==10, la salle n'a pas été configurée (on peut pas y aller)
                        Toast.makeText(getApplicationContext(), "Ce QRCode ne correspond à aucune salle", Toast.LENGTH_LONG).show();
                        return;
                    }
                    try {
                        //nom - android pos - étage - qr code
                        Utils.writeToFile(roomName + ";" + roomInfo.get(1) + ";" + roomInfo.get(0) +";" + roomInfo.get(2) +";", "RoomSelected.txt");
                        Log.d("QRCODE Test txt : ", roomName + ";" + roomInfo.get(1) + ";" + roomInfo.get(0) +";" + roomInfo.get(2) +";");
                        Intent myIntent = new Intent(getApplicationContext(), Menu.class);
                        Toast.makeText(getApplicationContext(), "Salle de destination enregistrée", Toast.LENGTH_LONG).show();
                        startActivity(myIntent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
