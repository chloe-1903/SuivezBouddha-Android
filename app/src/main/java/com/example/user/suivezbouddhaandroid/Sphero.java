package com.example.user.suivezbouddhaandroid;

/**
 * Created by lucas on 16/12/16.
 */

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
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
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
    private int dataStep = 0;
    private AssetManager assets;
    private MacroObject macro;
    private String popUpMessage;
    private Context context;
    private Utils utils;
    private String roomSelectedId;
    private int QRCodeID;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 42;

    private final MediaPlayer mp = new MediaPlayer();

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sphero);

        assets = getAssets();
        context = this.getApplicationContext();
        utils = new Utils();

        /*
            Associate a listener for robot state changes with the DualStackDiscoveryAgent.
            DualStackDiscoveryAgent checks for both Bluetooth Classic and Bluetooth LE.
            DiscoveryAgentClassic checks only for Bluetooth Classic robots.
            DiscoveryAgentLE checks only for Bluetooth LE robots.
       */
        DualStackDiscoveryAgent.getInstance().addRobotStateListener( this );

        //Check the permission
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

        //for the button
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

        //Init buttons
        initViews();

        roomSelectedId = utils.readFile("RoomSelected.txt");
        String[] roomSelectedIdTab = roomSelectedId.split(";");
        roomSelectedId = roomSelectedIdTab[0];
    }

    /**
     * Find every buttons we need and set the unclickable until the sphero is connected
     */
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

    //===============================================
    //=================== SPHERO ====================
    //===============================================

    /**
     * If we got permissions, start the discovery
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
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

    /**
     * Start discovery the sphero
     */
    private void startDiscovery() {
        //If the DiscoveryAgent is not already looking for robots, start discovery.
        if( !DualStackDiscoveryAgent.getInstance().isDiscovering() ) {
            try {
                DualStackDiscoveryAgent.getInstance().startDiscovery(getApplicationContext());
                Toast.makeText(getApplicationContext(), "Recherche...", Toast.LENGTH_LONG).show();
                Log.d("Sphero", "Recherche");
            } catch (DiscoveryException e) {
                Toast.makeText(getApplicationContext(), "Veuillez allumer le bluetooth", Toast.LENGTH_LONG).show();
                Log.e("Sphero", "DiscoveryException: " + e.getMessage());
            }
        }
    }


    /**
     * Check the sphero status and act
     * @param robot
     * @param type
     */
    @Override
    public void handleRobotChangedState( Robot robot, RobotChangedStateNotificationType type ) {
        Log.d("Sphero", String.valueOf(type));
        Toast.makeText(getApplicationContext(), String.valueOf(type), Toast.LENGTH_SHORT).show();
        switch( type ) {
            case Online: {

                //Set buttons enable
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
            case Connecting: {
                //Instructions Popups
                String message = "Bienvenue dans l'utilisation de Bouddha ! Commencez par positionner la sphère sur la pastille rouge du point de départ.";
                instructionsPopup(0, "Instructions", message, R.drawable.pastille2, 189, 190);

                break;
            }
            case Disconnected: {
                _calibrationView.setEnabled(false);
                _calibrationButtonView.setEnabled(false);
                break;
            }
        }
    }

    /**
     * Add to the macro a blink
     */
    public void blinkStairs(MacroObject macro, int nbBlink) {

        for(int i = 0; i < nbBlink; i++){
            macro.addCommand(new RGB(237, 127, 16, 1000));
            macro.addCommand(new Delay(1000));
            macro.addCommand(new RGB(0, 0, 0, 500));
            macro.addCommand(new Delay(500));
        }
    }

    //===============================================
    //=================== BUTTONS ===================
    //===============================================

    /**
     * Global buttons handlers
     * @param v
     */
    @Override
    public void onClick(View v) {
        if ( mRobot == null ) {
            return;
        }
        switch( v.getId() ) {
            case R.id.go: {

                //On disable le bouton
                //TODO
                goButton.setAlpha(0.5f);
                goButton.setClickable(false);

                //Connection to the server
                client = new Client();
                client.addObserver(this);
                client.connect();

                while(!client.isConnected());

                //Play a sound
                playSond(0, "R2D2Scream.mp3");

                //Ask for a direction
                Log.i("Sphero", "Instructions globales : " + dataStep);
                client.askDirection(roomSelectedId, String.valueOf(dataStep));
                dataStep++;
                Log.i("Sphero", "Etape : "+dataStep);

                //Button scan
                scanButton.setClickable(true);
                scanButton.setAlpha(1f);
                scanButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //Start scan activity
                        Intent myIntent = new Intent(getApplicationContext(), ScanActivity.class);
                        //myIntent.putExtra("id", dataStep); //TODO je crois que c'est useless ca now
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

                //Lock other buttons
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

                //Unlock other buttons
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

    //===============================================
    //=================== ANSWERS ===================
    //===============================================

    /**
     * update when the server answer
     * @param observable
     * @param o
     */
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
        String stairs;

        //get the direction JSON
        jsonObject = client.getDirections();

        if(jsonObject==null) return;

        //read the json
        try {
            directions = jsonObject.getJSONArray("directions");

            macro = new MacroObject();

            for(int y = 0; y < directions.length(); y++) {
                Log.i("Sphero", "Instructions locales : "+ y);

                instruction = directions.getJSONObject(y);
                QRCodeID =  jsonObject.getInt("qrcodeId");

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

            //We check if the run is over
            finish = jsonObject.getBoolean("finish");
            if(finish) {
                Log.d("Sphero", "finished ! ");
                macro.addCommand( new RGB(0, 255, 0, 10000));
                macro.addCommand( new Delay( 10000 ) );
                playSond(delayTotal, "R2D2_finish.mp3");
                endPopup(delayTotal);
            }

            //Check if we got stairs this time
            stairs = jsonObject.getString("stairs");
            if(stairs.equals("up")) {
                stairsPopup(delayTotal, "up");
                blinkStairs(macro, 5);
            } else if (stairs.equals("down")) {
                stairsPopup(delayTotal, "down");
                blinkStairs(macro, 5);
            }

            //Send the macro to the robot and play
            macro.setMode( MacroObject.MacroObjectMode.Normal );
            macro.setRobot( mRobot.getRobot() );
            macro.playMacro();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * When the scan activity end
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // A contact was picked.  Here we will just display it
                // to the user.
                boolean dataBool = data.getBooleanExtra("data", false);
                int QRCodeIDRevied = data.getIntExtra("index", -1);

                if(dataBool && QRCodeIDRevied == QRCodeID) {
                    //Run sphero
                    scanButton.setClickable(false);
                    scanButton.setAlpha(0.5f);

                    Log.i("Sphero", "Instructions globales : " + dataStep);
                    client.askDirection(roomSelectedId, String.valueOf(dataStep));
                    dataStep++;
                    Log.i("Sphero", "Etape : "+dataStep);

                    //Unlock scan
                    scanButton.setClickable(true);
                    scanButton.setAlpha(1f);
                    scanButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent myIntent = new Intent(getApplicationContext(), ScanActivity.class);
                            //myIntent.putExtra("id", dataStep); //TODO je crois que c'est useless ca now
                            startActivityForResult(myIntent, 1);
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), "Vous n'avez pas scanné le bon QRCode pour le parcours sélectionné. Etes-vous perdu ? ", Toast.LENGTH_LONG).show();
                }
            }

        }
    }

    //===============================================
    //=================== SOUNDS ====================
    //===============================================

    /**
     * finish sound
     * @param delay
     */
    public void playSond(int delay, final String soundFile) {

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
                    afd = assets.openFd(soundFile);
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

    //===============================================
    //=================== POPUPS ====================
    //===============================================

    /**
     * The popup displayed when we got stairs + vribration
     * @param delay
     * @param direction
     */
    public void stairsPopup(int delay, String direction) {

        if(direction.equals("up")) {
            popUpMessage = "Veuillez s'il-vous-plaît monter les escaliers avec Bouddha et le repositionner sur la pastille rouge. Scannez ensuite le prochain QRCode.";
        }
        else if (direction.equals("down")) {
            popUpMessage = "Veuillez s'il-vous-plaît descendre les escaliers avec Bouddha et le repositionner sur la pastille rouge. Scannee ensuite le prochain QRCode.";
        } else {
            popUpMessage = "Erreur !";
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            public void run() {

                final float scale = getResources().getDisplayMetrics().density;

                //Add layout image
                LayoutInflater factory = LayoutInflater.from(Sphero.this);
                final View view = factory.inflate(R.layout.popup, null);

                //Switch image
                ImageView image = (ImageView) view.findViewById(R.id.dialog_imageview);
                image.setImageResource(R.drawable.pastille2);
                image.getLayoutParams().height = (int) (189 * scale);
                image.getLayoutParams().width = (int) (190 * scale);
                image.requestLayout();

                //popup
                AlertDialog alertDialog = new AlertDialog.Builder(Sphero.this).create();
                alertDialog.setTitle("Attention un escalier !");
                alertDialog.setMessage(popUpMessage);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setView(view);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                alertDialog.show();

                //Center the text
                TextView messageText = (TextView)alertDialog.findViewById(android.R.id.message);
                messageText.setGravity(Gravity.CENTER);

                //Vibration
                Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);
            }
        }, (delay + delay));
    }


    /**
     * Start instructions popups
     * @param id
     * @param title
     * @param text
     * @param imageInt
     * @param height
     * @param width
     */
    public void instructionsPopup(final int id, final String title, final String text, final int imageInt, final int height, final int width) {

        runOnUiThread(new Runnable() {
            public void run() {

                final float scale = getResources().getDisplayMetrics().density;

                //Add layout image
                LayoutInflater factory = LayoutInflater.from(Sphero.this);
                final View view = factory.inflate(R.layout.popup, null);

                //Switch image
                ImageView image = (ImageView) view.findViewById(R.id.dialog_imageview);
                image.setImageResource(imageInt);
                image.getLayoutParams().height = (int) (height * scale);
                image.getLayoutParams().width = (int) (width * scale);
                image.requestLayout();

                //popup
                AlertDialog alertDialog = new AlertDialog.Builder(Sphero.this).create();
                alertDialog.setTitle(title);
                alertDialog.setMessage(text);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setView(view);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(id == 0) {
                                    String message = "Une fois la boule positionnée et connectée, mettez-vous derrière la sphère et utilisez le bouton rond pour calibrer Bouddha comme sur l'image.";
                                    instructionsPopup(1, "Instructions", message, R.drawable.calibration, 166, 158);
                                } else if ( id == 1) {
                                    String message = "Une fois que Bouddha sera bien calibré, vous pourrez appuyer sur le bouton \"GO\" et vous commencerez à suivre la boule ! En cas de problème, vous pouvez toujours appuyer sur le bouton \"STOP\" pour arrêter la sphère.";
                                    instructionsPopup(2, "Instructions", message, R.drawable.menu, 166, 318);
                                } else if ( id == 2) {
                                    String message = "Lorsque celle-ci s'arrêtera, il vous faudra scanner le QRCode le plus proche à l'aide du bouton \"SCAN\"";
                                    instructionsPopup(3, "Instructions", message, R.drawable.menu2, 77, 318);
                                }
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
        });

    }

    public void endPopup(int delay) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            public void run() {

                //popup
                AlertDialog alertDialog = new AlertDialog.Builder(Sphero.this).create();
                alertDialog.setTitle("Fin du parcours !");
                alertDialog.setMessage("Vous êtes bien arrivé à destination.");
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                alertDialog.show();

                //Center the text
                TextView messageText = (TextView)alertDialog.findViewById(android.R.id.message);
                messageText.setGravity(Gravity.CENTER);
            }
        }, (delay + delay));
    }

    //===============================================
    //================ BASIC ON... ==================
    //===============================================
    @Override
    protected void onStart() {
        super.onStart();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if( Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkSelfPermission( Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {
                startDiscovery();
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
}