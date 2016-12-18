package com.example.user.suivezbouddhaandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class TestServeur extends AppCompatActivity {

    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_serveur);
        client = new Client();
    }

    public void connect(View view){
        client.connect();
    }

    public void updateMessage(View view){
        TextView textView = (TextView) findViewById(R.id.message);
        textView.setText(client.getMessage());
    }

    public void sendMessage(View view) {
        EditText text = (EditText) findViewById(R.id.messageToSend);
        client.sendMessage(text.getText().toString());
    }
}
