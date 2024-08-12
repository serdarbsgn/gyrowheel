package com.serdarbsgn.gyrowheel;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "com.serdarbsgn.gyrowheel.PREFS";
    private static final String KEY_SENSOR_MULTIPLIER = "SENSOR_MULTIPLIER";
    private static final String KEY_TOUCH_MULTIPLIER = "TOUCH_MULTIPLIER";
    private static final String KEY_SMOOTH_MULTIPLIER = "SMOOTH_MULTIPLIER";
    private static final String KEY_TRIGGER_MULTIPLIER = "TRIGGER_MULTIPLIER";
    private static final String KEY_USE_ANALOG_TRIGGER = "USE_ANALOG_TRIGGER";
    private static final String KEY_TOUCHPAD_MULTIPLIER = "TOUCHPAD_MULTIPLIER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        SeekBar seekBarSensor = findViewById(R.id.seekBarSensorMultiplier);
        seekBarSensor.setMin(1);
        seekBarSensor.setMax(50);
        seekBarSensor.setProgress(25);
        int savedSensorMultiplier = sharedPreferences.getInt(KEY_SENSOR_MULTIPLIER, seekBarSensor.getProgress());
        seekBarSensor.setProgress(savedSensorMultiplier);

        TextView textViewSensor = findViewById(R.id.seekBarSensorMultiplierText);
        textViewSensor.setText(String.valueOf(seekBarSensor.getProgress()));

        seekBarSensor.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewSensor.setText(String.valueOf(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                saveMultiplier(KEY_SENSOR_MULTIPLIER,seekBarSensor.getProgress());
            }
        });


        SeekBar seekBarTouch = findViewById(R.id.seekBarTouchMultiplier);
        seekBarTouch.setMin(1);
        seekBarTouch.setMax(50);
        seekBarTouch.setProgress(25);
        int savedTouchMultiplier = sharedPreferences.getInt(KEY_TOUCH_MULTIPLIER, seekBarTouch.getProgress());
        seekBarTouch.setProgress(savedTouchMultiplier);

        TextView textViewTouch = findViewById(R.id.seekBarTouchMultiplierText);
        textViewTouch.setText(String.valueOf(seekBarTouch.getProgress()));

        seekBarTouch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewTouch.setText(String.valueOf(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                saveMultiplier(KEY_TOUCH_MULTIPLIER,seekBarTouch.getProgress());
            }
        });

        SeekBar seekBarSmooth = findViewById(R.id.seekBarSmoothMultiplier);
        seekBarSmooth.setMin(1);
        seekBarSmooth.setMax(10);
        seekBarSmooth.setProgress(4);
        int savedSmoothMultiplier = sharedPreferences.getInt(KEY_SMOOTH_MULTIPLIER, seekBarSmooth.getProgress());
        seekBarSmooth.setProgress(savedSmoothMultiplier);

        TextView textViewSmooth = findViewById(R.id.seekBarSmoothMultiplierText);
        textViewSmooth.setText(String.valueOf(seekBarSmooth.getProgress()));

        seekBarSmooth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewSmooth.setText(String.valueOf(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                saveMultiplier(KEY_SMOOTH_MULTIPLIER,seekBarSmooth.getProgress());
            }
        });

        SeekBar seekBarTrigger = findViewById(R.id.seekBarTriggerMultiplier);
        seekBarTrigger.setMin(1);
        seekBarTrigger.setMax(100);
        seekBarTrigger.setProgress(20);
        int savedTriggerMultiplier = sharedPreferences.getInt(KEY_TRIGGER_MULTIPLIER, seekBarTrigger.getProgress());
        seekBarTrigger.setProgress(savedTriggerMultiplier);

        TextView textViewTrigger = findViewById(R.id.seekBarTriggerMultiplierText);
        textViewTrigger.setText(String.valueOf(seekBarTrigger.getProgress()));

        seekBarTrigger.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewTrigger.setText(String.valueOf(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                saveMultiplier(KEY_TRIGGER_MULTIPLIER,seekBarTrigger.getProgress());
            }
        });


        SeekBar seekBarTouchpad = findViewById(R.id.seekBarTouchpadMultiplier);
        seekBarTouchpad.setMin(1);
        seekBarTouchpad.setMax(100);
        seekBarTouchpad.setProgress(20);
        int savedTouchpadMultiplier = sharedPreferences.getInt(KEY_TOUCHPAD_MULTIPLIER, seekBarTouchpad.getProgress());
        seekBarTouchpad.setProgress(savedTouchpadMultiplier);

        TextView textViewToucpad = findViewById(R.id.seekBarTouchpadMultiplierText);
        textViewToucpad.setText(String.valueOf(seekBarTouchpad.getProgress()));

        seekBarTouchpad.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewToucpad.setText(String.valueOf(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                saveMultiplier(KEY_TOUCHPAD_MULTIPLIER,seekBarTouchpad.getProgress());
            }
        });

        boolean savedAnalogSwitch = sharedPreferences.getBoolean(KEY_USE_ANALOG_TRIGGER, false);
        SwitchCompat switchAnalogTrigger = findViewById(R.id.switchAnalogTrigger);
        switchAnalogTrigger.setChecked(savedAnalogSwitch);
        switchAnalogTrigger.setOnCheckedChangeListener((buttonView, isChecked) -> saveSwitch(KEY_USE_ANALOG_TRIGGER,isChecked));
    }

    private void saveMultiplier(String key,int sensorMultiplier) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, sensorMultiplier);
        editor.apply();
    }

    private void saveSwitch(String key ,boolean switchValue) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, switchValue);
        editor.apply();
    }
}
