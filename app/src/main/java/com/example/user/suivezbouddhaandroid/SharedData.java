package com.example.user.suivezbouddhaandroid;

/**
 * Created by Arnaud Garnier on 05/01/17.
 */

import android.app.Application;

public class SharedData extends Application {

    private static boolean data;


    public static boolean getData() {
        return data;
    }

    public static void setData(boolean data) {
        SharedData.data = data;
    }

}