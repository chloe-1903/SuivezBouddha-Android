package com.example.user.suivezbouddhaandroid;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;
import java.util.UUID;

/**
 * Created by Arnaud on 13/01/17.
 */

public class MyApplication extends Application {

    private BeaconManager beaconManager;

    private String targetRoom;

    @Override
    public void onCreate() {
        super.onCreate();
        beaconManager = new BeaconManager(getApplicationContext());

        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                /*showNotification(
                        "FLASH INFO",
                        "Vous entrez dans la zone du beacon.");*/
                targetRoom = Utils.readFile("RoomSelected.txt");
                String[] roomSelectedIdTab = targetRoom.split(";");
                targetRoom = roomSelectedIdTab[0];
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                v.vibrate(500);
                Toast.makeText(getApplicationContext(), "Vous êtes en salle " + targetRoom + ".", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onExitedRegion(Region region) {
                // exit notification ?
            }
        });

        // scan every second ; default value for wait time
        beaconManager.setBackgroundScanPeriod(1000, 25000);

        // update every 3 second
        beaconManager.setRegionExitExpiration(3000);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startMonitoring(new Region(
                        "monitored region",
                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                        20634, 36204));
            }
        });

    }

    public void showNotification(String title, String message) {
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[] { notifyIntent }, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }


}
