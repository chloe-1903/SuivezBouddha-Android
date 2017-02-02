package com.example.user.suivezbouddhaandroid;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
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
                            HashMap<String, String> planning = new HashMap<String, String>();
                            String androidPos = "";
                            String floor = "";
                            String qrcode = "";
                            for (int j = 0; j < rooms.length(); j++) {
                                JSONArray jsonFloor = rooms.getJSONArray(j);
                                for (int k = 0; k < jsonFloor.length(); k++) {
                                    JSONObject jsonRoom = jsonFloor.getJSONObject(k);
                                    if (jsonRoom.getString("number").equals(roomName)) {
                                        androidPos = jsonRoom.getString("positionAndroid");
                                        floor = Integer.toString(j + 1);
                                        qrcode = jsonRoom.getString("qrcodeId");
                                        JSONObject jsonPlanning = jsonRoom.getJSONObject("activity");
                                        planning.put("8h - 9h", jsonPlanning.getString("8h - 9h"));
                                        planning.put("9h - 10h", jsonPlanning.getString("9h - 10h"));
                                        planning.put("10h - 11h", jsonPlanning.getString("10h - 11h"));
                                        planning.put("11h - 12h", jsonPlanning.getString("11h - 12h"));
                                        planning.put("12h - 13h", jsonPlanning.getString("12h - 13h"));
                                        planning.put("13h - 14h", jsonPlanning.getString("13h - 14h"));
                                        planning.put("14h - 15h", jsonPlanning.getString("14h - 15h"));
                                        planning.put("15h - 16h", jsonPlanning.getString("15h - 16h"));
                                    }
                                }
                            }
                            //nom - android pos - étage - qr code
                            Log.d("debug salle :", roomName + ";" + androidPos + ";" + floor + ";" + qrcode + ";");
                            planningPopup(roomName, androidPos, floor, qrcode, planning);
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    private void goRoom(String roomName, String androidPos, String floor, String qrcode){
        try{
            Utils.writeToFile(roomName+";"+ androidPos + ";" + floor + ";" + qrcode+";", "RoomSelected.txt");
        }catch (IOException e){
            e.printStackTrace();
        }
        Intent myIntent = new Intent(getApplicationContext(), Menu.class);
        startActivity(myIntent);
    }


    public void planningPopup(final String roomNumber, final String androidPos, final String floor, final String qrcode,final HashMap<String, String> planning) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            public void run() {
                Log.d("debug salle", "in planning popup");
                //popup params
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Planing de la salle "+ roomNumber);
                String message="";
                for (String plage : planning.keySet()) {
                    if(!planning.get(plage).equals(""))
                        message+="\n "+ plage + " : "+ planning.get(plage);
                }
                alertDialog.setMessage(message);
                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Y ALLER!",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                goRoom(roomNumber, androidPos, floor, qrcode);
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE , "AUTRE SALLE",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                //show popup
                alertDialog.show();

                //Center the text
                TextView messageText = (TextView) alertDialog.findViewById(android.R.id.message);
                messageText.setGravity(Gravity.CENTER);
            }
        }, 0);
    }

}
