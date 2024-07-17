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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.Locale;


public class GyroWheelActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private SensorEventListener rotationVectorListener;
    private boolean isButtonAPressed = false;
    private boolean isButtonBPressed = false;
    private String socketAddress;
    private UDPOverInternet socketClient;

    private boolean useBluetooth;
    private BluetoothConn bluetoothConn;
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

        // Check if the rotation vector sensor is available
        if (rotationVectorSensor == null) {
            Toast.makeText(this, "This mode relies solely on Rotation sensors which your device doesn't have.", Toast.LENGTH_SHORT).show();
            Log.e("RotationSensor", "Rotation Vector Sensor not available");
            finish();
            return;
        }

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

                    // Create a string or JSON for different use cases.

                    int temPitch=0;
//                    int tempAzimuth=0; this is not being utilised yet.
                    int tempRoll=0;
                    if (pitch>0) {
                        temPitch= -Math.min(Math.round(pitch * 32767), 32767);
                    }
                    else{
                        temPitch = -Math.max(Math.round(pitch * 32767), -32767);
                    }
                    tempRoll = 32767 - Math.abs(Math.round(roll * 21844));

                    String sensorData = String.format(Locale.US, "%d,%d,%d,%d", tempRoll,temPitch,  isButtonAPressed ? 1 : 0, isButtonBPressed ? 1 : 0);
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

    protected void onDestroy() {
        super.onDestroy();
        // Unregister the sensor listener
        if(socketAddress!=null){
            socketClient.sendData("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
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
