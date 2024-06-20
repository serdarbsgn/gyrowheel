package com.serdarbsgn.gyrowheel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText editTextIpAddress = findViewById(R.id.editTextIpAddress);
        final EditText editTextSocketIp = findViewById(R.id.editTextSocketIp);

        Button buttonConfirm = findViewById(R.id.buttonConfirm);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipAddress = editTextIpAddress.getText().toString();

                // Pass the IP address to the new activity
                Intent intent = new Intent(MainActivity.this, GyroWheelActivity.class);
                intent.putExtra("IP_ADDRESS", ipAddress);
                startActivity(intent);
            }
        });
        Button buttonConfirmAllButtons = findViewById(R.id.buttonConfirmAllButtons);
        buttonConfirmAllButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipAddress = editTextIpAddress.getText().toString();

                // Pass the IP address to the new activity
                Intent intent = new Intent(MainActivity.this, GamepadActivity.class);
                intent.putExtra("IP_ADDRESS", ipAddress);
                startActivity(intent);
            }
        });
        Button buttonNetworkUdpGW = findViewById(R.id.networkUdp);
        buttonNetworkUdpGW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipAddress = editTextSocketIp.getText().toString();

                // Pass the IP address to the new activity
                Intent intent = new Intent(MainActivity.this, GyroWheelActivity.class);
                intent.putExtra("SOCKET_IP", ipAddress);
                startActivity(intent);
            }
        });
        Button buttonNetworkUdpGP = findViewById(R.id.networkUdpAllButtons);
        buttonNetworkUdpGP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipAddress = editTextSocketIp.getText().toString();

                // Pass the IP address to the new activity
                Intent intent = new Intent(MainActivity.this, GamepadActivity.class);
                intent.putExtra("SOCKET_IP", ipAddress);
                startActivity(intent);
            }
        });
        Button buttonUsb = findViewById(R.id.buttonUsb);
        buttonUsb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GyroWheelActivity.class);
                intent.putExtra("IP_ADDRESS", (String) null);
                startActivity(intent);
            }
        });
    }
}
