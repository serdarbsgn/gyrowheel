package com.serdarbsgn.gyrowheel;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonNetwork = findViewById(R.id.useNetwork);
        buttonNetwork.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NetworkActivity.class);
            startActivity(intent);
        });

        Button buttonBluetooth = findViewById(R.id.useBluetooth);
        buttonBluetooth.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BluetoothActivity.class);
            startActivity(intent);
        });
        Button buttonSettings = findViewById(R.id.buttonSettings);
        buttonSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        Button buttonPrivacy = findViewById(R.id.buttonPrivacyPolicy);
        buttonPrivacy.setOnClickListener(v -> {
            String url = "https://serdarbisgin.com.tr/privacy-policy-for-gyrowheel-app";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });
        Button buttonSupport = findViewById(R.id.buttonSupportMe);
        MobileAds.initialize(this, initializationStatus -> {});
        buttonSupport.setOnClickListener(v -> {
            RewardedAd rewardedAd = GlobalSettings.getInstance().getRewardedAd();
            if (rewardedAd != null) {
                rewardedAd.show(MainActivity.this, rewardItem -> {
                    Toast.makeText(MainActivity.this, "Thank you for your support!", Toast.LENGTH_SHORT).show();
                    GlobalSettings.getInstance().setRewardedAd(null);
                    loadRewardedAd();
                });
            } else {
                Toast.makeText(MainActivity.this, "Ad is not ready yet, please press the button again.", Toast.LENGTH_SHORT).show();
                loadRewardedAd();
            }
        });
    }

    private void loadRewardedAd() {
        if(GlobalSettings.getInstance().isLoadingAd()){
            Toast.makeText(MainActivity.this, "Ad is still loading,press the button again after a while.", Toast.LENGTH_SHORT).show();
            return;
        }
        GlobalSettings.getInstance().setLoadingAd(true);
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, "AD_UNIT_ID", adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd ad) {
                GlobalSettings.getInstance().setRewardedAd(ad);
                GlobalSettings.getInstance().setLoadingAd(false);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                GlobalSettings.getInstance().setRewardedAd(null);
                GlobalSettings.getInstance().setLoadingAd(false);
            }
        });
    }
}
