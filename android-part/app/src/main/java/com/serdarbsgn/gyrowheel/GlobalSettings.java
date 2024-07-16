package com.serdarbsgn.gyrowheel;

import com.google.android.gms.ads.rewarded.RewardedAd;

public class GlobalSettings {
    private static GlobalSettings instance;
    private boolean blcModeUuid;
    private RewardedAd rewardedAd;
    private boolean isLoadingAd;

    private GlobalSettings() {
        blcModeUuid = false; // default value
        rewardedAd = null;
        isLoadingAd = false;
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

    public RewardedAd getRewardedAd() {
        return rewardedAd;
    }

    public void setRewardedAd(RewardedAd rewardedAd) {
        this.rewardedAd = rewardedAd;
    }

    public boolean isLoadingAd() {
        return isLoadingAd;
    }

    public void setLoadingAd(boolean loadingAd) {
        isLoadingAd = loadingAd;
    }
}
