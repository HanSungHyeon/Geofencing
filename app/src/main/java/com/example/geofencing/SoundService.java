package com.example.geofencing;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.IBinder;
import android.util.Log;

public class SoundService extends Service {
    SoundPool soundPool;
    int sound1,sound2;
    public SoundService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }
    @Override
    public void onCreate(){
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC,1);
        sound1 = soundPool.load(this,R.raw.sound1,1);
        sound2 = soundPool.load(this,R.raw.sound2,1);

    }
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){

        //soundPool.play(sound2,1,1,1,0,1f);
        int value = intent.getIntExtra("value",0);
        if(value == 1){
        soundPool.play(sound1,1,1,1,0,1f);}
        else if(value ==2){
            soundPool.play(sound2,1,1,1,0,1f);
        }
        return super .onStartCommand(intent,flags,startId);
    }


}