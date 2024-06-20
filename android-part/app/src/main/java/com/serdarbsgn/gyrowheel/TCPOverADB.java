package com.serdarbsgn.gyrowheel;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TCPOverADB {

    public static void sendPostRequest(final String ip, final JSONObject jsonData) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Create a URL object
                    URL url = new URL(ip);
                    // Open a connection
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    // Write the data
                    conn.setRequestProperty("Content-Type", "application/json");
                    OutputStream os = conn.getOutputStream();
                    os.write(jsonData.toString().getBytes());
                    os.flush();
                    os.close();
                    conn.getResponseCode();
                    // Close the connection
                    conn.disconnect();
                } catch (IOException e) {

                }
            }
        });
        thread.start(); // Starts the thread
    }
}
