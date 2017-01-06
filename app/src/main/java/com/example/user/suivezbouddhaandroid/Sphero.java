package com.example.user.suivezbouddhaandroid;

/**
 * Created by lucas on 16/12/16.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.orbotix.ConvenienceRobot;
import com.orbotix.DualStackDiscoveryAgent;
import com.orbotix.calibration.api.CalibrationEventListener;
import com.orbotix.calibration.api.CalibrationImageButtonView;
import com.orbotix.calibration.api.CalibrationView;
import com.orbotix.command.RollCommand;
import com.orbotix.common.DiscoveryException;
import com.orbotix.common.Robot;
import com.orbotix.common.RobotChangedStateListener;
import com.orbotix.le.RobotLE;
import com.orbotix.macro.MacroObject;
import com.orbotix.macro.cmd.Delay;
import com.orbotix.macro.cmd.Fade;
import com.orbotix.macro.cmd.RGB;
import com.orbotix.macro.cmd.Roll;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


/**
 * Hello World Sample
 * Connect either a Bluetooth Classic or Bluetooth LE robot to an Android Device, then
 * blink the robot's LED on or off every two seconds.
 *
 * This example also covers turning on Developer Mode for LE robots.
 */

public class Sphero extends Activity implements RobotChangedStateListener, View.OnClickListener, Observer {

    private ConvenienceRobot mRobot;
    private Button goButton;
    private Button stopButton;
    private Button scanButton;
    private CalibrationView _calibrationView;
    private CalibrationImageButtonView _calibrationButtonView;
    private Client client;
    private JSONObject jsonObject;
    private boolean data = false;
    private int dataStep = 0;
    private AssetManager assets;

    private MacroObject macro;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 42;

