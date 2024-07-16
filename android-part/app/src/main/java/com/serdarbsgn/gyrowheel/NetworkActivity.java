package com.serdarbsgn.gyrowheel;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class NetworkActivity extends AppCompatActivity {

    private Boolean useEditedLayout = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_network);

        final EditText editTextSocketIp = findViewById(R.id.editTextSocketIp);

        Button buttonNetworkUdpGW = findViewById(R.id.networkUdp);
        buttonNetworkUdpGW.setOnClickListener(v -> {
            String ipAddress = editTextSocketIp.getText().toString();
            Intent intent = new Intent(NetworkActivity.this, GyroWheelActivity.class);
            intent.putExtra("SOCKET_IP", ipAddress);
            startActivity(intent);
        });

        Button buttonNetworkUdpGP = findViewById(R.id.networkUdpAllButtons);
        buttonNetworkUdpGP.setOnClickListener(v -> {
            String ipAddress = editTextSocketIp.getText().toString();
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
        useCustomLayout.setOnCheckedChangeListener((buttonView, isChecked) ->useEditedLayout = isChecked);
    }
}
