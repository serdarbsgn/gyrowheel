package com.serdarbsgn.gyrowheel;

import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.UUID;
//It can advertise just fine, but windows and phone couldn't establish the connection, so i failed here.
public class BluetoothLe {

    private static final String TAG = "BluetoothLe";

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeAdvertiser bluetoothLeAdvertiser;
    private BluetoothGattServer bluetoothGattServer;
    private BluetoothDevice connectedDevice;

    private static final UUID SERVICE_UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");
    private static final UUID CHARACTERISTIC_UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");

    private static BluetoothLe instance;
    private final Handler handler;
    private final HandlerThread handlerThread;
    private boolean isConnected = false;

    private BluetoothLe() {
        handlerThread = new HandlerThread("BluetoothLEThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    public static synchronized BluetoothLe getInstance() {
        if (instance == null) {
            instance = new BluetoothLe();
        }
        return instance;
    }

    public void initialize(BluetoothAdapter adapter, Context context) {
        handler.post(() -> {
            try {
                bluetoothAdapter = adapter;
                bluetoothAdapter.setName("GyroWheel LE");
                startAdvertising();
                setupGattServer(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void startAdvertising() {
        bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();

        if (bluetoothLeAdvertiser == null) {
            return;
        }

        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .build();

        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .setIncludeTxPowerLevel(true)
                .addServiceUuid(new ParcelUuid(SERVICE_UUID))
                .build();

        bluetoothLeAdvertiser.startAdvertising(settings, data, advertiseCallback);
    }

    private void stopAdvertising() {
        if (bluetoothLeAdvertiser == null) return;
        bluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
    }

    private final AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
        }
    };

    private void setupGattServer(Context context) {
        BluetoothManager bluetoothManager = getSystemService(context, BluetoothManager.class);
        bluetoothGattServer = bluetoothManager.openGattServer(context, gattServerCallback);

        BluetoothGattService service = new BluetoothGattService(SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);
        BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(
                CHARACTERISTIC_UUID,
                BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE
        );
        service.addCharacteristic(characteristic);
        bluetoothGattServer.addService(service);
    }

    private final BluetoothGattServerCallback gattServerCallback = new BluetoothGattServerCallback() {
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
            if (newState == BluetoothAdapter.STATE_CONNECTED) {
                Log.i(TAG, "Device connected: " + device.getAddress());
                isConnected = true;
                connectedDevice = device;
            } else if (newState == BluetoothAdapter.STATE_DISCONNECTED) {
                Log.i(TAG, "Device disconnected: " + device.getAddress());
                isConnected = false;
                connectedDevice = null;
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            if (CHARACTERISTIC_UUID.equals(characteristic.getUuid())) {
                bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, "Hello BLE".getBytes());
            }
        }

        @Override
        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
            super.onDescriptorReadRequest(device, requestId, offset, descriptor);
            if (CHARACTERISTIC_UUID.equals(descriptor.getUuid())) {
                bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, "Hello BLE".getBytes());
            }
        }
    };

    public void sendData(byte[] data) {
        handler.post(() -> {
            if (bluetoothGattServer != null && connectedDevice != null) {
                BluetoothGattCharacteristic characteristic = bluetoothGattServer
                        .getService(SERVICE_UUID)
                        .getCharacteristic(CHARACTERISTIC_UUID);
                characteristic.setValue(data);
                bluetoothGattServer.notifyCharacteristicChanged(connectedDevice, characteristic, false);
            }
        });
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void disconnect() {
        handler.post(() -> {
            if (connectedDevice != null) {
                bluetoothGattServer.cancelConnection(connectedDevice);
                isConnected = false;
                connectedDevice = null;
            }
        });
    }

    public void close() {
        handler.post(() -> {
            disconnect();
            if (bluetoothGattServer != null) {
                bluetoothGattServer.close();
            }
            stopAdvertising();
            handlerThread.quitSafely();
        });
    }
}
