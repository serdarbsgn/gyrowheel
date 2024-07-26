package com.serdarbsgn.gyrowheel;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "com.serdarbsgn.gyrowheel.PREFS";
    private static final String KEY_SENSOR_MULTIPLIER = "SENSOR_MULTIPLIER";
    private static final String KEY_TOUCH_MULTIPLIER = "TOUCH_MULTIPLIER";
    private static final String KEY_SMOOTH_MULTIPLIER = "SMOOTH_MULTIPLIER";
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
    }

    private void saveMultiplier(String key,int sensorMultiplier) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, sensorMultiplier);
        editor.apply();
    }
}
