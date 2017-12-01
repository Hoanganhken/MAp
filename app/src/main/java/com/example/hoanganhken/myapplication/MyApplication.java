package com.example.hoanganhken.myapplication;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by Hoang Anh Ken on 12/1/2017.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}