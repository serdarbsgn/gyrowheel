package com.serdarbsgn.gyrowheel;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;
@SuppressLint("MissingPermission")
public class BluetoothConn {

    private static final String TAG = "BluetoothConn";
    private static BluetoothConn instance;
    private boolean isConnected = false;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private final Handler handler;
    private final HandlerThread handlerThread;

    private BluetoothConn() {
        handlerThread = new HandlerThread("BluetoothThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    public static synchronized BluetoothConn getInstance() {
        if (instance == null) {
            instance = new BluetoothConn();
        }
        return instance;
    }

    // Initialize the Bluetooth connection
    public void initialize(BluetoothAdapter adapter, BluetoothDevice device) {
        handler.post(() -> {
            try {
                bluetoothAdapter = adapter;
                bluetoothAdapter.setName("GyroWheel");
                connect(device);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public void connect(BluetoothDevice device) {
        boolean useUuid = GlobalSettings.getInstance().isBlcModeUuid();
        handler.post(() -> {
            try {
                if (useUuid) {
                    bluetoothSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                } else {
                    Method m = device.getClass().getMethod("createRfcommSocket", int.class);
                    bluetoothSocket = (BluetoothSocket) m.invoke(device, 10);
                }
                bluetoothAdapter.cancelDiscovery();

                bluetoothSocket.connect();
                Log.d(TAG, "Connected to " + device.getName());
                isConnected = true;

            } catch (Exception e) {
                Log.e(TAG, "Error connecting to " + device.getName(), e);
                disconnect();
            }
        });
    }

    public void sendData(byte[] data) {
        handler.post(() -> {
            if (bluetoothSocket != null) {
                try {
                    bluetoothSocket.getOutputStream().write(data);
                } catch (IOException e) {
                    Log.e(TAG, "Error writing to output stream", e);
                }
            }
        });
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void disconnect() {
        handler.post(() -> {
            if (bluetoothSocket != null) {
                try {
                    bluetoothSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing socket", e);
                }
                bluetoothSocket = null;
                isConnected=false;
            }
        });
    }

    public void close() {
        handler.post(() -> {
            disconnect();
            handlerThread.quitSafely();
        });
        instance = null;
    }
}
