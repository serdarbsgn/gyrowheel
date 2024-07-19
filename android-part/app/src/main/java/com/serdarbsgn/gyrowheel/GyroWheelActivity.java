package com.serdarbsgn.gyrowheel;

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
    private boolean isButtonAPressed = false;
    private boolean isButtonBPressed = false;
    private String socketAddress;
    private UDPOverInternet socketClient;
    int temPitch, tempRoll;

    private boolean useBluetooth;
    private BluetoothConn bluetoothConn;
    private Handler handler;
    private Runnable dataSender;

    private static final float ALPHA = 0.4f; // Smoothing factor(Lower is smoother but delayed.)
    private float filteredX = 0; // Smoothed x value
    private float filteredY = 0; // Smoothed y value
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_gyrowheel);
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
                    Toast.makeText(this, "This mode relies solely on Rotation sensors which your device doesn't have any suitable", Toast.LENGTH_SHORT).show();
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

                                int axisY = Math.round(filteredX * 3276);
                                int axisZ = Math.round(filteredY * 3276);
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
                Toast.makeText(this, "Gravity Sensor is not available. Using Rotation Vector Sensor as fallback.", Toast.LENGTH_SHORT).show();
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

                            int pitch = Math.round(orientationAngles[1] * 32767);   // Rotation around the X axis
                            int roll = Math.round(orientationAngles[2] * 21844);  // Rotation around the Y axis

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
                        axisY = Math.round(event.values[1]* 3276);
                        axisZ = Math.round(event.values[2]* 3276);
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
        String sensorData = String.format(Locale.US, "%d,%d,%d,%d", tempRoll,temPitch,  isButtonAPressed ? 1 : 0, isButtonBPressed ? 1 : 0);
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
                isButtonAPressed = isChecked;
            }
        });

        switchB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonB.setEnabled(!isChecked); // Disable buttonB if switchB is checked
                isButtonBPressed = isChecked;
            }
        });

        buttonA.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    isButtonAPressed = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    isButtonAPressed = false;
                }
                return true; // To consume the event
            }
        });

        buttonB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    isButtonBPressed = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    isButtonBPressed = false;
                }
                return true; // To consume the event
            }
        });
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
