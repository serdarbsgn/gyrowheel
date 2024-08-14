package com.serdarbsgn.gyrowheel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;

@SuppressLint("MissingPermission")
public class BluetoothActivity extends AppCompatActivity {

    private BluetoothConn bluetoothConn;
    private BluetoothAdapter bluetoothAdapter;
    private final String TAG = "BluetoothActivity";
    private static final int REQUEST_PERMISSION_BT = 1;
    private static final int REQUEST_PERMISSION_BT_CONNECT = 2;
    private ArrayAdapter<String> devicesArrayAdapter;
    private ArrayList<BluetoothDevice> devicesList;
    private Boolean useEditedLayout = false;
    private static final String PREFS_NAME = "com.serdarbsgn.gyrowheel.PREFS";
    private static final String KEY_MAC_ADDRESS = "MAC_ADDRESS";
    private String tempMacAddress,tempType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        devicesArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        devicesList = new ArrayList<>();

        ListView listViewDevices = findViewById(R.id.listViewDevices);
        listViewDevices.setAdapter(devicesArrayAdapter);
        listViewDevices.setOnItemClickListener((parent, view, position, id) -> {
            BluetoothDevice device = devicesList.get(position);
            EditText editTextBluetoothMac = findViewById(R.id.editTextBluetoothMAC);
            editTextBluetoothMac.setText(device.getAddress());
        });

        final EditText editTextBluetoothMac = findViewById(R.id.editTextBluetoothMAC);
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedMACAddress = sharedPreferences.getString(KEY_MAC_ADDRESS, editTextBluetoothMac.getText().toString());
        editTextBluetoothMac.setText(savedMACAddress);
        Switch forwardedSocket = findViewById(R.id.switchForwardedSocket);
        forwardedSocket.setOnCheckedChangeListener((buttonView, isChecked) -> GlobalSettings.getInstance().setBlcModeUuid(isChecked));
        Switch useCustomLayout = findViewById(R.id.useCustomLayout);
        useCustomLayout.setOnCheckedChangeListener((buttonView, isChecked) -> useEditedLayout = isChecked);

        Button buttonBtConnect = findViewById(R.id.bluetoothConnectMAC);
        buttonBtConnect.setOnClickListener(v -> {
            String macAddress = editTextBluetoothMac.getText().toString();
            saveMACAddress(macAddress);
            if (MACAddressValidator.isValidMACAddress(macAddress)) {
                requestBluetoothPermissions(macAddress, "Connect");
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.invalid_mac), Toast.LENGTH_SHORT).show();
            }
        });
        Button buttonBtGW = findViewById(R.id.bluetooth);
        buttonBtGW.setOnClickListener(v -> {
            Intent intent = new Intent(BluetoothActivity.this, GyroWheelActivity.class);
            intent.putExtra("USE_BLUETOOTH", true);
            startActivity(intent);
        });
        Button buttonBtGP = findViewById(R.id.bluetoothAllButtons);
        buttonBtGP.setOnClickListener(v -> {
            Intent intent = new Intent(BluetoothActivity.this, GamepadActivity.class);
            intent.putExtra("USE_BLUETOOTH", true);
            intent.putExtra("CUSTOM_LAYOUT", useEditedLayout);
            startActivity(intent);
        });
        Button buttonBtKM = findViewById(R.id.bluetoothKeyboardMouse);
        buttonBtKM.setOnClickListener(v -> {
            Intent intent = new Intent(BluetoothActivity.this, KeyboardAndMouseActivity.class);
            intent.putExtra("USE_BLUETOOTH", true);
            startActivity(intent);
        });
        buttonBtGW.setEnabled(false);
        buttonBtGP.setEnabled(false);
        buttonBtKM.setEnabled(false);

        findViewById(R.id.bluetoothShowComputers).setOnClickListener(v -> {
            requestBluetoothPermissions(null,"Scan");
        });
        Button edit = findViewById(R.id.editLayout);
        edit.setOnClickListener(v -> {
            Intent intent = new Intent(BluetoothActivity.this, EditableActivity.class);
            startActivity(intent);
        });
    }

    private void initializeBluetoothConnection(String macAddress) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled()){
            Toast.makeText(this, getString(R.string.please_enable_bluetooth), Toast.LENGTH_SHORT).show();
        }
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
                findViewById(R.id.bluetoothKeyboardMouse).setEnabled(true);
            });
        }).start();
        Toast.makeText(this, getString(R.string.button_available_after_conn), Toast.LENGTH_SHORT).show();
    }

    private void requestBluetoothPermissions(String macAddress, String type) {
        tempMacAddress = macAddress;
        tempType = type;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Requesting BLUETOOTH_CONNECT and BLUETOOTH_SCAN permission");
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.BLUETOOTH_CONNECT,
                        android.Manifest.permission.BLUETOOTH_SCAN
                }, REQUEST_PERMISSION_BT_CONNECT);
            } else {
                handleBluetoothAction(tempMacAddress, tempType);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Requesting BLUETOOTH, BLUETOOTH_ADMIN and ACCESS_COARSE_LOCATION permissions");
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.BLUETOOTH,
                        android.Manifest.permission.BLUETOOTH_ADMIN
                }, REQUEST_PERMISSION_BT);
            } else {
                handleBluetoothAction(tempMacAddress, tempType);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_BT_CONNECT) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                handleBluetoothAction(tempMacAddress, tempType);
            } else {
                Toast.makeText(this, getString(R.string.permission_declined), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_PERMISSION_BT) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                handleBluetoothAction(tempMacAddress, tempType);
            } else {
                Toast.makeText(this, getString(R.string.permission_declined), Toast.LENGTH_SHORT).show();
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
        @SuppressLint("MissingPermission")
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null && device.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.COMPUTER){
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

    private void saveMACAddress(String macAddress) {
        // Save the IP address to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_MAC_ADDRESS, macAddress);
        editor.apply();
    }
}