package com.serdarbsgn.gyrowheel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.view.MotionEvent;
import android.widget.CompoundButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Objects;


public class GyroWheelActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor rotationVectorSensor;
    private SensorEventListener rotationVectorListener;
    private boolean isButtonAPressed = false;
    private boolean isButtonBPressed = false;
    private String ipAddress;
    private String socketAddress;
    private USBHelper usbHelper;
    private UDPOverInternet socketClient;
    private  TCPSocketOverADB tcpSocketOverADB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_gyrowheel);

        // Get the IP address passed from MainActivity
        ipAddress = getIntent().getStringExtra("IP_ADDRESS");
        socketAddress = getIntent().getStringExtra("SOCKET_IP");
        if(ipAddress==null && socketAddress==null){
            usbHelper = new USBHelper(getApplicationContext());
        }
        // Initialize the SensorManager
        if(socketAddress!=null) {
            socketClient = new UDPOverInternet(socketAddress, 12345);
        }
        if(Objects.equals(ipAddress, "127.0.0.1")){
            tcpSocketOverADB = new TCPSocketOverADB(ipAddress,12346);
        }
        // Get the rotation vector sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

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
                        temPitch= Math.min(Math.round(pitch * 32767), 32767);
                    }
                    else{
                        temPitch = Math.max(Math.round(pitch * 32767), -32767);
                    }
                    tempRoll = 32767 - Math.abs(Math.round(roll * 21844));

                    if(ipAddress!=null){
                        if(Objects.equals(ipAddress, "127.0.0.1")){
                            String sensorData = String.format(Locale.US, "%d,%d,%d,%d", tempRoll,temPitch,  isButtonAPressed ? 1 : 0, isButtonBPressed ? 1 : 0);
                            tcpSocketOverADB.sendData(sensorData);
                        }else{
                        JSONObject data = new JSONObject();
                        try {
                            data.put("SR",tempRoll);
                            data.put("SP", temPitch);
                            data.put("LT", isButtonAPressed ? 1 : 0);
                            data.put("RT", isButtonBPressed ? 1 : 0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        TCPOverADB.sendPostRequest(ipAddress, data);
                        }
                    }else {
                        String sensorData = String.format(Locale.US, "%d,%d,%d,%d", tempRoll,temPitch,  isButtonAPressed ? 1 : 0, isButtonBPressed ? 1 : 0);
                        if (socketAddress != null) {
                            socketClient.sendData(sensorData);
                        } else {
                            usbHelper.sendInputOverADB(sensorData);
                        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the sensor listener
        if(ipAddress==null && socketAddress == null){
            usbHelper.closeFile();
        }
        if(socketAddress!=null){
            socketClient.close();
        }
        if(Objects.equals(ipAddress, "127.0.0.1")){
            tcpSocketOverADB.close();
        }
        sensorManager.unregisterListener(rotationVectorListener);
    }
}
