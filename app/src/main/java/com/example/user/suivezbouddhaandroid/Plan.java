package com.example.user.suivezbouddhaandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class Plan extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);
        ImageView imageView = (ImageView) findViewById(R.id.img);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.plan1);
        Canvas c = new Canvas(bitmap);
        Paint p = new Paint();
        p.setColor(Color.GREEN);
        c.drawLine(2, 2, 10, 20, p);
        imageView.draw(c);
        imageView.setImageBitmap(bitmap);
    }


}
