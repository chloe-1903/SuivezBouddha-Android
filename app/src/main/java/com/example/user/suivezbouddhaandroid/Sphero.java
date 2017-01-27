package com.example.user.suivezbouddhaandroid;

/**
 * Created by lucas on 16/12/16.
 */

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
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
import com.orbotix.calibration.api.CalibrationEventListener;
import com.orbotix.calibration.api.CalibrationImageButtonView;
import com.orbotix.calibration.api.CalibrationView;
import com.orbotix.classic.DiscoveryAgentClassic;
import com.orbotix.command.RollCommand;
import com.orbotix.common.DiscoveryAgent;
import com.orbotix.common.DiscoveryAgentEventListener;
import com.orbotix.common.DiscoveryException;
import com.orbotix.common.Robot;
import com.orbotix.common.RobotChangedStateListener;
import com.orbotix.le.RobotLE;
import com.orbotix.macro.MacroObject;
import com.orbotix.macro.cmd.Delay;
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

import static com.orbotix.common.RobotChangedStateListener.RobotChangedStateNotificationType.Connected;


/**
 * Hello World Sample
 * Connect either a Bluetooth Classic or Bluetooth LE robot to an Android Device, then
 * blink the robot's LED on or off every two seconds.
 *
 * This example also covers turning on Developer Mode for LE robots.
 */

public class Sphero extends Activity implements RobotChangedStateListener, View.OnClickListener, Observer, DiscoveryAgentEventListener {

    private ConvenienceRobot mRobot;
    private Button stopButton;
    private Button scanButton;
    private Button perduButton;
    private CalibrationView _calibrationView;
    private CalibrationImageButtonView _calibrationButtonView;

    private DiscoveryAgent _currentDiscoveryAgent;
    private RobotChangedStateNotificationType typeGlobal;
    private MacroObject macro;

    private Client client;
    private JSONObject jsonObject;
    private String popUpMessage;
    private String roomSelectedId;
    private int QRCodeIDFromSelectedRoom;
    private int QRCodeID = -1;

    private AssetManager assets;
    private MediaPlayer mp;

    private boolean macroStopped = false;
    private boolean iAmLost = false;
    private boolean discoveryFailed = false;

    // the first scan to perform
    private boolean first = true;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 42;

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
        _currentDiscoveryAgent = DiscoveryAgentClassic.getInstance();
        _currentDiscoveryAgent.addRobotStateListener( this );

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

        //For the calibration button
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

        //init mediaPlayer
        mp = new MediaPlayer();

