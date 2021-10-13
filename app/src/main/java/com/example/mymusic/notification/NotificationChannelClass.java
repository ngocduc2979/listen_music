package com.example.mymusic.notification;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

public class NotificationChannelClass extends Application {

    public static final String CHANNEL_ID = "Notification channel id";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.wtf("Notification", "create channel");
        createChannel();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "channel service", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setSound(null, null);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            if (notificationManager != null){
                notificationManager.createNotificationChannel(notificationChannel);
                Log.wtf("Notification", "# null");
            }
        }
    }
}
