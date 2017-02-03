package com.example.user.suivezbouddhaandroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Stack;

public class Plan extends AppCompatActivity implements Observer {
    private Client client;
    private float x;
    private float y;
    private Button scanButton;
    private int currentFloor;
    private String roomSelectedId;
    private String[] roomPosition;
    private int roomFloor;
    private Bitmap bitmap;
    private Bitmap tempBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        //Création du client
        client = new Client();
        client.addObserver(this);
        client.connect();

        //Initialisation de l'ID de la salle où aller
        String line = Utils.readFile("RoomSelected.txt");
        String[] roomSelectedIdTab = line.split(";");
        roomSelectedId = roomSelectedIdTab[0];
        roomPosition = roomSelectedIdTab[1].split("-");
        roomFloor = Integer.valueOf(roomSelectedIdTab[2]);
        currentFloor = roomFloor; //On part du principe que l'utilisateur est déjà au bon étage
        //Création du listener du scan
        scanButton = (Button)findViewById(R.id.scan_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
                intent.putExtra("arrowDir", "none");
                startActivityForResult(intent, 1);
            }
        });

        //Instruction popup
        String message = "Scannez les QRCodes à l'aide du bouton \"OÙ SUIS-JE ?\" pour afficher votre position. La salle est indiquée par un point vert et votre position par un point rouge.";
        imgPopup("Instructions", message, R.drawable.scan_qrcode, 144, 248);
    }

    public void drawPosition(){
        if (bitmap!=null && tempBitmap!=null)//Si on a déjà dessiné la carte
            removeOldMap();
        runOnUiThread(new Runnable() {
            public void run() {
                ImageView imageView = (ImageView) findViewById(R.id.img);
                imageView.setImageResource(0);
                if (currentFloor == 2 )
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.planetage1);
                else
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.planetage0);
                tempBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
                Canvas c = new Canvas(tempBitmap);
                c.drawBitmap(bitmap, 0, 0, null);
                Paint p = new Paint();
                p.setColor(Color.rgb(204, 102, 119));
                float scale = getResources().getDisplayMetrics().density;
                if (x!=0 && y !=0){
                    c.drawCircle(x*scale, y*scale, 35, p);
                }if (currentFloor== roomFloor) { //si on est au bon étage, on met la salle en valeur
                    Paint paintArrival = new Paint();
                    paintArrival.setColor(Color.rgb(119, 170, 119));
                    c.drawCircle(Integer.valueOf(roomPosition[0])*scale, Integer.valueOf(roomPosition[1])*scale, 100, paintArrival);
                }
                //Log.d("salle selectionnée ", ""+roomSelectedId);
                imageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
            }
        });
    }

    @Override
    public void update(Observable o, Object arg) {
        JSONObject jsonAnswer = client.getPosition();
        String position = null;
        try {
            position = jsonAnswer.getString("position");
            x = Float.valueOf(position.split("-")[0]);
            y = Float.valueOf(position.split("-")[1]);
            currentFloor = Integer.valueOf(jsonAnswer.getString("floor"));
            drawPosition();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                int dat = data.getIntExtra("index", -1);

                String temp = Integer.toString(dat);
                Log.d("debug", temp);

                if(dat != -1) {
                    client.askPosition(temp);
                }
            }
        }
    }


    /**
     * Classic popup with image
     * @param title
     * @param text
     * @param imageInt
     * @param height
     * @param width
     */
    public void imgPopup(final String title, final String text, final int imageInt, final int height, final int width) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            public void run() {
                final float scale = getResources().getDisplayMetrics().density;

                //Add layout image
                LayoutInflater factory = LayoutInflater.from(Plan.this);
                final View view = factory.inflate(R.layout.popup, null);

                //Switch image
                ImageView image = (ImageView) view.findViewById(R.id.dialog_imageview);
                image.setImageResource(imageInt);
                image.getLayoutParams().height = (int) (height * scale);
                image.getLayoutParams().width = (int) (width * scale);
                image.requestLayout();

                //popup
                AlertDialog alertDialog = new AlertDialog.Builder(Plan.this).create();
                alertDialog.setTitle(title);
                alertDialog.setMessage(text);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setView(view);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //nothing, just close automatically
                            }
                        });
                try {
                    alertDialog.show();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                //Center the text
                TextView messageText = (TextView)alertDialog.findViewById(android.R.id.message);
                messageText.setGravity(Gravity.CENTER);
            }
        }, (0));
    }

    @Override
    protected void onDestroy() {
        removeOldMap();
        super.onDestroy();
    }

    public void removeOldMap(){
        //Recycling bitmap to avoid out of memory
        bitmap.recycle();
        bitmap=null;

        tempBitmap.recycle();
        tempBitmap=null;
    }
}
