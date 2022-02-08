package com.example.birdsofafeather;

import android.app.Application;
import android.content.Context;

public class BoFApplication extends Application {
    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }
}