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

    @SuppressLint("ClickableViewAccessibility")
    protected void setListeners(){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        touchpadMultiplier = sharedPreferences.getInt(KEY_TOUCHPAD_MULTIPLIER,20)/20f;

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
                    keystroke = "";
                } else if (before.length() < after.length()) {
                    String temp = after.substring(before.length());
                    if(temp.equals("\n")){
                        command = "enter";
                        keystroke = "";
                    }else if (temp.equals(" ")) {
                        command = "space";
                        keystroke = "";
                    }else{
                        keystroke = temp;
                        command = "";
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
            @Override
            public boolean onTouch(View v, MotionEvent event) {
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
                return true;
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
                (leftClick ? 1 : 0) + (rightClick ? 2 : 0));
        if(!command.isEmpty()){
            command = "";
        }
        if(!keystroke.isEmpty()){
            keystroke = "";
        }
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
