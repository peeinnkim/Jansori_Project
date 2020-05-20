package com.example.jansori_project.application;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class JansoriApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
