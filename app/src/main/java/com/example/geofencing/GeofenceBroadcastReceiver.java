package com.example.geofencing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastReceiv";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //Toast.makeText(context, "Geofence triggered...",Toast.LENGTH_SHORT).show();
        NotificationHelper notificationHelper = new NotificationHelper(context);
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        //SoundManger.initSounds(context.getApplicationContext());


        if(geofencingEvent.hasError()){
            Log.d(TAG,"리시버 에러");
        }
        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for(Geofence geofence: geofenceList){
            Log.d(TAG,"onReceive: "+geofence.getRequestId());
        }

        int transitionType = geofencingEvent.getGeofenceTransition();

        if(transitionType == Geofence.GEOFENCE_TRANSITION_ENTER){
            Toast.makeText(context,"보행자 사고 다발지역 진입",Toast.LENGTH_SHORT).show();
            notificationHelper.sendHighPriorityNotification("보행자 사고 다발지역 진입","보행자 사고 다발지역 진입",MapsActivity.class);
            Intent intent1 = new Intent(context,SoundService.class);
            intent1.putExtra("value",1);
            context.startService(intent1);

        }
        else if(transitionType == Geofence.GEOFENCE_TRANSITION_EXIT){
            Toast.makeText(context,"보행자 사고 다발지역 이탈",Toast.LENGTH_SHORT).show();
            notificationHelper.sendHighPriorityNotification("보행자 사고 다발지역 이탈","보행자 사고 다발지역 이탈",MapsActivity.class);
            Intent intent1 = new Intent(context,SoundService.class);
            intent1.putExtra("value",2);
            context.startService(intent1);
        }
        /*switch (transitionType){
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(context,"보행자 사고 다발지역 진입",Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("보행자 사고 다발지역 진입","보행자 사고 다발지역 진입",MapsActivity.class);
                Intent intent1 = new Intent(context,SoundService.class);
                intent1.putExtra("value",1);
                context.startService(intent1);

                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Toast.makeText(context,"보행자 사고 다발지역입니다",Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("보행자 사고 다발지역","보행자 사고 다발지역",MapsActivity.class);
                Intent intent1 = new Intent(context,SoundService.class);
                intent1.putExtra("value",1);
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(context,"보행자 사고 다발지역 이탈",Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("보행자 사고 다발지역 이탈","보행자 사고 다발지역 이탈",MapsActivity.class);
                Intent intent3 = new Intent(context,SoundService.class);
                *//*intent3.putExtra("value",2);
                context.startService(new Intent(context,SoundService.class));*//*
                context.startService(new Intent(context,SoundService.class));
                break;
        }*/
    }
}