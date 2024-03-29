package com.example.geofencing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.geofencing.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
    private float GEOFENCE_RADIUS = 100;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";
    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUD_LOCATION_ACCESS_REQUEST_CODE = 10002;
    ArrayList<Taas> taaslist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Add a marker in Sydney and move the camera
        LatLng Daejeon = new LatLng(36.3353, 127.4565);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Daejeon, 16));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            enableUserLocation();
        }
        mMap.setOnMapLongClickListener(this);
        //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
        taaslist();
    }



    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("위치정보")
                        .setMessage("이 앱을 사용하기 위해서는 위치정보에 접근이 필요합니다. 위치정보 접근을 허용하여 주세요.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        }).create().show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
        if(requestCode == BACKGROUD_LOCATION_ACCESS_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "지오펜스 추가 가능",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"백그라운드",Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        if(Build.VERSION.SDK_INT >= 29){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_BACKGROUND_LOCATION)==
                    PackageManager.PERMISSION_GRANTED) {
                tryAddingGeofence(latLng);
            }else{
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_BACKGROUND_LOCATION)){
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},BACKGROUD_LOCATION_ACCESS_REQUEST_CODE);
                }else{
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},BACKGROUD_LOCATION_ACCESS_REQUEST_CODE);
                }
            }
        }
        else{
            tryAddingGeofence(latLng);
        }
    }

    private void tryAddingGeofence(LatLng latLng){
        //mMap.clear();
        addMarker(latLng);
        addCircle(latLng, GEOFENCE_RADIUS);
        //addGeofence(latLng, GEOFENCE_RADIUS);
    }

    private void addGeofence(/*LatLng latLng*/Taas taas, float radius) {
        LatLng position = new LatLng(taas.getlat(),taas.getlon());
        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, position, radius, Geofence.GEOFENCE_TRANSITION_ENTER |
                Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();



        try{
            geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            Log.d(TAG, "onSUCCESS: Geofence Added...");
                            Log.d(TAG,"지오펜스 추가");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            String errorMessage = geofenceHelper.getErrorString(e);
                            Log.d(TAG, "onFailure" + errorMessage);
                        }
                    });}catch (SecurityException e){e.printStackTrace();}
    }



    private void addMarker(LatLng latLng)   {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng);
        mMap.addMarker(markerOptions);

    }

    private void addCircle(LatLng latLng,float radius){
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255,255,0,0));
        circleOptions.fillColor(Color.argb(64,255,0,0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);

    }


    public void taaslist(){
        ArrayList<Taas> taaslist = new ArrayList<>();
        taaslist.add(new Taas(36.3158, 127.4078));
        taaslist.add (new Taas(36.3158, 127.4078));
        taaslist.add (new Taas(36.3928, 127.3106));
        taaslist.add (new Taas(36.3475, 127.3672));
        taaslist.add (new Taas(36.3297, 127.4335));
        taaslist.add (new Taas(36.3270, 127.4355));
        taaslist.add (new Taas(36.3427, 127.4355));
        taaslist.add (new Taas(36.3203, 127.4467));
        taaslist.add (new Taas(36.3146, 127.4388));
        taaslist.add (new Taas(36.3196, 127.4159));
        taaslist.add (new Taas(36.3215, 127.4092));
        taaslist.add (new Taas(36.32624466592023, 127.39608393854492));
        taaslist.add (new Taas(36.3046052445744, 127.38616914902406));
        taaslist.add (new Taas(36.30838571585318, 127.3750953326642));
        taaslist.add (new Taas(36.306975850624596, 127.33510896329774));
        taaslist.add (new Taas(36.35336983785516, 127.33875278390722));
        taaslist.add (new Taas(36.35060962662878, 127.4414406951592));
        taaslist.add (new Taas(36.32411811141908, 127.42755648512328));
        taaslist.add(new Taas(36.32865393299976, 127.43417773248085));
        taaslist.add(new Taas(36.34471660519038, 127.40173571154426));
        taaslist.add(new Taas(36.36505739472767, 127.3782640758318));
        taaslist.add(new Taas(36.35288572709863, 127.36878766972998));
        taaslist.add(new Taas(36.351348516866686, 127.37846813684827));
        taaslist.add(new Taas(36.35095066401503, 127.34009851701767));
        taaslist.add(new Taas(36.317063281324785, 127.39165994408131));
        taaslist.add(new Taas(36.322995374331484, 127.39579731060913));
        taaslist.add(new Taas(36.34902224828835, 127.3900114546842));
        taaslist.add(new Taas(36.34613602238069, 127.3847650538648));
        taaslist.add(new Taas(36.33855285707191, 127.3931871314198));
        taaslist.add(new Taas(36.43466707455801, 127.38525963494413));
        taaslist.add(new Taas(36.32965614053835, 127.45449064605627));

        for(Taas taas:taaslist){
            addMarkertest(taas);
            addCircleTest(taas,200);
            addGeofence(taas,200);
        }
    }

    private Marker addMarkertest(Taas taas){
        LatLng position = new LatLng(taas.getlat(),taas.getlon());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);

        return mMap.addMarker(markerOptions);
    }
    private Circle addCircleTest(Taas taas,float radius){
        LatLng position = new LatLng(taas.getlat(),taas.getlon());
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(position);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255,255,0,0));
        circleOptions.fillColor(Color.argb(64,255,0,0));
        circleOptions.strokeWidth(4);
        return mMap.addCircle(circleOptions);
    }
}