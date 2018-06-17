package com.mcc.wallpaper.app;

import android.app.Application;
import android.content.Context;

import com.google.firebase.messaging.FirebaseMessaging;

public class MyApplication extends Application {

    private static Application application;

    @Override
    public void onCreate() {
        application = this;
        super.onCreate();

        FirebaseMessaging.getInstance().subscribeToTopic("wallpaper");
    }

    public static Context getContext() {
        return application.getApplicationContext();
    }
}