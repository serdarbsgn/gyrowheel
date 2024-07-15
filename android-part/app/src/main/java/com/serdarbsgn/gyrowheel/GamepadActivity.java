package com.serdarbsgn.gyrowheel;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
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
    private boolean useSensor = true;
    HashMap<Integer, ArrayList<Integer>> buttonPlacement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); This makes onCreate run twice.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //If use custom layout switch is on, will try to use custom layout, otherwise use the default layout.
        if (getIntent().getBooleanExtra("CUSTOM_LAYOUT",false)) {
            setContentView(R.layout.activity_editable);
            initButtonPlacement();
        } else {
            setContentView(R.layout.activity_gamepad);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        socketAddress = getIntent().getStringExtra("SOCKET_IP");
        useBluetooth = getIntent().getBooleanExtra("USE_BLUETOOTH",false);
        if (!useBluetooth) {
            socketClient = new UDPOverInternet(socketAddress, 12345);
        }else{
            initBluetooth();
        }
        buttons = getIntegerIntegerHashMap();
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

    private void initButtonPlacement(){
        buttonPlacement = loadButtonPositions();//Try to read button positions from txt.
        for (Map.Entry<Integer, ArrayList<Integer>> entry : buttonPlacement.entrySet()) {
            Integer key = entry.getKey();
            ArrayList<Integer> position = entry.getValue();

            // Find the button by its ID
            View button = findViewById(key);
            if (button != null) {
                // Update button's layout position
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
                params.leftMargin = position.get(0);
                params.topMargin = position.get(1);
                button.setScaleX((float) position.get(2) /100);
                button.setScaleY((float) position.get(2) /100);
                button.setLayoutParams(params);
            }
        }
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
        // Create a SensorEventListener to listen for rotation vector data
        rotationVectorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                    int temPitch = 0;
                    //int tempAzimuth=0;
                    int tempRoll = 0;
                    if (ALX == 0 && ALY == 0 && useSensor) {
                    // Convert the rotation-vector to a 4x4 matrix.
                    float[] rotationMatrix = new float[9];
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

                    // Calculate orientation angles.
                    float[] orientationAngles = new float[3];
                    SensorManager.getOrientation(rotationMatrix, orientationAngles);

                    float azimuth = orientationAngles[0]; // Rotation around the Z axis
                    float pitch = orientationAngles[1];   // Rotation around the X axis
                    float roll = orientationAngles[2];    // Rotation around the Y axis

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
                            ALX = Math.min(Math.round(diffX * 327), 32767);
                        } else {
                            ALX = Math.max(Math.round(diffX * 327), -32767);
                        }
                        if (diffY > 0) {
                            ALY = Math.min(Math.round(diffY * 327), 32767);
                        } else {
                            ALY = Math.max(Math.round(diffY * 327), -32767);
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

        Switch switchSensor = findViewById(R.id.switchSensor);
        switchSensor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                useSensor = !isChecked;
                if (useSensor) {
                    Toast.makeText(getApplicationContext(), "Phone rotation sensor data for left analog is enabled.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Phone rotation sensor data for left analog is disabled.", Toast.LENGTH_SHORT).show();
                }
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

    private HashMap<Integer, ArrayList<Integer>> loadButtonPositions() {
        HashMap<Integer, ArrayList<Integer>> buttons = getIntegerIntegerArrayHashMap();

        try (FileInputStream fis = openFileInput("button_positions.txt")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {  // Expecting key, x, y, size
                    Integer key = Integer.parseInt(parts[0]);
                    Integer x = Integer.parseInt(parts[1]);
                    Integer y = Integer.parseInt(parts[2]);
                    Integer size = Integer.parseInt(parts[3]);
                    buttons.put(key, new ArrayList<>(Arrays.asList(x, y, size)));
                }
            }
        } catch (IOException e) {
            Toast.makeText(this,"Use Edit Layout button to set a layout first.",Toast.LENGTH_SHORT).show();
        }

        return buttons;
    }

    private @NonNull HashMap<Integer, ArrayList<Integer>> getIntegerIntegerArrayHashMap() {
        HashMap<Integer, ArrayList<Integer>> buttons = new HashMap<>();

        buttons.put(R.id.buttonLB, new ArrayList<>(Arrays.asList(0, 0, 100)));
        buttons.put(R.id.buttonLT, new ArrayList<>(Arrays.asList(0, 0, 100)));
        buttons.put(R.id.buttonL3, new ArrayList<>(Arrays.asList(0, 0, 100)));

        buttons.put(R.id.buttonRB, new ArrayList<>(Arrays.asList(0, 0, 100)));
        buttons.put(R.id.buttonRT, new ArrayList<>(Arrays.asList(0, 0, 100)));
        buttons.put(R.id.buttonR3, new ArrayList<>(Arrays.asList(0, 0, 100)));

        buttons.put(R.id.buttonBack, new ArrayList<>(Arrays.asList(0, 0, 100)));
        buttons.put(R.id.buttonStart, new ArrayList<>(Arrays.asList(0, 0, 100)));

        buttons.put(R.id.buttonY, new ArrayList<>(Arrays.asList(0, 0, 100)));
        buttons.put(R.id.buttonX, new ArrayList<>(Arrays.asList(0, 0, 100)));
        buttons.put(R.id.buttonB, new ArrayList<>(Arrays.asList(0, 0, 100)));
        buttons.put(R.id.buttonA, new ArrayList<>(Arrays.asList(0, 0, 100)));

        buttons.put(R.id.buttonAU, new ArrayList<>(Arrays.asList(0, 0, 100)));
        buttons.put(R.id.buttonAL, new ArrayList<>(Arrays.asList(0, 0, 100)));
        buttons.put(R.id.buttonAR, new ArrayList<>(Arrays.asList(0, 0, 100)));
        buttons.put(R.id.buttonAD, new ArrayList<>(Arrays.asList(0, 0, 100)));

        buttons.put(R.id.buttonAUL, new ArrayList<>(Arrays.asList(0, 0, 100)));
        buttons.put(R.id.buttonAUR, new ArrayList<>(Arrays.asList(0, 0, 100)));
        buttons.put(R.id.buttonADL, new ArrayList<>(Arrays.asList(0, 0, 100)));
        buttons.put(R.id.buttonADR, new ArrayList<>(Arrays.asList(0, 0, 100)));

        buttons.put(R.id.rightAnalog, new ArrayList<>(Arrays.asList(0, 0, 100)));
        buttons.put(R.id.leftAnalog, new ArrayList<>(Arrays.asList(0, 0, 100)));
        buttons.put(R.id.switchSensor, new ArrayList<>(Arrays.asList(0, 0, 100)));

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
        //If using Network mode, close the socket.
        if(socketAddress!=null){
            socketClient.close();
        }
        // Unregister the sensor listener
        if(sensorManager != null){
        sensorManager.unregisterListener(rotationVectorListener);
        }
        if (bluetoothConn!=null){
            //To return the state of controller to neutral on all buttons, for convenience.
            bluetoothConn.sendData("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0".getBytes());
        }
    }
}
