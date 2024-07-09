package com.serdarbsgn.gyrowheel;

public class GlobalSettings {
    private static GlobalSettings instance;
    private boolean blcModeUuid;

    private GlobalSettings() {
        blcModeUuid = false; // default value
    }

    public static synchronized GlobalSettings getInstance() {
        if (instance == null) {
            instance = new GlobalSettings();
        }
        return instance;
    }

    public boolean isBlcModeUuid() {
        return blcModeUuid;
    }

    public void setBlcModeUuid(boolean blcModeUuid) {
        this.blcModeUuid = blcModeUuid;
    }
}
