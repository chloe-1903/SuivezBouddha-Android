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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer{
    private Client client;
    private Utils utils;
    private JSONArray rooms;
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
                intent.putExtra("arrowDir", "none");
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
        for (int i = 0; i< rooms.length(); i++) {
            JSONArray jsonFloor = null; //ensemble des salles de l'étage
            try {
                jsonFloor = rooms.getJSONArray(i);
                for (int k = 0; k< jsonFloor.length() ; k++)
                {
                    JSONObject jsonRoom = jsonFloor.getJSONObject(k);
                    array.add(jsonRoom.getString("number"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
                        String roomName = array.get(i);

                        try {
                            String androidPos = "";
                            String floor = "";
                            String qrcode = "";
                            for (int j = 0; j< rooms.length(); j++) {
                                JSONArray jsonFloor = rooms.getJSONArray(j);
                                for (int k = 0; k < jsonFloor.length(); k++) {
                                    JSONObject jsonRoom = jsonFloor.getJSONObject(k);
                                    if(jsonRoom.getString("number").equals(roomName)){
                                        androidPos = jsonRoom.getString("positionAndroid");
                                        floor = Integer.toString(j+1);
                                        qrcode = jsonRoom.getString("qrcodeId");
                                    }
                                }
                            }
                            //nom - android pos - étage - qr code
                            Log.d("debug salle :", roomName+";"+ androidPos + ";" + floor + ";" + qrcode+";");
                            Utils.writeToFile(roomName+";"+ androidPos + ";" + floor + ";" + qrcode+";", "RoomSelected.txt");
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
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

                String temp = Integer.toString(dat);
                Log.d("debug", temp);

                if (dat == 10){//si dat==10, la salle n'a pas été configurée (on peut pas y aller)
                    Toast.makeText(getApplicationContext(), "Cette salle n'a aucune présentation aujourd'hui", Toast.LENGTH_LONG).show();
                    return;
                }

                if(dat != -1 && rooms!=null) {
                    Log.d("debug salle :", ""+dat);
                    try {
                        String androidPos = "";
                        String floor = "";
                        String roomName = "";
                        for (int j = 0; j< rooms.length(); j++) {
                            JSONArray jsonFloor = rooms.getJSONArray(j);
                            for (int k = 0; k < jsonFloor.length(); k++) {
                                JSONObject jsonRoom = jsonFloor.getJSONObject(k);
                                if(Integer.valueOf(jsonRoom.getString("qrcodeId"))==dat){
                                    androidPos = jsonRoom.getString("positionAndroid");
                                    floor = Integer.toString(j+1);
                                    roomName = jsonRoom.getString("number");
                                }
                            }
                        }
                        if (roomName=="") {//on a pas trouvé la salle
                            Toast.makeText(getApplicationContext(), "Cette salle n'a aucune présentation aujourd'hui", Toast.LENGTH_LONG).show();
                            return;
                        }
                        //nom - android pos - étage - qr code
                        Log.d("debug salle :", roomName+";"+ androidPos + ";" + floor + ";" + dat+";");
                        Utils.writeToFile(roomName+";"+ androidPos + ";" + floor + ";" + dat+";", "RoomSelected.txt");
                        Intent myIntent = new Intent(getApplicationContext(), Menu.class);
                        startActivity(myIntent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
