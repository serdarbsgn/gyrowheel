package com.serdarbsgn.gyrowheel;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.Locale;


public class GyroWheelActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private SensorEventListener rotationVectorListener,gravityListener,accelerometerListener;
    private int isButtonAPressed = 0;
    private int isButtonBPressed = 0;
    private String socketAddress;
    private UDPOverInternet socketClient;
    int temPitch, tempRoll;

    private boolean useBluetooth;
    private BluetoothConn bluetoothConn;
    private Handler handler;
    private Runnable dataSender;
    private int multiplier =25;
    private static float ALPHA = 0.4f; // Smoothing factor(Lower is smoother but delayed.)
    private float filteredX = 0; // Smoothed x value
    private float filteredY = 0; // Smoothed y value
    private static final String PREFS_NAME = "com.serdarbsgn.gyrowheel.PREFS";
    private static final String KEY_SENSOR_MULTIPLIER = "SENSOR_MULTIPLIER";
    private static final String KEY_SMOOTH_MULTIPLIER = "SMOOTH_MULTIPLIER";
    private static final String KEY_TRIGGER_MULTIPLIER = "TRIGGER_MULTIPLIER";
    private static final String KEY_USE_ANALOG_TRIGGER = "USE_ANALOG_TRIGGER";

    private float triggerMultiplier = 1f;
    private boolean useAnalogTrigger = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_gyrowheel);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        socketAddress = getIntent().getStringExtra("SOCKET_IP");
        useBluetooth = getIntent().getBooleanExtra("USE_BLUETOOTH",false);
        // Initialize the SensorManager
        if (!useBluetooth) {
            socketClient = new UDPOverInternet(socketAddress, 12345);
        }else{
            initBluetooth();
        }

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
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        multiplier = sharedPreferences.getInt(KEY_SENSOR_MULTIPLIER, 25);
        ALPHA = sharedPreferences.getInt(KEY_SMOOTH_MULTIPLIER,4)/10f;
        triggerMultiplier = sharedPreferences.getInt(KEY_TRIGGER_MULTIPLIER,20)/20f;
        useAnalogTrigger = sharedPreferences.getBoolean(KEY_USE_ANALOG_TRIGGER,false);

        socketAddress = getIntent().getStringExtra("SOCKET_IP");
        // Initialize the SensorManager
        socketClient = new UDPOverInternet(socketAddress, 12345);
        // Get the rotation vector sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        Sensor gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (gravitySensor == null) {
            if (rotationVectorSensor == null) {
                if (accelerometerSensor == null) {
                    Toast.makeText(this, getString(R.string.no_rotation_sensor), Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }else{
                    accelerometerListener = new SensorEventListener() {

                        @Override
                        public void onSensorChanged(SensorEvent event) {
                            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                                float x = event.values[1];
                                float y = event.values[2];

                                // Apply the low-pass filter
                                filteredX = ALPHA * x + (1 - ALPHA) * filteredX;
                                filteredY = ALPHA * y + (1 - ALPHA) * filteredY;

                                int axisY = Math.round(filteredX * 131* multiplier);
                                int axisZ = Math.round(filteredY * 131* multiplier);
                                temPitch = (axisY > 0) ? Math.min(axisY, 32767) : Math.max(axisY, -32767);
                                tempRoll = (axisZ > 0) ? Math.min(axisZ, 32767) : Math.max(axisZ, -32767);
                            }
                        }

                        @Override
                        public void onAccuracyChanged(Sensor sensor, int accuracy) {
                        }
                    };
                    sensorManager.registerListener(accelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
                }
            }else{
                Toast.makeText(this, getString(R.string.using_rotation), Toast.LENGTH_SHORT).show();
                // Use rotation vector sensor as a fallback
                // Create a SensorEventListener to listen for rotation vector data
                rotationVectorListener = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                            float[] rotationMatrix = new float[9];
                            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
                            // Calculate orientation angles.
                            float[] orientationAngles = new float[3];
                            SensorManager.getOrientation(rotationMatrix, orientationAngles);

                            int pitch = Math.round(orientationAngles[1] * 1310* multiplier);   // Rotation around the X axis
                            int roll = Math.round(orientationAngles[2] * 873* multiplier);  // Rotation around the Y axis

                            temPitch = (pitch > 0) ? -Math.min(pitch, 32767) : -Math.max(pitch, -32767);
                            tempRoll = 32767 - Math.abs(roll);

                        }

                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    }
                };

                // Register the listener
                sensorManager.registerListener(rotationVectorListener, rotationVectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
            }
        }else{
            gravityListener = new SensorEventListener() {

                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
                        int axisY,axisZ;
                        axisY = Math.round(event.values[1]* 131* multiplier);
                        axisZ = Math.round(event.values[2]* 131* multiplier);
                        temPitch = (axisY > 0) ? Math.min(axisY, 32767) : Math.max(axisY, -32767);
                        tempRoll = (axisZ > 0) ? Math.min(axisZ, 32767) : Math.max(axisZ, -32767);
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            };
            sensorManager.registerListener(gravityListener, gravitySensor, SensorManager.SENSOR_DELAY_GAME);
        }
        handler = new Handler(Looper.getMainLooper());
        dataSender = new Runnable() {
            @Override
            public void run() {
                sendData();
                handler.postDelayed(this, 5); // 5 milliseconds delay}
            }
        };
        handler.post(dataSender);
    }
    private void sendData(){
        String sensorData = String.format(Locale.US, "%d,%d,%d,%d", tempRoll,temPitch,  isButtonAPressed , isButtonBPressed);
        if(!useBluetooth){
            socketClient.sendData(sensorData);
        }else if (bluetoothConn!=null){
            bluetoothConn.sendData(sensorData.getBytes());
        }
    }
    private void initButtons(){
        // Set up buttons and their touch listeners
        Button buttonA = findViewById(R.id.buttonA);
        Button buttonB = findViewById(R.id.buttonB);
        SwitchCompat switchA, switchB;
        switchA = findViewById(R.id.switchA);
        switchB = findViewById(R.id.switchB);

        switchA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonA.setEnabled(!isChecked); // Disable buttonA if switchA is checked
                isButtonAPressed = isChecked ? 1:0;
            }
        });

        switchB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonB.setEnabled(!isChecked); // Disable buttonB if switchB is checked
                isButtonBPressed = isChecked ? 1:0;
            }
        });

        if (useAnalogTrigger) {
            findViewById(R.id.buttonB).setOnTouchListener(new View.OnTouchListener() {
                private float startX; // To store initial touch coordinates
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startX = event.getX(); // Record initial Y coordinate
                            break;
                        case MotionEvent.ACTION_MOVE:
                            float currentX = event.getX();
                            float diffX = startX-currentX  ;
                            int tempVal = Math.max(Math.min(Math.round(diffX * triggerMultiplier), 255),0);
                            if(tempVal == 1){tempVal=2;}
                            isButtonBPressed = tempVal;
                            break;
                        case MotionEvent.ACTION_UP:
                            isButtonBPressed = 0;
                            break;
                    }
                    return true; // Consume the touch event
                }
            });
            findViewById(R.id.buttonA).setOnTouchListener(new View.OnTouchListener() {
                private float startX; // To store initial touch coordinates
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startX = event.getX(); // Record initial Y coordinate
                            break;
                        case MotionEvent.ACTION_MOVE:
                            float currentX = event.getX();
                            float diffX = startX-currentX;
                            int tempVal = Math.max(Math.min(Math.round(diffX * triggerMultiplier), 255),0);
                            if(tempVal == 1){tempVal=2;}
                            isButtonAPressed = tempVal;
                            break;
                        case MotionEvent.ACTION_UP:
                            isButtonAPressed = 0;
                            break;
                    }
                    return true; // Consume the touch event
                }
            });
            switchA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    buttonA.setEnabled(!isChecked); // Disable buttonA if switchA is checked
                    isButtonAPressed = isChecked ? 255:0;
                }
            });

            switchB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    buttonB.setEnabled(!isChecked); // Disable buttonB if switchB is checked
                    isButtonBPressed = isChecked ? 255:0;
                }
            });
        }
        else{

            buttonA.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    isButtonAPressed = 1;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    isButtonAPressed = 0;
                }
                return true; // To consume the event
            });

            buttonB.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    isButtonBPressed = 1;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    isButtonBPressed = 0;
                }
                return true; // To consume the event
            });
        }

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
            socketClient.sendData("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
            socketClient.close();
        }
        // Unregister the sensor listener
        if(sensorManager != null){
            sensorManager.unregisterListener(rotationVectorListener);
            sensorManager.unregisterListener(gravityListener);
            sensorManager.unregisterListener(accelerometerListener);
        }
        if (bluetoothConn!=null){
            //To return the state of controller to neutral on all buttons, for convenience.
            bluetoothConn.sendData("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0".getBytes());
        }
        if (handler != null && dataSender != null) {
            handler.removeCallbacks(dataSender);
        }
    }
}
