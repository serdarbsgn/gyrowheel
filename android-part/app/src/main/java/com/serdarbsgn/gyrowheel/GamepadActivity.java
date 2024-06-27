package com.serdarbsgn.gyrowheel;

import android.bluetooth.BluetoothAdapter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class GamepadActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private SensorEventListener rotationVectorListener;
    HashMap<Integer, Integer> buttons;
    int ALX,ALY,ARX,ARY;
    private String socketAddress;
    private boolean useBluetooth;
    private UDPOverInternet socketClient;
    private BluetoothConn bluetoothConn;
    private BluetoothAdapter bluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); This makes onCreate run twice.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_gamepad);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Get the IP address passed from MainActivity
        socketAddress = getIntent().getStringExtra("SOCKET_IP");
        useBluetooth = getIntent().getBooleanExtra("USE_BLUETOOTH",false);
        // Initialize the SensorManager
        if (!useBluetooth) {
            socketClient = new UDPOverInternet(socketAddress, 12345);
        }else{
            initBluetooth();
        }
        // LB,LT,L3,RB,RT,R3,BC,ST,Y,X,B,A;
        // Set up buttons and their touch listeners
    }
    @Override
    protected void onStart() {
        super.onStart();
        initSensors();
        initButtons();
    }
    private void initBluetooth(){
        bluetoothConn = BluetoothConn.getInstance();
    }
    private void initSensors(){

        // Get the rotation vector sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        // Check if the rotation vector sensor is available
        if (rotationVectorSensor == null) {
            Log.e("RotationSensor", "Rotation Vector Sensor not available");
            return;
        }
        buttons = getIntegerIntegerHashMap();
        // Create a SensorEventListener to listen for rotation vector data
        rotationVectorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {

                    // Convert the rotation-vector to a 4x4 matrix.
                    float[] rotationMatrix = new float[9];
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

                    // Calculate orientation angles.
                    float[] orientationAngles = new float[3];
                    SensorManager.getOrientation(rotationMatrix, orientationAngles);

                    float azimuth = orientationAngles[0]; // Rotation around the Z axis
                    float pitch = orientationAngles[1];   // Rotation around the X axis
                    float roll = orientationAngles[2];    // Rotation around the Y axis

                    // Create a JSON object to hold the sensor data and button states
                    int temPitch = 0;
                    //int tempAzimuth=0;
                    int tempRoll = 0;
                    if (ALX == 0 && ALY == 0) {
                        if (pitch > 0) {
                            temPitch = -Math.min(Math.round(pitch * 32767), 32767);
                        } else {
                            temPitch = -Math.max(Math.round(pitch * 32767), -32767);
                        }
                        tempRoll = 32767 - Math.abs(Math.round(roll * 21844));
//                    long currentTimeMillis = System.currentTimeMillis();
                    } else {
                        temPitch = ALX;
                        tempRoll = -ALY;
                    }


                    String sensorData = String.format(Locale.US,
                            "%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d",
                            tempRoll,
                            temPitch,
                            buttons.get(R.id.buttonLB),
                            buttons.get(R.id.buttonLT),
                            buttons.get(R.id.buttonL3),
                            buttons.get(R.id.buttonRB),
                            buttons.get(R.id.buttonRT),
                            buttons.get(R.id.buttonR3),
                            buttons.get(R.id.buttonBack),
                            buttons.get(R.id.buttonStart),
                            buttons.get(R.id.buttonY),
                            buttons.get(R.id.buttonX),
                            buttons.get(R.id.buttonB),
                            buttons.get(R.id.buttonA),
                            buttons.get(R.id.buttonAU),
                            buttons.get(R.id.buttonAL),
                            buttons.get(R.id.buttonAR),
                            buttons.get(R.id.buttonAD),
                            ARX, ARY
                    );
                    if(!useBluetooth){
                        socketClient.sendData(sensorData);
                    }else if (bluetoothConn!=null){
                        bluetoothConn.sendData(sensorData.getBytes());
                    }
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // You can react to accuracy changes here if needed
            }
        };

        // Register the listener
        sensorManager.registerListener(rotationVectorListener, rotationVectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }
    private void initButtons(){
        for (Map.Entry<Integer, Integer> button : buttons.entrySet()) {
            findViewById(button.getKey()).setOnTouchListener((v, event) -> {
                findViewById(button.getKey()).performClick();
                button.setValue(Math.min(1, Math.abs(event.getAction() - 1)));
                return true;
            });
        }
        findViewById(R.id.leftAnalog).setOnTouchListener(new View.OnTouchListener() {
            private float startX, startY; // To store initial touch coordinates

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX(); // Record initial X coordinate
                        startY = event.getY(); // Record initial Y coordinate
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float currentX = event.getX();
                        float currentY = event.getY();

                        // Calculate the difference from start position
                        float diffX = currentX - startX;
                        float diffY = currentY - startY;
                        if (diffX > 0) {
                            ALX = Math.min(Math.round(diffX * 165), 32767);
                        } else {
                            ALX = Math.max(Math.round(diffX * 165), -32767);
                        }
                        if (diffY > 0) {
                            ALY = Math.min(Math.round(diffY * 165), 32767);
                        } else {
                            ALY = Math.max(Math.round(diffY * 165), -32767);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        ALX = 0;
                        ALY = 0;
                        break;
                }
                return true; // Consume the touch event
            }
        });

        findViewById(R.id.rightAnalog).setOnTouchListener(new View.OnTouchListener() {
            private float startX, startY; // To store initial touch coordinates
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX(); // Record initial X coordinate
                        startY = event.getY(); // Record initial Y coordinate
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float currentX = event.getX();
                        float currentY = event.getY();

                        // Calculate the difference from start position
                        float diffX = currentX - startX;
                        float diffY = currentY - startY;
                        if (diffX > 0) {
                            ARX = Math.min(Math.round(diffX * 327), 32767);
                        } else {
                            ARX = Math.max(Math.round(diffX * 327), -32767);
                        }
                        if (diffY > 0) {
                            ARY = Math.min(Math.round(diffY * 327), 32767);
                        } else {
                            ARY = Math.max(Math.round(diffY * 327), -32767);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        ARX = 0;
                        ARY = 0;
                        break;
                }
                return true; // Consume the touch event
            }
        });

        findViewById(R.id.buttonAUL).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                buttons.put(R.id.buttonAU, Math.abs(event.getAction() - 1));
                buttons.put(R.id.buttonAL, Math.abs(event.getAction() - 1));
                return true; // To consume the event
            }
        });

        findViewById(R.id.buttonAUR).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                buttons.put(R.id.buttonAU, Math.abs(event.getAction() - 1));
                buttons.put(R.id.buttonAR, Math.abs(event.getAction() - 1));
                return true; // To consume the event
            }
        });

        findViewById(R.id.buttonADL).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                buttons.put(R.id.buttonAD, Math.abs(event.getAction() - 1));
                buttons.put(R.id.buttonAL, Math.abs(event.getAction() - 1));
                return true; // To consume the event
            }
        });

        findViewById(R.id.buttonADR).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                buttons.put(R.id.buttonAD, Math.abs(event.getAction() - 1));
                buttons.put(R.id.buttonAR, Math.abs(event.getAction() - 1));
                return true; // To consume the event
            }
        });
    }
    private @NonNull HashMap<Integer, Integer> getIntegerIntegerHashMap() {
        HashMap<Integer,Integer> buttons = new HashMap<>();

        buttons.put(R.id.buttonLB,0);
        buttons.put(R.id.buttonLT,0);
        buttons.put(R.id.buttonL3,0);

        buttons.put(R.id.buttonRB,0);
        buttons.put(R.id.buttonRT,0);
        buttons.put(R.id.buttonR3,0);

        buttons.put(R.id.buttonBack,0);
        buttons.put(R.id.buttonStart,0);

        buttons.put(R.id.buttonY,0);
        buttons.put(R.id.buttonX,0);
        buttons.put(R.id.buttonB,0);
        buttons.put(R.id.buttonA,0);

        buttons.put(R.id.buttonAU,0);
        buttons.put(R.id.buttonAL,0);
        buttons.put(R.id.buttonAR,0);
        buttons.put(R.id.buttonAD,0);
        return buttons;
    }

    protected void onPause() {
        super.onPause();
        finish();
    }

    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the sensor listener
        if(socketAddress!=null){
            socketClient.close();
        }
        if(sensorManager != null){
        sensorManager.unregisterListener(rotationVectorListener);
        }
        if (bluetoothConn!=null){
            bluetoothConn.sendData("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0".getBytes());
        }
    }
}
