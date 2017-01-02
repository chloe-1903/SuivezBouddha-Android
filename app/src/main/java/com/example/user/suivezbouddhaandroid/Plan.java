package com.example.user.suivezbouddhaandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

public class Plan extends AppCompatActivity implements Observer {
    Client client;
    private float x;
    private float y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);
        x=500; y=600;
        drawPosition();
        client = new Client();
        client.addObserver(this);
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
                p.setColor(Color.rgb(245, 20, 65));
                c.drawCircle(x, y, 20, p);
                imageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
            }
        });
    }

    public void connect(View view){
        client.connect();
    }

    @Override
    public void update(Observable o, Object arg) {
        x= client.getX(); y=client.getY();
        drawPosition();
    }
}
