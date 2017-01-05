package com.example.user.suivezbouddhaandroid;

/**
 * Created by lucas on 16/12/16.
 */

import android.graphics.Color;
import android.os.Bundle;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
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
import com.orbotix.macro.cmd.Roll;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private CalibrationView _calibrationView;
    private CalibrationImageButtonView _calibrationButtonView;
    private Client client;
    private JSONObject jsonObject;

    private MacroObject macro;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 42;


    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sphero);

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

    }

    private void initViews() {

        goButton = (Button) findViewById(R.id.go);
        stopButton = (Button) findViewById(R.id.stop);

        goButton.setOnClickListener( this );
        stopButton.setOnClickListener( this );
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
        }a
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
        super.onDestroy();
        DualStackDiscoveryAgent.getInstance().addRobotStateListener(null);
    }

    @Override
    public void handleRobotChangedState( Robot robot, RobotChangedStateNotificationType type ) {
        Log.d("Sphero", String.valueOf(type));
        Toast.makeText(getApplicationContext(), String.valueOf(type), Toast.LENGTH_SHORT).show();
        switch( type ) {
            case Online: {

                _calibrationView.setEnabled(true);
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

                client = new Client();
                client.addObserver(this);
                client.connect();

                //TODO 2 en static, ca pue !
                /*
                for(int i = 0; i < 2; i++) {
                    Log.i("Sphero", "Instructions globales : " + i);

                    client.askDirection(String.valueOf(i));

                    //TODO scan le QR Code pour continuer
                }
                */
                while(!client.isConnected());
                Log.i("Sphero", "Instructions globales : " + 0);
                client.askDirection(String.valueOf(0));

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
            }

            /**
             * Invoked when the user moves the calibration ring
             * @param angle The angle that the robot has rotated to.
             */
            @Override
            public void onCalibrationChanged(float angle) {
                // The usual thing to do when calibration happens is to send a roll command with this new angle, a speed of 0
                // and the calibrate flag set.
                mRobot.rotate(angle);
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

    @Override
    public void update(Observable observable, Object o) {

        Log.i("Sphero", "Update !");

        JSONArray directions;
        JSONObject instruction;

        float speed;
        int direction;
        int delay;

        jsonObject = client.getDirections();

        try {

            directions = jsonObject.getJSONArray("directions");

            macro = new MacroObject();

            for(int y = 0; y < directions.length(); y++) {
                Log.i("Sphero", "Instructions locales : "+ y);

                instruction = directions.getJSONObject(y);

                speed = Float.parseFloat(instruction.getString("speed"));
                direction = instruction.getInt("direction");
                delay = instruction.getInt("delay");

                macro.addCommand( new Roll(speed, direction, delay));
                macro.addCommand( new Delay(delay));
            }

            //Stop
            macro.addCommand( new Roll(0.0f, 0, 0));

            //Send the macro to the robot and play
            macro.setMode( MacroObject.MacroObjectMode.Normal );
            macro.setRobot( mRobot.getRobot() );
            macro.playMacro();


        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*
        macro = new MacroObject();

        macro.addCommand( new Roll(0.20f, 0, 1000));
        macro.addCommand( new Delay(1000));

        //Pause
        macro.addCommand( new Roll(0.0f, 0, 500));
        macro.addCommand( new Delay(500));

        macro.addCommand( new Roll(0.20f, 180, 1000));
        macro.addCommand( new Delay(1000));

        //Stop
        macro.addCommand( new Roll(0.0f, 0, 0));

        //Send the macro to the robot and play
        macro.setMode( MacroObject.MacroObjectMode.Normal );
        macro.setRobot( mRobot.getRobot() );
        macro.playMacro();
        */
        //mRobot.sendCommand(new RollCommand(90, 0.3f, RollCommand.State.GO));
    }
}