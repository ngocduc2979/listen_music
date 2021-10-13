package com.example.mymusic.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.mymusic.service_music.PlayerService;

public class MyBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        Log.wtf("check action Broadcast", action);

        Intent intentService = new Intent(context, PlayerService.class);
        intentService.setAction(action);
        context.startService(intentService);
    }
}
