package com.example.mymusic;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.mymusic.service_music.ServiceMusic;

public class MyBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        int actionMusic = intent.getIntExtra("action_music", 0);
        Log.wtf("BroadCast", "get Action");

        Intent intentService = new Intent(context, ServiceMusic.class);
        intentService.putExtra("action_service", actionMusic);
        context.startService(intentService);
    }
}
