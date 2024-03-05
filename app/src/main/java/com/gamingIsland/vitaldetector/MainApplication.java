package com.gamingIsland.vitaldetector;

import android.app.Application;

import com.onesignal.OneSignal;

public class MainApplication extends Application {

    private static final String ONESIGNAL_APP_ID = "f33de226-2f2e-4acb-b25b-7a7f01cfec93";

    @Override
    public void onCreate() {
        super.onCreate();

        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

    }
}
