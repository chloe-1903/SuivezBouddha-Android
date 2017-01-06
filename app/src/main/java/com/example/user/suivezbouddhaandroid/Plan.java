package com.example.user.suivezbouddhaandroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;
import java.util.Stack;

public class Plan extends AppCompatActivity implements Observer {
    private Client client;
    private float x;
    private float y;
    private Stack<String> qrCodesIds;
    private Button scanButton;
    private int dataStep = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);
        qrCodesIds = new Stack<>();
        for (int i=8; i>0 ; i--)
            qrCodesIds.push(String.valueOf(i));
        client = new Client();
        client.addObserver(this);
        client.connect();
        x=y=0;
        drawPosition();
        scanButton = (Button)findViewById(R.id.scan_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
                intent.putExtra("id", ++dataStep);

                startActivityForResult(intent, 1);
            }
        });
        //client.askPosition("0");
    }

    public void drawPosition(){
        runOnUiThread(new Runnable() {
            public void run() {
                ImageView imageView = (ImageView) findViewById(R.id.img);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.plan1nt);
                Bitmap tempBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
                Canvas c = new Canvas(tempBitmap);
                c.drawBitmap(bitmap, 0, 0, null);
                Paint p = new Paint();
                p.setColor(Color.rgb(204, 102, 119));
                if (x!=0 && y !=0)
                    c.drawCircle(x, y, 30, p);
                imageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
            }
        });
    }

    //Cette fonction sera appelée une fois un QRCode décodé
    public void askDirection(View view){
        if (!qrCodesIds.empty())
            client.askPosition(qrCodesIds.pop());
    }

    @Override
    public void update(Observable o, Object arg) {
        x= client.getX(); y=client.getY();
        drawPosition();
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
}
