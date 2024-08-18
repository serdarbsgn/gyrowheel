package com.serdarbsgn.gyrowheel;

import android.os.Handler;
import android.os.HandlerThread;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPOverInternet {

    private final String serverIp;
    private final int serverPort;
    private DatagramSocket udpSocket;
    private InetAddress serverAddress;
    private Handler handler;
    private HandlerThread handlerThread;
    public UDPOverInternet(String serverIp, int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        initSocket();
    }

    private void initSocket() {
        handlerThread = new HandlerThread("UDPSocketThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        handler.post(() -> {
            try {
                udpSocket = new DatagramSocket();
                serverAddress = InetAddress.getByName(serverIp);
            } catch (Exception ignored) {
            }
        });
    }

    public void sendData(String data) {
        byte[] dataBytes = data.getBytes();
        handler.post(() -> {
            try {
                DatagramPacket packet = new DatagramPacket(dataBytes, dataBytes.length, serverAddress, serverPort);
                udpSocket.send(packet);
            } catch (Exception ignored) {

            }
        });
    }

    public void close() {
        handler.post(() -> {
            if (udpSocket != null && !udpSocket.isClosed()) {
                udpSocket.close();
            }
            handlerThread.quitSafely();
        });
    }
}
