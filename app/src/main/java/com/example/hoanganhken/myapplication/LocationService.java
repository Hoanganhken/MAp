package com.example.hoanganhken.myapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import de.greenrobot.event.EventBus;

/**
 * Created by Hoang Anh Ken on 12/1/2017.
 */

public class LocationService extends Service {
    private LocationManager locationManager;
    private MyLocationListener myLocationListener;
    private Firebase rootUrl;
    private Firebase urlCurrenUser;
    private Firebase.AuthStateListener mAuthStateListener;
    public static double lattitude=0;
    public static double longitude=0;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        this.myLocationListener = new MyLocationListener();
        rootUrl = new Firebase(Constant.FIREBASE_CHAT_URL);
        mAuthStateListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                setAuthenticatedUser(authData);
            }
        };
        rootUrl.addAuthStateListener(mAuthStateListener);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, myLocationListener);
    }

    private void setAuthenticatedUser(AuthData authData) {
        if (authData != null) {
            urlCurrenUser = new Firebase(Constant.FIREBASE_CHAT_URL).child(Constant.CHILD_USERS).child(authData.getUid());
        } else {
            urlCurrenUser=null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            rootUrl.removeAuthStateListener(mAuthStateListener);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            locationManager.removeUpdates(myLocationListener);
        } catch (Exception e) {
        }
    }
    //lăng nghe sự kiện khi vị trí hiện tại của mình thay đổi
    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            try {
                urlCurrenUser.child(Constant.CHILD_LATITUDE).setValue(location.getLatitude());
                urlCurrenUser.child(Constant.CHILD_LONGITUDE).setValue(location.getLongitude());
            }catch (Exception e){}
            lattitude=location.getLatitude();
            longitude=location.getLongitude();
            EventBus.getDefault().post(location);
            Log.d("lam", "onLocationChanged:lam "+location.getLatitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }


    }
}
