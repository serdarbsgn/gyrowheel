package com.serdarbsgn.gyrowheel;

import android.os.Handler;
import android.os.HandlerThread;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class TCPSocketOverADB {

    private final String serverIp;
    private final int serverPort;
    private Socket tcpSocket;
    private BufferedWriter writer;
    private Handler handler;
    private HandlerThread handlerThread;

    public TCPSocketOverADB(String serverIp, int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        initSocket();
    }

    private void initSocket() {
        handlerThread = new HandlerThread("TCPSocketThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        handler.post(() -> {
            try {
                tcpSocket = new Socket();
                tcpSocket.setTcpNoDelay(true);
                tcpSocket.setPerformancePreferences(1, 2, 0);
                tcpSocket.connect(new java.net.InetSocketAddress(serverIp, serverPort));
                writer = new BufferedWriter(new OutputStreamWriter(tcpSocket.getOutputStream()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void sendData(String data) {
        handler.post(() -> {
            try {
                writer.write(data);
                writer.newLine(); // Write newline character to indicate the end of the message
                writer.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void close() {
        handler.post(() -> {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (tcpSocket != null && !tcpSocket.isClosed()) {
                    tcpSocket.close();
                }
                handlerThread.quitSafely();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
