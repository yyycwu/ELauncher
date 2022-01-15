package com.moan.launcher;

import android.app.Application;
import android.os.Build;

import com.moan.launcher.utils.NotificationChannels;


public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannels.createAllNotificationChannels(this);
        }
    }
}
