package com.serdarbsgn.gyrowheel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class KeyboardAndMouseActivity extends AppCompatActivity {
    public int mouseX,mouseY;
    public String before = "";
    public String after = "";
    public String command = "";
    public String keystroke = "";
    public int[] mouseCoordinates = new int[2];
    public boolean rightClick = false;
    public boolean leftClick = false;
    public boolean changed = false;
    public boolean ctrlPressed = false;
    public boolean altPressed = false;
    public boolean windowsPressed = false;
    public boolean shiftPressed = false;
    public int twoFingerGesture = 0;
    private Handler handler;
    private Runnable dataSender;

    private String socketAddress;
    private boolean useBluetooth;
    private UDPOverInternet socketClient;
    private BluetoothConn bluetoothConn;
    int colorPrimary = 0;
    int alpha = 0, red = 0, green = 0, blue = 0;
    private float touchpadMultiplier = 1f;
    private static final String PREFS_NAME = "com.serdarbsgn.gyrowheel.PREFS";
    private static final String KEY_TOUCHPAD_MULTIPLIER = "TOUCHPAD_MULTIPLIER";
    private static final String KEY_OVERRIDE_VOLUME_BUTTONS = "OVERRIDE_VOLUME_BUTTONS";
    private static final String KEY_VOLUME_BUTTONS_MODE = "VOLUME_BUTTONS_MODE";
    private boolean overrideVolumeKeys = false;
    private int volumeKeyMode = 0;
    private float density = 1f;
    float leftCenterX,leftCenterY;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard_mouse);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        colorPrimary = ContextCompat.getColor(this, R.color.button_color);
        alpha = Color.alpha(colorPrimary);
        red = Color.red(colorPrimary);
        green = Color.green(colorPrimary);
        blue = Color.blue(colorPrimary);
        density = this.getResources().getDisplayMetrics().density;
        socketAddress = getIntent().getStringExtra("SOCKET_IP");
        useBluetooth = getIntent().getBooleanExtra("USE_BLUETOOTH",false);
        if (!useBluetooth) {
            socketClient = new UDPOverInternet(socketAddress, 12345);
        }else{
            initBluetooth();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setListeners();
        setHandlerSender();
    }

    private void initBluetooth(){
        bluetoothConn = BluetoothConn.getInstance();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(!overrideVolumeKeys){
            return super.onKeyDown(keyCode,event);
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                switch (volumeKeyMode){
                    case 0:
                        command = "m_vol_up";
                        break;
                    case 1:
                        command = "m_next";
                        break;
                    case 2:
                        leftClick = true;
                        break;
                    case 3:
                        twoFingerGesture = 16;
                        break;
                }
                changed = true;
                return true;

            case KeyEvent.KEYCODE_VOLUME_DOWN:
                switch (volumeKeyMode){
                    case 0:
                        command = "m_vol_down";
                        break;
                    case 1:
                        command = "m_previous";
                        break;
                    case 2:
                        rightClick = true;
                        break;
                    case 3:
                        twoFingerGesture = 32;
                        break;
                }
                changed = true;
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(!overrideVolumeKeys){
            return super.onKeyUp(keyCode,event);
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if(volumeKeyMode==2){
                    leftClick = false;
                }
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if(volumeKeyMode==2){
                    rightClick = false;
                }
                changed = true;
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    protected void setListeners(){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        touchpadMultiplier = sharedPreferences.getInt(KEY_TOUCHPAD_MULTIPLIER,20)/20f;
        overrideVolumeKeys = sharedPreferences.getBoolean(KEY_OVERRIDE_VOLUME_BUTTONS,false);
        volumeKeyMode = sharedPreferences.getInt(KEY_VOLUME_BUTTONS_MODE,0);
        final EditText keyboardInput = findViewById(R.id.keyboardView);
        keyboardInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                before = charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                after = editable.toString();
                if (before.length() > after.length()){
                    command = "backspace";
                } else if (before.length() < after.length()) {
                    String temp = after.substring(before.length());
                    if(temp.equals("\n")){
                        command = "enter";
                    }else if (temp.equals(" ")) {
                        command = "space";
                    }else{
                        keystroke = temp;
                    }
                }
                changed=true;
            }
        });
        keyboardInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DEL) { // Handle Backspace key
                        command = "backspace";
                        changed=true;
                        return true;
                    }
                }
                return false;
            }
        });

        View keyboardButton = findViewById(R.id.keyboardOpen);
        keyboardButton.setOnTouchListener(new View.OnTouchListener() {
            final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (imm != null) {
                        if (keyboardInput.isFocused()) {
                            keyboardInput.clearFocus();
                            imm.hideSoftInputFromWindow(keyboardInput.getWindowToken(), 0);
                        } else {
                            keyboardInput.requestFocus();
                            imm.showSoftInput(keyboardInput, InputMethodManager.SHOW_IMPLICIT);
                        }
                    }
                }
                return true;
            }
        });

        Button leftClickButton = findViewById(R.id.leftClick);
        Button rightClickButton = findViewById(R.id.rightClick);
        leftClickButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        leftClick = true;
                        changed = true;
                        v.setBackgroundColor(Color.argb(alpha, 255, 255 - red + green, blue));
                        break;
                    case MotionEvent.ACTION_UP:
                        leftClick = false;
                        changed = true;
                        v.setBackgroundColor(Color.argb(alpha, red, green, blue));
                        break;
                }
                return true; // To consume the event
            }
        });
        rightClickButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        rightClick = true;
                        changed = true;
                        v.setBackgroundColor(Color.argb(alpha, 255, 255 - red + green, blue));
                        break;
                    case MotionEvent.ACTION_UP:
                        rightClick = false;
                        changed = true;
                        v.setBackgroundColor(Color.argb(alpha, red, green, blue));
                        break;
                }
                return true; // To consume the event
            }
        });
        View touchPad = findViewById(R.id.touchPadView);
        touchPad.setOnTouchListener(new View.OnTouchListener() {
            private float startX, startY;
            private float pinchX,pinchY;
            private float ptr0X,ptr0Y,ptr1X,ptr1Y;
            private int mode = 0;
            private long startTime = 0; // for preventing possibly unwanted middle clicks.
            private boolean twoToOne = false; // transition between one and two finger movements without jittery mouse movement.
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int pointerCount = event.getPointerCount();
                if (pointerCount == 1) {
                    if(twoToOne){
                        startX = event.getX();
                        startY = event.getY();
                        twoToOne = false;
                    }
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startX = event.getX();
                            startY = event.getY();
                            break;

                        case MotionEvent.ACTION_MOVE:
                            float currentX = event.getX();
                            float currentY = event.getY();
                            mouseX = (int) (currentX - startX);
                            mouseY = (int) (currentY - startY);
                            mouseCoordinates[0] = (int) (mouseX*touchpadMultiplier);
                            mouseCoordinates[1] = (int) (mouseY*touchpadMultiplier);
                            changed=true;
                            startX = currentX;
                            startY = currentY;
                            break;

                        case MotionEvent.ACTION_UP:
                            mouseCoordinates[0] = 0;
                            mouseCoordinates[1] = 0;
                            mouseX = 0;
                            mouseY = 0;
                            break;
                    }
                }else if (pointerCount == 2) { // Two-finger touch
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_POINTER_DOWN:
                            // Record the initial positions of both fingers
                            ptr0X = event.getX(0);
                            ptr0Y = event.getY(0);
                            ptr1X = event.getX(1);
                            ptr1Y = event.getY(1);

                            // Calculate the initial pinch distance
                            pinchX = Math.abs(ptr1X - ptr0X);
                            pinchY = Math.abs(ptr1Y - ptr0Y);
                            twoFingerGesture = 0;
                            startTime = System.currentTimeMillis();
                            break;

                        case MotionEvent.ACTION_MOVE:
                            float newPtr0X = event.getX(0);
                            float newPtr0Y = event.getY(0);
                            float newPtr1X = event.getX(1);
                            float newPtr1Y = event.getY(1);

                            float newPinchX = Math.abs(newPtr1X - newPtr0X);
                            float newPinchY = Math.abs(newPtr1Y - newPtr0Y);

                            float ptr0Ydiff = newPtr0Y - ptr0Y;
                            float ptr1Ydiff = newPtr1Y - ptr1Y;
                            float pinchXdiff = newPinchX - pinchX;
                            float pinchYdiff = newPinchY - pinchY;
                            //0 for no gesture, 4 for zoom in, 8 for zoom out, 16 for scroll up, 32 for down, 64 for middle click
                            switch (mode) {
                                case 0:
                                    if (Math.abs(pinchXdiff) > Math.abs(pinchYdiff)) {
                                        if (pinchXdiff > 10) {
                                            mode = 1;
                                            twoFingerGesture = 4;
                                        } else if (pinchXdiff < -10) {
                                            mode = 1;
                                            twoFingerGesture = 8;
                                        }
                                    } else {
                                        if (ptr0Ydiff > 4 && ptr1Ydiff > 4) {
                                            mode = 2;
                                            twoFingerGesture = 16;
                                        } else if (ptr0Ydiff < -3 && ptr1Ydiff < -3) {
                                            mode = 2;
                                            twoFingerGesture = 32;
                                        }
                                    }
                                    break;

                                case 1:
                                    if (pinchXdiff > 50/touchpadMultiplier) {
                                        twoFingerGesture = 4;
                                    } else if (pinchXdiff < -50/touchpadMultiplier) {
                                        twoFingerGesture = 8;
                                    }
                                    break;

                                case 2:
                                    if (ptr0Ydiff > 3 && ptr1Ydiff > 3) {
                                        twoFingerGesture = 16;
                                    } else if (ptr0Ydiff < -3 && ptr1Ydiff < -3) {
                                        twoFingerGesture = 32;
                                    }
                                    break;
                            }
                            if(twoFingerGesture>0){
                            ptr0X = newPtr0X;
                            ptr0Y = newPtr0Y;
                            ptr1X = newPtr1X;
                            ptr1Y = newPtr1Y;
                            pinchX = newPinchX;
                            pinchY = newPinchY;
                            changed = true;
                            }
                            break;

                        case MotionEvent.ACTION_POINTER_UP:
                            if(twoFingerGesture == 0 && mode == 0){
                                long elapsedTime = System.currentTimeMillis() - startTime;
                                if(elapsedTime < 1000){
                                    twoFingerGesture = 64;
                                }
                                else{
                                    twoFingerGesture = 0;
                                }
                            }else{
                                twoFingerGesture = 0;
                            }
                            changed = true;
                            mode = 0;
                            twoToOne = true;
                            mouseCoordinates[0] = 0;
                            mouseCoordinates[1] = 0;
                            break;
                    }
                }
                return true;
            }
        });
        View functionView = findViewById(R.id.function_keys);
        View functionKnob = findViewById(R.id.f_knob);
        functionKnob.setVisibility(View.INVISIBLE);
        functionKnob.setScaleX(0.6f);
        functionKnob.setScaleY(0.6f);
        functionView.setVisibility(View.INVISIBLE);
        View functionButton = findViewById(R.id.f_keys);
        functionButton.setBackgroundColor(Color.argb(alpha, red, green, blue));
        functionButton.setOnTouchListener(new View.OnTouchListener() {
            private boolean open = false;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    open = !open;
                    if(open){
                        functionButton.setBackgroundColor(Color.argb(alpha, 255, 255 - red + green, blue));
                        touchPad.setVisibility(View.INVISIBLE);
                        functionView.setVisibility(View.VISIBLE);
                        functionKnob.setVisibility(View.VISIBLE);
                    }else{
                        functionButton.setBackgroundColor(Color.argb(alpha, red, green, blue));
                        touchPad.setVisibility(View.VISIBLE);
                        functionView.setVisibility(View.INVISIBLE);
                        functionKnob.setVisibility(View.INVISIBLE);
                    }
                }
                return true;
            }
        });
        functionView.setOnTouchListener(new View.OnTouchListener() {
            private float startX, startY,innerX,innerY;// To store initial touch coordinates
            private final float boundingBoxRadius = density*140;
            private int distance;
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
                        float dx = (currentX - startX);
                        float dy = (currentY - startY);
                        distance = (int) Math.sqrt(dx * dx + dy * dy);
                        if (distance > boundingBoxRadius) {
                            float scale = boundingBoxRadius / distance;
                            innerX = dx * scale;
                            innerY = dy * scale;
                        } else {
                            innerX = dx;
                            innerY = dy;
                        }
                        functionKnob.setX(innerX);
                        functionKnob.setY(innerY);
                        break;
                    case MotionEvent.ACTION_UP:
                        if(distance>density*45){
                            float angleRadians = (float) Math.atan2((int) innerY, (int) innerX);
                            float angleDegrees = ((float) Math.toDegrees(angleRadians))%360;
                            int fkey = ((Math.round(angleDegrees/30)%12)+15)%12;
                            if(fkey == 0){fkey=12;}
                            command = "f_"+fkey;
                            changed = true;
                        }
                        functionKnob.setX(leftCenterX);
                        functionKnob.setY(leftCenterY);
                        break;
                }
                return true; // Consume the touch event
            }
        });
        setMediaButtons();
    }
    //lets reduce the boilerplate, for my sanity.
    protected void setMediaButtons(){
        HashMap<Integer, String> viewCommandMap = getIntegerStringHashMap();


        // Set up touch listeners using the map
        for (Map.Entry<Integer, String> entry : viewCommandMap.entrySet()) {
            final View view = findViewById(entry.getKey());
            final String commandValue = entry.getValue();

            view.setBackgroundColor(Color.argb(alpha, red, green, blue));
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            command = commandValue;
                            changed = true;
                            v.setBackgroundColor(Color.argb(alpha, 255, 255 - red + green, blue));
                            break;
                        case MotionEvent.ACTION_UP:
                            v.setBackgroundColor(Color.argb(alpha, red, green, blue));
                            break;
                    }
                    return true;
                }
            });
        }
        final View ctrl = findViewById(R.id.ctrl);
        ctrl.setBackgroundColor(Color.argb(alpha, red, green, blue));
        ctrl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ctrlPressed = true;
                        changed = true;
                        v.setBackgroundColor(Color.argb(alpha, 255, 255 - red + green, blue));
                        break;
                    case MotionEvent.ACTION_UP:
                        ctrlPressed = false;
                        changed = true;
                        v.setBackgroundColor(Color.argb(alpha, red, green, blue));
                        break;
                }
                return true;
            }
        });
        final View alt = findViewById(R.id.alt);
        alt.setBackgroundColor(Color.argb(alpha, red, green, blue));
        alt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        altPressed = true;
                        changed = true;
                        v.setBackgroundColor(Color.argb(alpha, 255, 255 - red + green, blue));
                        break;
                    case MotionEvent.ACTION_UP:
                        altPressed = false;
                        changed = true;
                        v.setBackgroundColor(Color.argb(alpha, red, green, blue));
                        break;
                }
                return true;
            }
        });
        final View shift = findViewById(R.id.shift);
        shift.setBackgroundColor(Color.argb(alpha, red, green, blue));
        shift.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        shiftPressed = true;
                        changed = true;
                        v.setBackgroundColor(Color.argb(alpha, 255, 255 - red + green, blue));
                        break;
                    case MotionEvent.ACTION_UP:
                        shiftPressed = false;
                        changed = true;
                        v.setBackgroundColor(Color.argb(alpha, red, green, blue));
                        break;
                }
                return true;
            }
        });
        final View windows = findViewById(R.id.windows);
        windows.setBackgroundColor(Color.argb(alpha, red, green, blue));
        windows.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        windowsPressed = true;
                        changed = true;
                        v.setBackgroundColor(Color.argb(alpha, 255, 255 - red + green, blue));
                        break;
                    case MotionEvent.ACTION_UP:
                        windowsPressed = false;
                        changed = true;
                        v.setBackgroundColor(Color.argb(alpha, red, green, blue));
                        break;
                }
                return true;
            }
        });
    }

    private static @NonNull HashMap<Integer, String> getIntegerStringHashMap() {
        HashMap<Integer, String> viewCommandMap = new HashMap<>();
        viewCommandMap.put(R.id.mediaNext, "m_next");
        viewCommandMap.put(R.id.mediaPrevious, "m_previous");
        viewCommandMap.put(R.id.mediaPlay, "m_play");
        viewCommandMap.put(R.id.mediaVolUp, "m_vol_up");
        viewCommandMap.put(R.id.mediaVolDown, "m_vol_down");
        viewCommandMap.put(R.id.mediaVolMute, "m_vol_mute");
        viewCommandMap.put(R.id.caps_lock, "caps");
        viewCommandMap.put(R.id.tab, "tab");
        viewCommandMap.put(R.id.escape, "esc");
        viewCommandMap.put(R.id.del, "del");
        viewCommandMap.put(R.id.up_arrow, "ar_up");
        viewCommandMap.put(R.id.down_arrow, "ar_down");
        viewCommandMap.put(R.id.left_arrow, "ar_left");
        viewCommandMap.put(R.id.right_arrow, "ar_right");
        return viewCommandMap;
    }

    protected void setHandlerSender(){
        handler = new Handler(Looper.getMainLooper());
        final int[] idleCount = {0};
        dataSender = new Runnable() {
            @Override
            public void run() {
                if(changed){
                    sendData();
                    changed = false;
                    idleCount[0]=0;
                    handler.postDelayed(this, 0);
                }else if(idleCount[0] < 150){
                    handler.postDelayed(this, idleCount[0]++);
                }else{
                    handler.postDelayed(this, 2L *idleCount[0]);
                }//conserve some cpu usage by reducing the poll rate.
            }
        };
        handler.post(dataSender);
    }
    private void sendData(){
        if(windowsPressed){
            command = "w_"+command;
        }
        if(shiftPressed){
            command = "s_"+command;
        }
        if(altPressed){
            command = "a_"+command;
        }
        if(ctrlPressed){
            command = "c_"+command;
        }
        String sensorData = String.format(Locale.US,
                "%s|%s|%d|%d|%d",
                command,
                keystroke,
                mouseCoordinates[0],
                mouseCoordinates[1],
                (leftClick ? 1 : 0) + (rightClick ? 2 : 0) + twoFingerGesture);
        command = "";
        twoFingerGesture = 0;
        keystroke = "";
        if (!useBluetooth) {
            socketClient.sendData(sensorData);
        } else if (bluetoothConn != null) {
            bluetoothConn.sendData(sensorData.getBytes());
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
            socketClient.sendData("||0|0|0");
            socketClient.close();
        }
        if (bluetoothConn!=null){
            //To return the state of controller to neutral on all buttons, for convenience.
            bluetoothConn.sendData("||0|0|0".getBytes());
        }
        if (handler != null && dataSender != null) {
            handler.removeCallbacks(dataSender);
        }
    }
}
