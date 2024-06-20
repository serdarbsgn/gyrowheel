package com.serdarbsgn.gyrowheel;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Objects;


public class GamepadActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor rotationVectorSensor;
    private SensorEventListener rotationVectorListener;
    private boolean LB,LT,L3,RB,RT,R3,BC,ST,Y,X,B,A,AU,AL,AR,AD;
    private String ipAddress;
    private String socketAddress;
    private USBHelper usbHelper;
    private UDPOverInternet socketClient;
    private  TCPSocketOverADB tcpSocketOverADB;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_gamepad);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

                    // Create a JSON object to hold the sensor data and button states

                    int temPitch=0;
//                    int tempAzimuth=0;
                    int tempRoll=0;
                    if (pitch>0) {
                        temPitch= Math.min(Math.round(pitch * 32767), 32767);
                    }
                    else{
                        temPitch = Math.max(Math.round(pitch * 32767), -32767);
                    }
                    tempRoll = 32767 - Math.abs(Math.round(roll * 21844));
//                    long currentTimeMillis = System.currentTimeMillis();

                    if(ipAddress!=null){
                        if(Objects.equals(ipAddress, "127.0.0.1")){
                            String sensorData = String.format(Locale.US,
                                    "%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d",
                                    tempRoll,
                                    temPitch,
                                    LB ? 1 : 0,
                                    LT ? 1 : 0,
                                    L3 ? 1 : 0,
                                    RB ? 1 : 0,
                                    RT ? 1 : 0,
                                    R3 ? 1 : 0,
                                    BC ? 1 : 0,
                                    ST ? 1 : 0,
                                    Y ? 1 : 0,
                                    X ? 1 : 0,
                                    B ? 1 : 0,
                                    A ? 1 : 0,
                                    AU ? 1 : 0,
                                    AL ? 1 : 0,
                                    AR ? 1 : 0,
                                    AD ? 1 : 0
                            );
                            tcpSocketOverADB.sendData(sensorData);
                        }else{
                            JSONObject data = new JSONObject();
                            try {
                                data.put("SR", tempRoll);
                                data.put("SP", temPitch);
                                data.put("LB", LB ? 1 : 0);
                                data.put("LT", LT ? 1 : 0);
                                data.put("L3", L3 ? 1 : 0);
                                data.put("RB", RB ? 1 : 0);
                                data.put("RT", RT ? 1 : 0);
                                data.put("R3", R3 ? 1 : 0);
                                data.put("BC", BC ? 1 : 0);
                                data.put("ST", ST ? 1 : 0);
                                data.put("Y", Y ? 1 : 0);
                                data.put("X", X ? 1 : 0);
                                data.put("B", B ? 1 : 0);
                                data.put("A", A ? 1 : 0);
                                data.put("AU",AU ? 1 : 0);
                                data.put("AL",AL ? 1 : 0);
                                data.put("AR",AR ? 1 : 0);
                                data.put("AD",AD ? 1 : 0);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            TCPOverADB.sendPostRequest(ipAddress, data);
                        }
                    }else {
                        String sensorData = String.format(Locale.US,
                                "%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d",
                                tempRoll,
                                temPitch,
                                LB ? 1 : 0,
                                LT ? 1 : 0,
                                L3 ? 1 : 0,
                                RB ? 1 : 0,
                                RT ? 1 : 0,
                                R3 ? 1 : 0,
                                BC ? 1 : 0,
                                ST ? 1 : 0,
                                Y ? 1 : 0,
                                X ? 1 : 0,
                                B ? 1 : 0,
                                A ? 1 : 0,
                                AU ? 1 : 0,
                                AL ? 1 : 0,
                                AR ? 1 : 0,
                                AD ? 1 : 0
                        );
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
        // LB,LT,L3,RB,RT,R3,BC,ST,Y,X,B,A;
        // Set up buttons and their touch listeners
        Button buttonLB = findViewById(R.id.buttonLB);
        Button buttonLT = findViewById(R.id.buttonLT);
        Button buttonL3 = findViewById(R.id.buttonL3);

        Button buttonRB = findViewById(R.id.buttonRB);
        Button buttonRT = findViewById(R.id.buttonRT);
        Button buttonR3 = findViewById(R.id.buttonR3);

        Button buttonBC = findViewById(R.id.buttonBack);
        Button buttonST = findViewById(R.id.buttonStart);

        Button buttonY = findViewById(R.id.buttonY);
        Button buttonX = findViewById(R.id.buttonX);
        Button buttonB = findViewById(R.id.buttonB);
        Button buttonA = findViewById(R.id.buttonA);

        Button buttonAU = findViewById(R.id.buttonAU);
        Button buttonAL = findViewById(R.id.buttonAL);
        Button buttonAR = findViewById(R.id.buttonAR);
        Button buttonAD = findViewById(R.id.buttonAD);

        Button buttonAUL = findViewById(R.id.buttonAUL);
        Button buttonAUR = findViewById(R.id.buttonAUR);
        Button buttonADL = findViewById(R.id.buttonADL);
        Button buttonADR = findViewById(R.id.buttonADR);

        buttonLB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    LB = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    LB = false;
                }
                return true; // To consume the event
            }
        });

        buttonLT.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    LT = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    LT = false;
                }
                return true; // To consume the event
            }
        });

        buttonL3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    L3 = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    L3 = false;
                }
                return true; // To consume the event
            }
        });

        buttonRB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    RB = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    RB = false;
                }
                return true; // To consume the event
            }
        });
        buttonRT.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    RT = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    RT = false;
                }
                return true; // To consume the event
            }
        });
        buttonR3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    R3 = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    R3 = false;
                }
                return true; // To consume the event
            }
        });



        buttonBC.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    BC = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    BC = false;
                }
                return true; // To consume the event
            }
        });
        buttonST.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ST = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    ST = false;
                }
                return true; // To consume the event
            }
        });


        buttonY.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Y = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Y = false;
                }
                return true; // To consume the event
            }
        });

        buttonX.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    X = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    X = false;
                }
                return true; // To consume the event
            }
        });
        buttonB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    B = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    B = false;
                }
                return true; // To consume the event
            }
        });

        buttonA.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    A = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    A = false;
                }
                return true; // To consume the event
            }
        });

        buttonAU.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    AU = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    AU = false;
                }
                return true; // To consume the event
            }
        });

        buttonAL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    AL = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    AL = false;
                }
                return true; // To consume the event
            }
        });

        buttonAR.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    AR = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    AR = false;
                }
                return true; // To consume the event
            }
        });

        buttonAD.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    AD = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    AD = false;
                }
                return true; // To consume the event
            }
        });

        buttonAUL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    AU = true;
                    AL = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    AU = false;
                    AL = false;
                }
                return true; // To consume the event
            }
        });

        buttonAUR.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    AU = true;
                    AR = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    AU = false;
                    AR = false;
                }
                return true; // To consume the event
            }
        });

        buttonADL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    AD = true;
                    AL = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    AD = false;
                    AL = false;
                }
                return true; // To consume the event
            }
        });

        buttonADR.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    AD = true;
                    AR = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    AD = false;
                    AR = false;
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
