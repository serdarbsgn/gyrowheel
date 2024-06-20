package com.serdarbsgn.gyrowheel;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class USBHelper {

    private final File file;
    private BufferedWriter writer;

    public USBHelper(Context context) {
        // Get the app's package-specific directory
        File appDir = context.getExternalFilesDir(null);

        // Create a file in the directory
        file = new File(appDir, "inputs");
        try {
            writer = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

        public void sendInputOverADB(String data){
            try {
                writer = new BufferedWriter(new FileWriter(file));
                writer.write(data);
                writer.newLine();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    public void closeFile() {
        // Close the BufferedWriter
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Delete the file if it exists
        if (file != null && file.exists()) {
            file.delete();
        }
    }
}

