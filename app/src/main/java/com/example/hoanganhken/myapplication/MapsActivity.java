package com.example.hoanganhken.myapplication;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import de.greenrobot.event.EventBus;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, RoutingListener {

    private GoogleMap mMap;
    private  LatLng currenLocation;
    private  LatLng friendLocation;
    private User friendUser;
    private User currenUser;
    private Firebase friendUrl;
    Button btnRoutting;
    Button btnCancelRoutting;
    private boolean routting = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        String jsonReceiverUser = getIntent().getStringExtra(Constant.KEY_SEND_USER).split("---")[0];
        String jsonCurrenUser = getIntent().getStringExtra(Constant.KEY_SEND_USER).split("---")[1];
        Gson gson = new Gson();
        friendUser = gson.fromJson(jsonReceiverUser, User.class);
        currenUser = gson.fromJson(jsonCurrenUser, User.class);
        currenLocation = new LatLng(currenUser.latitude, currenUser.longitude);
        friendLocation = new LatLng(friendUser.latitude, friendUser.longitude);
        addControl();
        addEvenrt();
    }
    private void addControl() {
        btnRoutting =(Button) findViewById(R.id.btnRoutting);
        btnCancelRoutting =(Button) findViewById(R.id.btnCancelRoutting);
    }
    private void addEvenrt() {
        btnRoutting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routting = true;
                routing(currenLocation, friendLocation);
            }
        });
        btnCancelRoutting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routting = false;
                routing(currenLocation, friendLocation);
            }
        });
    }
   private ValueEventListener valueEventListenerFriendUser = new ValueEventListener() {
       @Override
       public void onDataChange(DataSnapshot dataSnapshot) {
           User user = dataSnapshot.getValue(User.class);
           friendLocation = new LatLng(user.latitude, user.latitude);
           routing(currenLocation, friendLocation);
       }

       @Override
       public void onCancelled(FirebaseError firebaseError) {

       }
   };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            EventBus.getDefault().unregister(this);
            friendUrl.removeEventListener(valueEventListenerFriendUser);
        }catch (Exception e){

        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        friendUrl = new Firebase(Constant.FIREBASE_CHAT_URL).child(Constant.CHILD_USERS).child(friendUser.id);
        friendUrl.addValueEventListener(valueEventListenerFriendUser);
        EventBus.getDefault().register(this);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(currenLocation);
                builder.include(friendLocation);
                LatLngBounds bounds = builder.build();
                try {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 80));
                }catch (Exception e){

                }
            }
        },500);
    }


    public void onEvent(Location currenLocation){
        LatLng lng = new LatLng(currenLocation.getLatitude(),currenLocation.getLongitude());
        routing(lng, friendLocation);
    }
// vẽ đường đi tính khoảng cách
    public void routing(LatLng a, LatLng b) {
        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.DRIVING)
                .withListener(MapsActivity.this)
                .waypoints(a, b)
                .build();
        routing.execute();
    }



    @Override
    public void onRoutingFailure() {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingCancelled() {

    }
    @Override
    public void onRoutingSuccess(PolylineOptions polylineOptions, Route route) {
        try {
            mMap.clear();
            PolylineOptions polyoptions = new PolylineOptions();
            polyoptions.color(Color.BLUE);
            polyoptions.width(10);
            polyoptions.addAll(polylineOptions.getPoints());
            if (routting){
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(currenLocation);
                builder.include(friendLocation);
                LatLngBounds bounds = builder.build();
                try {
                    mMap.animateCamera(CameraUpdateFactory
                            .newLatLngBounds(bounds, 80));
                } catch (Exception e) {
                }
                mMap.addPolyline(polyoptions);
            }
            String distance = route.getDistanceText();
            mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(MapsActivity.this));

            Marker markerB = mMap.addMarker(new MarkerOptions().position(friendLocation)
                    .title(friendUser.name + "-" + distance).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            markerB.showInfoWindow();
        } catch (Exception e) {
        }
    }


    public class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private View myContentsView;

        public MyInfoWindowAdapter(Activity context) {
            myContentsView = context.getLayoutInflater().inflate(
                    R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoContents(Marker marker) {
            TextView tvName = (TextView) myContentsView.findViewById(R.id.tvName);
            TextView tvDistance = (TextView) myContentsView.findViewById(R.id.tvDistance);
            TextView tvAddress = (TextView) myContentsView.findViewById(R.id.tvAddress);
            tvName.setText(marker.getTitle().split("-")[0]);
            tvDistance.setText(marker.getTitle().split("-")[1]);
            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }
    }
}