    private final MediaPlayer mp = new MediaPlayer();

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sphero);

        assets = getAssets();

        /*
            Associate a listener for robot state changes with the DualStackDiscoveryAgent.
            DualStackDiscoveryAgent checks for both Bluetooth Classic and Bluetooth LE.
            DiscoveryAgentClassic checks only for Bluetooth Classic robots.
            DiscoveryAgentLE checks only for Bluetooth LE robots.
       */
        DualStackDiscoveryAgent.getInstance().addRobotStateListener( this );


        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {

            int hasLocationPermission = checkSelfPermission( Manifest.permission.ACCESS_COARSE_LOCATION );
            if( hasLocationPermission != PackageManager.PERMISSION_GRANTED ) {
                Log.e( "Sphero", "Location permission has not already been granted" );
                List<String> permissions = new ArrayList<String>();
                permissions.add( Manifest.permission.ACCESS_COARSE_LOCATION);
                requestPermissions(permissions.toArray(new String[permissions.size()] ), REQUEST_CODE_LOCATION_PERMISSION );
            } else {
                Log.d( "Sphero", "Location permission already granted" );
            }
        }

        setupCalibration();

        // Here, you need to route all the touch events to the joystick and calibration view so that they know about
        // them. To do this, you need a way to reference the view (in this case, the id "entire_view") and attach
        // an onTouchListener which in this case is declared anonymously and invokes the
        // Controller#interpretMotionEvent() method on the joystick and the calibration view.
        findViewById(R.id.sphero).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                _calibrationView.interpretMotionEvent(event);
                return true;
            }
        });

        initViews();

        //SharedData sharedData = (SharedData) getApplicationContext();
        //data = SharedData.getData();

        Log.i("Sphero", String.valueOf(data));

    }

    private void initViews() {

        goButton = (Button) findViewById(R.id.go);
        stopButton = (Button) findViewById(R.id.stop);
        scanButton = (Button) findViewById(R.id.scan);

        goButton.setOnClickListener( this );
        stopButton.setOnClickListener( this );

        scanButton.setAlpha(.5f);
        scanButton.setClickable(false);

        goButton.setAlpha(.5f);
        goButton.setClickable(false);

        stopButton.setAlpha(.5f);
        stopButton.setClickable(false);

        _calibrationButtonView.setAlpha(.5f);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch ( requestCode ) {
            case REQUEST_CODE_LOCATION_PERMISSION: {
                for( int i = 0; i < permissions.length; i++ ) {
                    if( grantResults[i] == PackageManager.PERMISSION_GRANTED ) {
                        startDiscovery();
                        Log.d( "Permissions", "Permission Granted: " + permissions[i] );
                    } else if( grantResults[i] == PackageManager.PERMISSION_DENIED ) {
                        Log.d( "Permissions", "Permission Denied: " + permissions[i] );
                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }


    //Turn the robot LED on or off every two seconds
    private void blink( final boolean lit ) {
        if( mRobot == null )
            return;

        if( lit ) {
            mRobot.setLed( 0.0f, 0.0f, 0.0f );
        } else {
            mRobot.setLed( 0.0f, 0.0f, 1.0f );
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                blink(!lit);
            }
        }, 2000);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if( Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkSelfPermission( Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {
                startDiscovery();
            }
        }
    }

    private void startDiscovery() {
        //If the DiscoveryAgent is not already looking for robots, start discovery.
        if( !DualStackDiscoveryAgent.getInstance().isDiscovering() ) {
            try {
                DualStackDiscoveryAgent.getInstance().startDiscovery(getApplicationContext());
                Toast.makeText(getApplicationContext(), "Recherche...", Toast.LENGTH_LONG).show();
                Log.d("Sphero", "Recherche");
            } catch (DiscoveryException e) {
                Toast.makeText(getApplicationContext(), "Allumez le bluetooth !", Toast.LENGTH_LONG).show();
                Log.e("Sphero", "DiscoveryException: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onStop() {
        //If the DiscoveryAgent is in discovery mode, stop it.
        /*
        if( DualStackDiscoveryAgent.getInstance().isDiscovering() ) {
            DualStackDiscoveryAgent.getInstance().stopDiscovery();
        }

        //If a robot is connected to the device, disconnect it
        if( mRobot != null ) {
            mRobot.disconnect();
            mRobot = null;
        }
        */
        Log.i("Sphero", "onStop");
        super.onStop();

    }

    @Override
    protected void onPause() {

        Log.i("Sphero", "onPause");
        super.onPause();

    }

    @Override
    protected void onResume() {

        Log.i("Sphero", "onResume");
        super.onResume();

    }


    @Override
    protected void onDestroy() {

        Log.i("Sphero", "onDestroy");

        if( DualStackDiscoveryAgent.getInstance().isDiscovering() ) {
            DualStackDiscoveryAgent.getInstance().stopDiscovery();
        }

        //If a robot is connected to the device, disconnect it
        if( mRobot != null ) {
            mRobot.disconnect();
            mRobot = null;
        }

        super.onDestroy();
        DualStackDiscoveryAgent.getInstance().addRobotStateListener(null);
    }

    @Override
    public void handleRobotChangedState( Robot robot, RobotChangedStateNotificationType type ) {
        Log.d("Sphero", String.valueOf(type));
        Toast.makeText(getApplicationContext(), String.valueOf(type), Toast.LENGTH_SHORT).show();
        switch( type ) {
            case Online: {

                //On remet les bouton enable
                goButton.setAlpha(1f);
                goButton.setClickable(true);

                stopButton.setAlpha(1f);
                stopButton.setClickable(true);

                _calibrationView.setEnabled(true);
                _calibrationButtonView.setAlpha(1f);
                _calibrationButtonView.setEnabled(true);


                //If robot uses Bluetooth LE, Developer Mode can be turned on.
                //This turns off DOS protection. This generally isn't required.
                if( robot instanceof RobotLE) {
                    ( (RobotLE) robot ).setDeveloperMode( true );
                }

                //Save the robot as a ConvenienceRobot for additional utility methods
                mRobot = new ConvenienceRobot( robot );

                //Start blinking the robot's LED
                blink( false );
                break;
            }
            case Disconnected: {
                _calibrationView.setEnabled(false);
                _calibrationButtonView.setEnabled(false);
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if ( mRobot == null ) {
            return;
        }
        switch( v.getId() ) {
            case R.id.go: {

                //On disable le bouton
                //TODO
                //goButton.setAlpha(0.5f);
                //goButton.setClickable(false);

                client = new Client();
                client.addObserver(this);
                client.connect();

                while(!client.isConnected());

                if(mp.isPlaying())
                {
                    mp.stop();
                }

                try {
                    mp.reset();
                    AssetFileDescriptor afd;
                    afd = assets.openFd("R2D2Scream.mp3");
                    mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
                    mp.prepare();
                    mp.start();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.i("Sphero", "Instructions globales : " + dataStep);
                client.askDirection(String.valueOf(dataStep));
                dataStep++;
                Log.i("Sphero", "Etape : "+dataStep);

                //Bouton scan
                scanButton.setClickable(true);
                scanButton.setAlpha(1f);
                scanButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myIntent = new Intent(getApplicationContext(), ScanActivity.class);
                        myIntent.putExtra("id", dataStep);
                        startActivityForResult(myIntent, 1);
                    }
                });
                break;
            }
            case R.id.stop: {
                macro.stopMacro();
                mRobot.sendCommand(new RollCommand(0, 0.0f, RollCommand.State.STOP));
                break;
            }
        }
    }

    /**
     * Sets up the calibration gesture and button
     */
    private void setupCalibration() {
        // Get the view from the xml file
        _calibrationView = (CalibrationView)findViewById(R.id.calibrationView);
        _calibrationView.setDotColor(0xFF1990FF);
        _calibrationView.setDotSize(600);

        // Set the glow. You might want to not turn this on if you're using any intense graphical elements.
        _calibrationView.setShowGlow(false);
        // Register anonymously for the calibration events here. You could also have this class implement the interface
        // manually if you plan to do more with the callbacks.
        _calibrationView.setCalibrationEventListener(new CalibrationEventListener() {
            /**
             * Invoked when the user begins the calibration process.
             */
            @Override
            public void onCalibrationBegan() {
                // The easy way to set up the robot for calibration is to use ConvenienceRobot#calibrating(true)
                Log.v("Sphero", "Calibration began!");
                mRobot.calibrating(true);

                //On verouille les boutons
                scanButton.setAlpha(.5f);
                scanButton.setClickable(false);

                goButton.setAlpha(.5f);
                goButton.setClickable(false);

                stopButton.setAlpha(.5f);
                stopButton.setClickable(false);
            }

            /**
             * Invoked when the user moves the calibration ring
             * @param angle The angle that the robot has rotated to.
             */
            @Override
            public void onCalibrationChanged(float angle) {
                // The usual thing to do when calibration happens is to send a roll command with this new angle, a speed of 0
                // and the calibrate flag set.
                Log.v("Sphero", "Calibration changed!");
                mRobot.rotate(angle);

                //Calibration Sound
                if(!mp.isPlaying()) {
                    try {
                        mp.reset();
                        AssetFileDescriptor afd;
                        afd = assets.openFd("R2D2_calibration.mp3");
                        mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
                        mp.prepare();
                        mp.start();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            /**
             * Invoked when the user stops the calibration process
             */
            @Override
            public void onCalibrationEnded() {
                // This is where the calibration process is "committed". Here you want to tell the robot to stop as well as
                // stop the calibration process.
                mRobot.stop();
                mRobot.calibrating(false);

                //On deverouille les boutons
                scanButton.setAlpha(1f);
                scanButton.setClickable(true);

                goButton.setAlpha(1f);
                goButton.setClickable(true);

                stopButton.setAlpha(1f);
                stopButton.setClickable(true);
            }
        });
        // Like the joystick, turn this off until a robot connects.
        _calibrationView.setEnabled(false);

        // To set up the button, you need a calibration view. You get the button view, and then set it to the
        // calibration view that we just configured.
        _calibrationButtonView = (CalibrationImageButtonView) findViewById(R.id.calibrateButton);
        _calibrationButtonView.setCalibrationView(_calibrationView);
        _calibrationButtonView.setEnabled(false);
    }

    boolean test = false;

    @Override
    public void update(Observable observable, Object o) {

        Log.i("Sphero", "Update !");

        JSONArray directions;
        JSONObject instruction;

        float speed;
        int direction;
        int delay;
        int delayTotal = 0;

        boolean finish;

        jsonObject = client.getDirections();

        if(jsonObject==null) return;

        try {

            directions = jsonObject.getJSONArray("directions");

            macro = new MacroObject();

            for(int y = 0; y < directions.length(); y++) {
                Log.i("Sphero", "Instructions locales : "+ y);

                instruction = directions.getJSONObject(y);

                if (instruction==null) return;

                speed = Float.parseFloat(instruction.getString("speed"));
                direction = instruction.getInt("direction");
                delay = instruction.getInt("delay");
                delayTotal += delay;

                macro.addCommand( new Roll(speed, direction, delay));
                macro.addCommand( new Delay(delay));
            }

            //Stop
            macro.addCommand( new Roll(0.0f, 0, 0));

            //On regarde si on a finit le parcours
            finish = jsonObject.getBoolean("finish");
            if(finish) {
                Log.d("Sphero", "finished ! ");
                macro.addCommand( new RGB(0, 255, 0, 10000));
                macro.addCommand( new Delay( 10000 ) );
                finishSound(delayTotal);

                //TODO : Yolo popup ici
                runOnUiThread(new Runnable() {
                    public void run() {

                        AlertDialog alertDialog = new AlertDialog.Builder(Sphero.this).create();
                        alertDialog.setTitle("Attention un escalier !");
                        alertDialog.setMessage("Veuillez s'il vous plaît monter les escaliers avec Bouddha. Scanner ensuite le prochaine QRCode.");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                });
            }

            //Send the macro to the robot and play
            macro.setMode( MacroObject.MacroObjectMode.Normal );
            macro.setRobot( mRobot.getRobot() );
            macro.playMacro();


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // A contact was picked.  Here we will just display it
                // to the user.
                boolean dataBool = data.getBooleanExtra("data", false);

                if(dataBool) {
                    //On fait rouler la boule
                    scanButton.setClickable(false);
                    scanButton.setAlpha(0.5f);

                    Log.i("Sphero", "Instructions globales : " + dataStep);
                    client.askDirection(String.valueOf(dataStep));
                    dataStep++;
                    Log.i("Sphero", "Etape : "+dataStep);

                    //On remet le scan disponible pour la prochaine étape
                    scanButton.setClickable(true);
                    scanButton.setAlpha(1f);
                    scanButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent myIntent = new Intent(getApplicationContext(), ScanActivity.class);
                            myIntent.putExtra("id", dataStep);
                            startActivityForResult(myIntent, 1);
                        }
                    });

                }
            }

        }
    }

    public void finishSound(int delay) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            public void run() {
                if(mp.isPlaying())
                {
                    mp.stop();
                }

                try {
                    mp.reset();
                    AssetFileDescriptor afd;
                    afd = assets.openFd("R2D2_finish.mp3");
                    mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
                    mp.prepare();
                    mp.start();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, (delay + delay));
    }
}