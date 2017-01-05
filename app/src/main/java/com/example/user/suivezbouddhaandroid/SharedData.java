package com.example.user.suivezbouddhaandroid;

/**
 * Created by Arnaud Garnier on 05/01/17.
 */

import android.app.Application;

public class SharedData extends Application {

    private boolean data;


    public boolean getData() {
        return data;
    }

    public void setData(boolean data) {
        this.data = data;
    }

}