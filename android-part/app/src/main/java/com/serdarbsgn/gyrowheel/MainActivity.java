package com.serdarbsgn.gyrowheel;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private BluetoothConn bluetoothConn;
    private BluetoothAdapter bluetoothAdapter;
    private final String TAG = "MainActivity";
    private static final int REQUEST_PERMISSION_BT = 1;
    private static final int REQUEST_PERMISSION_BT_CONNECT = 2;
    private ArrayAdapter<String> devicesArrayAdapter;
    private ArrayList<BluetoothDevice> devicesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        devicesArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        devicesList = new ArrayList<>();

        ListView listViewDevices = findViewById(R.id.listViewDevices);
        listViewDevices.setAdapter(devicesArrayAdapter);
        listViewDevices.setOnItemClickListener((parent, view, position, id) -> {
            BluetoothDevice device = devicesList.get(position);
            EditText editTextBluetoothMac = findViewById(R.id.editTextBluetoothMAC);
            editTextBluetoothMac.setText(device.getAddress());
        });

        final EditText editTextSocketIp = findViewById(R.id.editTextSocketIp);
        Button buttonNetworkUdpGW = findViewById(R.id.networkUdp);
        buttonNetworkUdpGW.setOnClickListener(v -> {
            String ipAddress = editTextSocketIp.getText().toString();
            Intent intent = new Intent(MainActivity.this, GyroWheelActivity.class);
            intent.putExtra("SOCKET_IP", ipAddress);
            startActivity(intent);
        });

        Button buttonNetworkUdpGP = findViewById(R.id.networkUdpAllButtons);
        buttonNetworkUdpGP.setOnClickListener(v -> {
            String ipAddress = editTextSocketIp.getText().toString();
            Intent intent = new Intent(MainActivity.this, GamepadActivity.class);
            intent.putExtra("SOCKET_IP", ipAddress);
            startActivity(intent);
        });

        final EditText editTextBluetoothMac = findViewById(R.id.editTextBluetoothMAC);
        Button buttonBtConnect = findViewById(R.id.bluetoothConnectMAC);
        buttonBtConnect.setOnClickListener(v -> {
            String macAddress = editTextBluetoothMac.getText().toString();
            requestBluetoothPermissions(macAddress,"Connect");
        });

        Button buttonBtGW = findViewById(R.id.bluetooth);
        buttonBtGW.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GyroWheelActivity.class);
            intent.putExtra("USE_BLUETOOTH", true);
            startActivity(intent);
        });
        Button buttonBtGP = findViewById(R.id.bluetoothAllButtons);
        buttonBtGP.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GamepadActivity.class);
            intent.putExtra("USE_BLUETOOTH", true);
            startActivity(intent);
        });
        buttonBtGW.setEnabled(false);
        buttonBtGP.setEnabled(false);

        findViewById(R.id.bluetoothShowComputers).setOnClickListener(v -> {
            requestBluetoothPermissions(null,"Scan");
        });
    }

    private void initializeBluetoothConnection(String macAddress) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);
        bluetoothConn = BluetoothConn.getInstance();
        bluetoothConn.initialize(bluetoothAdapter, device);
        new Thread(() -> {
            while (bluetoothConn != null && !bluetoothConn.isConnected()) {
                try {
                    Thread.sleep(100); // Check connection status every 100ms
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            runOnUiThread(() -> {
                findViewById(R.id.bluetooth).setEnabled(true);
                findViewById(R.id.bluetoothAllButtons).setEnabled(true);
            });
        }).start();
        Toast.makeText(this, "Bluetooth Connected", Toast.LENGTH_SHORT).show();
    }

    private void requestBluetoothPermissions(String macAddress,String type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Requesting BLUETOOTH_CONNECT and BLUETOOTH_SCAN permission");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT,android.Manifest.permission.BLUETOOTH_SCAN}, REQUEST_PERMISSION_BT_CONNECT);
            }else {
                handleBluetoothAction(macAddress, type);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Requesting BLUETOOTH and BLUETOOTH_ADMIN permissions");
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.BLUETOOTH,
                        android.Manifest.permission.BLUETOOTH_ADMIN
                }, REQUEST_PERMISSION_BT);
            } else {
                handleBluetoothAction(macAddress, type);
            }
        }
    }

    private void handleBluetoothAction(String macAddress, String type) {
        if (type.equals("Connect")) {
            Log.d(TAG, "Permissions already granted, creating Bluetooth connection");
            initializeBluetoothConnection(macAddress);
        } else if (type.equals("Scan")) {
            Log.d(TAG, "Permissions already granted, searching Bluetooth computers");
            discoverDevices();
        }
    }
    private void discoverDevices() {
        devicesArrayAdapter.clear();
        devicesList.clear();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();
        IntentFilter filter;
        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    devicesList.add(device);
                    devicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    devicesArrayAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothConn != null) {
            bluetoothConn.close();
        }
    }
}
