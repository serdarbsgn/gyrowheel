package com.serdarbsgn.gyrowheel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

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

// To move buttons wherever user wants
public class EditableActivity extends AppCompatActivity {
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
            float sizeModifier = (float) position.get(2) / 100;
            view.setScaleX(sizeModifier);
            view.setScaleY(sizeModifier);
            view.setLayoutParams(params);

            view.setOnTouchListener(new View.OnTouchListener() {
                private float dX, dY, initialDistance, initialScale;
                private boolean blockMove = false;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            dX = v.getX() - event.getRawX();
                            dY = v.getY() - event.getRawY();
                            break;

                        case MotionEvent.ACTION_POINTER_DOWN:
                            if (event.getPointerCount() == 2) {
                                initialDistance = calculateDistance(event);
                                initialScale = view.getScaleX();
                            }
                            break;

                        case MotionEvent.ACTION_MOVE:
                            if (event.getPointerCount() == 1 && !blockMove) {
                                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) v.getLayoutParams();
                                layoutParams.leftMargin = (int) (event.getRawX() + dX);
                                layoutParams.topMargin = (int) (event.getRawY() + dY);

                                layoutParams.leftMargin = Math.max(0, Math.min(layoutParams.leftMargin, ((View) v.getParent()).getWidth() - v.getWidth()));
                                layoutParams.topMargin = Math.max(0, Math.min(layoutParams.topMargin, ((View) v.getParent()).getHeight() - v.getHeight()));

                                v.setLayoutParams(layoutParams);

                                ArrayList<Integer> position = button.getValue();
                                position.set(0, layoutParams.leftMargin);
                                position.set(1, layoutParams.topMargin);
                                button.setValue(position);
                            } else if (event.getPointerCount() == 2) {
                                float newDistance = calculateDistance(event);
                                float scaleFactor = newDistance / initialDistance;
                                float newScale = initialScale * scaleFactor;

                                newScale = Math.max(0.5f, Math.min(newScale, 5.0f));

                                v.setScaleX(newScale);
                                v.setScaleY(newScale);
                                ArrayList<Integer> position = button.getValue();
                                position.set(2, Math.round(newScale * 100));
                                button.setValue(position);
                            }
                            break;

                        case MotionEvent.ACTION_POINTER_UP:
                            if (event.getPointerCount() == 2) {
                                initialDistance = 0;
                                blockMove = true;
                            }
                            break;

                        case MotionEvent.ACTION_UP:
                            dX = 0;
                            dY = 0;
                            initialDistance = 0;
                            blockMove = false;
                            break;

                        default:
                            return false;
                    }
                    return true;
                }
            });
        }
    }

    private float calculateDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private HashMap<Integer, ArrayList<Integer>> loadButtonPositions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        HashMap<Integer, ArrayList<Integer>> buttons = getIntegerIntegerArrayHashMap(displayMetrics.widthPixels,displayMetrics.heightPixels);
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
            Toast.makeText(this,"Drag&drop or pinch buttons to configure layout.",Toast.LENGTH_SHORT).show();
        }

        return buttons;
    }

    public static @NonNull HashMap<Integer, ArrayList<Integer>> getIntegerIntegerArrayHashMap(int width, int height) {
        HashMap<Integer, ArrayList<Integer>> buttons = new HashMap<>();

        buttons.put(R.id.buttonLB, new ArrayList<>(Arrays.asList(0, 0, 100)));
        buttons.put(R.id.buttonLT, new ArrayList<>(Arrays.asList(width / 6, 0, 100)));
        buttons.put(R.id.buttonL3, new ArrayList<>(Arrays.asList(width * 2 / 6, 0, 100)));

        buttons.put(R.id.buttonRB, new ArrayList<>(Arrays.asList(width * 3 / 6, 0, 100)));
        buttons.put(R.id.buttonRT, new ArrayList<>(Arrays.asList(width * 4 / 6, 0, 100)));
        buttons.put(R.id.buttonR3, new ArrayList<>(Arrays.asList(width * 5 / 6, 0, 100)));

        buttons.put(R.id.buttonBack, new ArrayList<>(Arrays.asList(0, height / 6, 100)));
        buttons.put(R.id.buttonStart, new ArrayList<>(Arrays.asList(width / 6, height / 6, 100)));

        buttons.put(R.id.buttonY, new ArrayList<>(Arrays.asList(width * 2 / 6, height / 6, 100)));
        buttons.put(R.id.buttonX, new ArrayList<>(Arrays.asList(width * 3 / 6, height / 6, 100)));
        buttons.put(R.id.buttonB, new ArrayList<>(Arrays.asList(width * 4 / 6, height / 6, 100)));
        buttons.put(R.id.buttonA, new ArrayList<>(Arrays.asList(width * 5 / 6, height / 6, 100)));

        buttons.put(R.id.buttonAU, new ArrayList<>(Arrays.asList(0, height * 2 / 6, 100)));
        buttons.put(R.id.buttonAL, new ArrayList<>(Arrays.asList(width / 6, height * 2 / 6, 100)));
        buttons.put(R.id.buttonAR, new ArrayList<>(Arrays.asList(width * 2 / 6, height * 2 / 6, 100)));
        buttons.put(R.id.buttonAD, new ArrayList<>(Arrays.asList(width * 3 / 6, height * 2 / 6, 100)));

        buttons.put(R.id.buttonAUL, new ArrayList<>(Arrays.asList(width * 4 / 6, height * 2 / 6, 100)));
        buttons.put(R.id.buttonAUR, new ArrayList<>(Arrays.asList(width * 5 / 6, height * 2 / 6, 100)));
        buttons.put(R.id.buttonADL, new ArrayList<>(Arrays.asList(width * 3 / 6, height * 3 / 6, 100)));
        buttons.put(R.id.buttonADR, new ArrayList<>(Arrays.asList(width * 2/ 6, height * 3 / 6, 100)));

        buttons.put(R.id.rightAnalog, new ArrayList<>(Arrays.asList(width * 4 / 6, height * 7 / 12, 125)));
        buttons.put(R.id.leftAnalog, new ArrayList<>(Arrays.asList(width  / 12, height * 7 / 12, 200)));
        buttons.put(R.id.switchSensor, new ArrayList<>(Arrays.asList(width * 2 / 6, height * 4 / 6, 150)));

        return buttons;
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
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
            builder.append(key).append(",").append(value.get(0)).append(",").append(value.get(1)).append(",").append(value.get(2)).append("\n");
        }
        try (FileOutputStream fos = openFileOutput("button_positions.txt", Context.MODE_PRIVATE)) {
            fos.write(builder.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