        //Store data from the file into variables
        roomSelectedId = Utils.readFile("RoomSelected.txt");
        String[] roomSelectedIdTab = roomSelectedId.split(";");
        roomSelectedId = roomSelectedIdTab[0];
        QRCodeIDFromSelectedRoom = Integer.parseInt(roomSelectedIdTab[3]);
    }

    /**
     * Find every buttons we need and set the unclickable until the sphero is connected
     */
    private void initViews() {

        stopButton = (Button) findViewById(R.id.stop);
        scanButton = (Button) findViewById(R.id.scan);
        perduButton = (Button) findViewById(R.id.perdu);

        stopButton.setOnClickListener( this );
        scanButton.setOnClickListener( this );
        perduButton.setOnClickListener( this );

        //Disable buttons
        switchButtonState(scanButton, false);
        switchButtonState(stopButton, false);
        switchButtonState(perduButton, false);
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
        if( !_currentDiscoveryAgent.isDiscovering() ) {
            try {
                _currentDiscoveryAgent.startDiscovery(getApplicationContext());
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
        typeGlobal = type;
        Log.d("Sphero", "States : "+ String.valueOf(type));
        Toast.makeText(getApplicationContext(), String.valueOf(type), Toast.LENGTH_SHORT).show();
        switch( typeGlobal ) {
            case Online: {
                //Instructions Popups
                instructionsPopup();

                _currentDiscoveryAgent.stopDiscovery();
                _currentDiscoveryAgent.removeDiscoveryListener(this);

                //If robot uses Bluetooth LE, Developer Mode can be turned on.
                //This turns off DOS protection. This generally isn't required.
                if( robot instanceof RobotLE) {
                    ( (RobotLE) robot ).setDeveloperMode( true );
                }

                //Save the robot as a ConvenienceRobot for additional utility methods
                mRobot = new ConvenienceRobot( robot );

                //Start blinking the robot's LED
                blink( false );

                //Connection to the server
                Log.d("Sphero", "Connexion au serveur");
                client = new Client();
                client.addObserver(this);
                client.connect();

                //Check if the client is connected
                while(!client.isConnected());

                //Set buttons enable
                switchButtonState(scanButton, true);
                _calibrationView.setEnabled(true);
                _calibrationButtonView.setAlpha(1f);
                _calibrationButtonView.setEnabled(true);

                break;
            }
            case Connecting: {
                break;
            }
            case Connected: {

                //If we stay in this states a too long moment, we try to reconnect it again
                runOnUiThread(new Runnable() {
                    public void run() {
                        new CountDownTimer(5000, 5000) {
                            public void onTick(long millisUntilFinished) {}

                            public void onFinish() {
                                if (typeGlobal == Connected) {
                                    Log.i("Sphero", "Planté !");

                                    //Disconnect and reconnect bluetooth
                                    discoveryFailed = true;
                                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                    Intent intentBtEnabled = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                                    bluetoothAdapter.disable();
                                    startActivityForResult(intentBtEnabled, 0);
                                }
                            }
                        }.start();
                    }
                });

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
     * Disconnect the Sphero
     */
    public void disconnectRobot() {

        Log.d("Sphero", "disconnectRobot");

        if( _currentDiscoveryAgent.isDiscovering() ) {
            _currentDiscoveryAgent.stopDiscovery();
        }

        _currentDiscoveryAgent.removeRobotStateListener(this);

        //If a robot is connected to the device, disconnect it
        if( mRobot != null ) {
            mRobot.disconnect();
            mRobot = null;
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
            case R.id.stop: {
                //Stop the macro, so stop the sphero
                macro.stopMacro();
                mRobot.sendCommand(new RollCommand(0, 0.0f, RollCommand.State.STOP));

                //True, so popups won't be display at the end of the macro delay
                macroStopped = true;

                //Enable scan
                switchButtonState(scanButton, true);
                //Disable stop
                switchButtonState(stopButton, false);
                break;
            }
            case R.id.scan: {
                //Start scan activity
                Intent myIntent = new Intent(getApplicationContext(), ScanActivity.class);
                try {
                    if (first) {
                        myIntent.putExtra("arrowDir", "none");
                        first = false;
                    } else {
                        myIntent.putExtra("arrowDir", jsonObject.getString("arrow"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivityForResult(myIntent, 1);

                break;
            }
            case R.id.perdu: {
                //I can now scan whatever QRCode i want, to recalculate the road
                iAmLost = true;

                //Start scan activity
                Intent myIntent = new Intent(getApplicationContext(), ScanActivity.class);
                startActivityForResult(myIntent, 1);
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

                //Disable buttons
                switchButtonState(scanButton, false);
                switchButtonState(stopButton, false);
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

                //Enable buttons
                switchButtonState(scanButton, true);
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

    /**
     * Unlock scan when macro end and lock stop
     * @param delay
     */
    public void unlockScanAfterMacroEnd(int delay) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            public void run() {
                //Enable scan
                switchButtonState(scanButton, true);
                switchButtonState(stopButton, false);
            }
        }, (delay + delay));
    }

    /**
     * Switch button state (enable or disable)
     * @param button
     * @param state
     */
    public void switchButtonState(Button button, boolean state){

        if(state) {
            button.setAlpha(1f);
            button.setClickable(true);
        } else {
            button.setClickable(false);
            button.setAlpha(0.5f);
        }
    }

    //===============================================
    //=================== ANSWERS ===================
    //===============================================

    /**
     * Update when the server answer after we ask a direction
     * @param observable
     * @param o
     */
    @Override
    public void update(Observable observable, Object o) {

        JSONArray directions;
        JSONObject instruction;
        float speed; int direction; int delay; int delayTotal = 0;
        boolean finish; String stairs;

        //get the direction JSON and the finish var
        jsonObject = client.getDirections();
        finish = client.isFinish();

        //If we don't have object yet
        if(jsonObject==null) return;

        Log.d("Sphero", "New direction !");

        //read the json
        try {
            directions = jsonObject.getJSONArray("directions");
            //Get the next QRCodeID
            QRCodeID =  jsonObject.getInt("qrcodeId");

            Log.i("Sphero", "Prochain QRCode : "+QRCodeID);

            macro = new MacroObject();

            //Add instructions to the macro
            for(int y = 0; y < directions.length(); y++) {
                instruction = directions.getJSONObject(y);

                if (instruction==null) return;

                speed = Float.parseFloat(instruction.getString("speed"));
                direction = instruction.getInt("direction");
                delay = instruction.getInt("delay");
                delayTotal += delay;

                macro.addCommand( new Roll(speed, direction, delay));
                macro.addCommand( new Delay(delay));
            }

            //Add a stop at the end of the macro
            macro.addCommand( new Roll(0.0f, 0, 0));

            //We check if the run is over
            if(finish) {
                Log.d("Sphero", "finished ! ");
                macro.addCommand( new RGB(0, 255, 0, 10000));
                macro.addCommand( new Delay( 10000 ) );
                playSond(delayTotal, "R2D2_finish.mp3");
                endPopup(delayTotal);
            } else {
                //unlock scan
                unlockScanAfterMacroEnd(delayTotal);
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

        //Check codes...
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                //We will start an other macro, so this is no more true
                macroStopped = false;

                //Get data from intent return
                boolean dataBool = data.getBooleanExtra("data", false);
                int QRCodeIDRevied = data.getIntExtra("index", -2);

                //Check if scan is OK and if the QRCode is the good one
                //If the QRCodeID == -1, it's mean that it's the 1st we scan, so we enter in
                Log.d("Sphero", "QRCodeID : "+QRCodeID + ", QRCodeIDRevied : "+QRCodeIDRevied);
                Log.d("Sphero", "Point final : "+roomSelectedId+ ", "+QRCodeIDFromSelectedRoom);

                if( (dataBool && QRCodeIDRevied == QRCodeID) || ( QRCodeID == -1) || (iAmLost)) {

                    Log.i("Sphero", "Chemin : "+String.valueOf(QRCodeIDRevied) + " "+ String.valueOf(QRCodeIDFromSelectedRoom));

                    //I'm no more lost
                    iAmLost = false;
                    switchButtonState(perduButton, false);

                    //Check if we are not already in the current room
                    if(QRCodeIDFromSelectedRoom == QRCodeIDRevied) {
                        Toast.makeText(getApplicationContext(), "Vous êtes déjà à destination !", Toast.LENGTH_LONG).show();
                    } else {
                        if (QRCodeIDFromSelectedRoom != -1) { //Case if the room have no QRCode yet

                            //Disable scan
                            switchButtonState(scanButton, false);
                            //Enable stop button
                            switchButtonState(stopButton, true);

                            //Only the 1st time
                            if (QRCodeID == -1) {
                                //Disable calibration button
                                _calibrationView.setEnabled(false);
                                _calibrationButtonView.setAlpha(0.5f);
                                _calibrationButtonView.setClickable(false);

                                //Play a sound
                                playSond(0, "R2D2Scream.mp3");
                            }

                            //Ask the way to take
                            client.askDirection(String.valueOf(QRCodeIDRevied), String.valueOf(QRCodeIDFromSelectedRoom));
                        } else {
                            Log.e("Sphero", "Cette salle n'a pas de QRCodeID !");
                            Toast.makeText(getApplicationContext(), "Oups ! Nous ne pouvons pas encore aller dans cette salle.", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Vous n'avez pas scanné le bon QRCode pour le parcours sélectionné. Etes-vous perdu ? ", Toast.LENGTH_LONG).show();
                    //Enable lost button
                    switchButtonState(perduButton, true);
                }

            }

        }
    }

    //===============================================
    //=================== SOUNDS ====================
    //===============================================

    /**
     * Play a sound with a delay
     * @param delay
     */
    public void playSond(int delay, final String soundFile) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            public void run() {

                //Not executed if user press stop button
                if(!macroStopped) {
                    if (mp.isPlaying()) {
                        mp.stop();
                    }

                    try {
                        mp.reset();
                        AssetFileDescriptor afd;
                        afd = assets.openFd(soundFile);
                        mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                        mp.prepare();
                        mp.start();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, (delay + delay));
    }

    //===============================================
    //=================== POPUPS ====================
    //===============================================

    /**
     * The popup displayed when we got stairs + vibration
     * @param delay
     * @param direction
     */
    public void stairsPopup(int delay, String direction) {

        if(direction.equals("up")) {
            popUpMessage = "Veuillez s'il-vous-plaît monter les escaliers avec Bouddha et le repositionner sur la pastille rouge. Scannez ensuite le prochain QRCode.";
        }
        else if (direction.equals("down")) {
            popUpMessage = "Veuillez s'il-vous-plaît descendre les escaliers avec Bouddha et le repositionner sur la pastille rouge. Scannez ensuite le prochain QRCode.";
        } else {
            popUpMessage = "Erreur !";
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            public void run() {

                //Not executed if user press stop button
                if(!macroStopped) {
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

                    //popup params
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

                    //show popup
                    alertDialog.show();

                    //Center the text
                    TextView messageText = (TextView) alertDialog.findViewById(android.R.id.message);
                    messageText.setGravity(Gravity.CENTER);

                    //Vibration
                    Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(500);
                }
                else {
                    macroStopped = false;
                }
            }
        }, (delay + delay));
    }

    /**
     * Classic popup with image
     * @param title
     * @param text
     * @param imageInt
     * @param height
     * @param width
     */
    public void imgPopup(final String title, final String text, final int imageInt, final int height, final int width) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
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

                //popup params
                AlertDialog alertDialog = new AlertDialog.Builder(Sphero.this).create();
                alertDialog.setTitle(title);
                alertDialog.setMessage(text);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setView(view);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //nothing, just close automatically
                            }
                        });
                //To avoid double popup problem
                try {
                    //Show popup
                    alertDialog.show();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                //Center the text
                TextView messageText = (TextView)alertDialog.findViewById(android.R.id.message);
                messageText.setGravity(Gravity.CENTER);
            }
        }, (0));
    }

    /**
     * Start instructions popups
     */
    public void instructionsPopup() {
        //Pop 4
        String message = "Lorsque celle-ci s'arrêtera, il vous faudra scanner le QRCode le plus proche à l'aide du bouton \"SCAN\". Si vous êtes perdu, pas d'inquiétude, vous pourrez utiliser le bouton \"JE SUIS PERDU\".";
        imgPopup("Instructions", message, R.drawable.menu2, 77, 318);

        //Pop 3
        message = "Une fois que Bouddha sera bien calibré, vous pourrez appuyer sur le bouton \"SCAN\" pour scanner le QRCode le plus proche et vous commencerez à suivre la boule ! En cas de problème, vous pouvez toujours appuyer sur le bouton \"STOP\" pour arrêter la sphère.";
        imgPopup("Instructions", message, R.drawable.menu, 180, 318);

        //Pop 2
        message = "Une fois la boule positionnée et connectée, mettez-vous derrière la sphère et utilisez le bouton rond pour calibrer Bouddha comme sur l'image. Cette opération n'est a effectuer uniquement lors du premier départ à l'accueil.";
        imgPopup("Instructions", message, R.drawable.calibration, 166, 158);

        //Pop 1
        message = "Bienvenue dans l'utilisation de Bouddha ! Commencez par positionner la sphère sur la pastille rouge du point de départ.";
        imgPopup("Instructions", message, R.drawable.pastille2, 189, 190);
    }

    /**
     * End popup
     * @param delay
     */
    public void endPopup(int delay) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            public void run() {

                //Not executed if user press stop button
                if(!macroStopped) {
                    //popup params
                    AlertDialog alertDialog = new AlertDialog.Builder(Sphero.this).create();
                    alertDialog.setTitle("Fin du parcours !");
                    alertDialog.setMessage("Vous êtes arrivé à destination.");
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    //show popup
                    alertDialog.show();

                    //Center the text
                    TextView messageText = (TextView) alertDialog.findViewById(android.R.id.message);
                    messageText.setGravity(Gravity.CENTER);

                    //disable stop
                    switchButtonState(stopButton, false);

                    //Disconnect the sphero
                    disconnectRobot();
                }
                else {
                    macroStopped = false;
                }
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
        //We don't play this code anymore, so we can scan in the activity without being disconnected

        //If the DiscoveryAgent is in discovery mode, stop it.
        /*
        if( _currentDiscoveryAgent.isDiscovering() ) {
            _currentDiscoveryAgent.stopDiscovery();
        }

        //If a robot is connected to the device, disconnect it
        if( mRobot != null ) {
            mRobot.disconnect();
            mRobot = null;
        }
        */
        //Log.i("Sphero", "onStop");
        super.onStop();
    }

    @Override
    protected void onPause() {
        //Log.i("Sphero", "onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.i("Sphero", "onResume");

        //If the discovery failed, we start it again
        if(discoveryFailed) {
            startDiscovery();
            discoveryFailed = false;
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {

        Log.i("Sphero", "onDestroy");

        disconnectRobot();

        super.onDestroy();
        _currentDiscoveryAgent.addRobotStateListener(null);
    }

    @Override
    public void handleRobotsAvailable(List<Robot> list) {}
}