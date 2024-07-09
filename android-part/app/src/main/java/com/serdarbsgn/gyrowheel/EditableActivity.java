package com.serdarbsgn.gyrowheel;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
//To move buttons wherever user wants
public class EditableActivity extends AppCompatActivity{

    HashMap<Integer, ArrayList<Integer>> buttons;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editable);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        buttons = loadButtonPositions();
        initButtons();
        findViewById(R.id.leftAnalog).setBackgroundColor(Color.WHITE);
        findViewById(R.id.rightAnalog).setBackgroundColor(Color.WHITE);
    }
    private void initButtons() {
        for (Map.Entry<Integer, ArrayList<Integer>> button : buttons.entrySet()) {
            View view = findViewById(button.getKey());
            ArrayList<Integer> position = button.getValue();
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
            params.leftMargin = position.get(0);
            params.topMargin = position.get(1);
            view.setLayoutParams(params);
            view.setOnTouchListener(new View.OnTouchListener() {
                private float dX, dY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            dX = v.getX() - event.getRawX();
                            dY = v.getY() - event.getRawY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) v.getLayoutParams();
                            layoutParams.leftMargin = (int) (event.getRawX() + dX);
                            layoutParams.topMargin = (int) (event.getRawY() + dY);
                            v.setLayoutParams(layoutParams);

                            // Update the button's position in the hashmap
                            ArrayList<Integer> position = button.getValue();
                            position.set(0, layoutParams.leftMargin);
                            position.set(1, layoutParams.topMargin);
                            button.setValue(position);
                            break;
                        default:
                            return false;
                    }
                    return true;
                }
            });
        }
    }
    private HashMap<Integer, ArrayList<Integer>> loadButtonPositions() {
        HashMap<Integer, ArrayList<Integer>> buttons = getIntegerIntegerArrayHashMap();

        try (FileInputStream fis = openFileInput("button_positions.txt")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    Integer key = Integer.parseInt(parts[0]);
                    Integer x = Integer.parseInt(parts[1]);
                    Integer y = Integer.parseInt(parts[2]);
                    buttons.put(key, new ArrayList<>(Arrays.asList(x, y)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return buttons;
    }
    private @NonNull HashMap<Integer, ArrayList<Integer>> getIntegerIntegerArrayHashMap() {
        HashMap<Integer,ArrayList<Integer>> buttons = new HashMap<>();

        buttons.put(R.id.buttonLB, new ArrayList<>(Arrays.asList(0, 0)));
        buttons.put(R.id.buttonLT, new ArrayList<>(Arrays.asList(0, 0)));
        buttons.put(R.id.buttonL3, new ArrayList<>(Arrays.asList(0, 0)));

        buttons.put(R.id.buttonRB, new ArrayList<>(Arrays.asList(0, 0)));
        buttons.put(R.id.buttonRT, new ArrayList<>(Arrays.asList(0, 0)));
        buttons.put(R.id.buttonR3, new ArrayList<>(Arrays.asList(0, 0)));

        buttons.put(R.id.buttonBack, new ArrayList<>(Arrays.asList(0, 0)));
        buttons.put(R.id.buttonStart, new ArrayList<>(Arrays.asList(0, 0)));

        buttons.put(R.id.buttonY, new ArrayList<>(Arrays.asList(0, 0)));
        buttons.put(R.id.buttonX, new ArrayList<>(Arrays.asList(0, 0)));
        buttons.put(R.id.buttonB, new ArrayList<>(Arrays.asList(0, 0)));
        buttons.put(R.id.buttonA, new ArrayList<>(Arrays.asList(0, 0)));

        buttons.put(R.id.buttonAU, new ArrayList<>(Arrays.asList(0, 0)));
        buttons.put(R.id.buttonAL, new ArrayList<>(Arrays.asList(0, 0)));
        buttons.put(R.id.buttonAR, new ArrayList<>(Arrays.asList(0, 0)));
        buttons.put(R.id.buttonAD, new ArrayList<>(Arrays.asList(0, 0)));

        buttons.put(R.id.buttonAUL, new ArrayList<>(Arrays.asList(0, 0)));
        buttons.put(R.id.buttonAUR, new ArrayList<>(Arrays.asList(0, 0)));
        buttons.put(R.id.buttonADL, new ArrayList<>(Arrays.asList(0, 0)));
        buttons.put(R.id.buttonADR, new ArrayList<>(Arrays.asList(0, 0)));

        buttons.put(R.id.rightAnalog, new ArrayList<>(Arrays.asList(0, 0)));
        buttons.put(R.id.leftAnalog, new ArrayList<>(Arrays.asList(0, 0)));
        buttons.put(R.id.switchSensor, new ArrayList<>(Arrays.asList(0, 0)));

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
        saveButtonPositions();
    }

    private void saveButtonPositions() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Integer, ArrayList<Integer>> entry : buttons.entrySet()) {
            Integer key = entry.getKey();
            ArrayList<Integer> value = entry.getValue();
            builder.append(key).append(",").append(value.get(0)).append(",").append(value.get(1)).append("\n");
        }
        try (FileOutputStream fos = openFileOutput("button_positions.txt", Context.MODE_PRIVATE)) {
            fos.write(builder.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

