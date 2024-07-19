package com.serdarbsgn.gyrowheel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class NetworkActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "com.serdarbsgn.gyrowheel.PREFS";
    private static final String KEY_SOCKET_IP = "SOCKET_IP";

    private Boolean useEditedLayout = false;
    private EditText editTextSocketIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);

        editTextSocketIp = findViewById(R.id.editTextSocketIp);

        // Load the last saved IP address
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedIpAddress = sharedPreferences.getString(KEY_SOCKET_IP, editTextSocketIp.getText().toString());
        editTextSocketIp.setText(savedIpAddress);

        Button buttonNetworkUdpGW = findViewById(R.id.networkUdp);
        buttonNetworkUdpGW.setOnClickListener(v -> {
            String ipAddress = editTextSocketIp.getText().toString();
            saveIpAddress(ipAddress);
            Intent intent = new Intent(NetworkActivity.this, GyroWheelActivity.class);
            intent.putExtra("SOCKET_IP", ipAddress);
            startActivity(intent);
        });

        Button buttonNetworkUdpGP = findViewById(R.id.networkUdpAllButtons);
        buttonNetworkUdpGP.setOnClickListener(v -> {
            String ipAddress = editTextSocketIp.getText().toString();
            saveIpAddress(ipAddress);
            Intent intent = new Intent(NetworkActivity.this, GamepadActivity.class);
            intent.putExtra("SOCKET_IP", ipAddress);
            intent.putExtra("CUSTOM_LAYOUT", useEditedLayout);
            startActivity(intent);
        });

        Button edit = findViewById(R.id.editLayout);
        edit.setOnClickListener(v -> {
            Intent intent = new Intent(NetworkActivity.this, EditableActivity.class);
            startActivity(intent);
        });

        Switch useCustomLayout = findViewById(R.id.useCustomLayout);
        useCustomLayout.setOnCheckedChangeListener((buttonView, isChecked) -> useEditedLayout = isChecked);
    }

    private void saveIpAddress(String ipAddress) {
        // Save the IP address to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SOCKET_IP, ipAddress);
        editor.apply();
    }
}
