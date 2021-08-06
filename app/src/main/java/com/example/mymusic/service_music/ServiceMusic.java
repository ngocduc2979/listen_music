package com.example.mymusic.service_music;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.mymusic.MyBroadCastReceiver;
import com.example.mymusic.ObjectSong;
import com.example.mymusic.R;
import com.example.mymusic.activity.ActivityPlayMusic;
import com.example.mymusic.activity.MainActivity;

import java.util.List;

import static com.example.mymusic.NotificationChannelClass.CHANNEL_ID;
import static com.example.mymusic.fragment.FragmentSongs.listSongs;

public class ServiceMusic extends Service {

    private static final int ACTION_PAUSE = 1;
    private static final int ACTION_PLAY = 2;
    private static final int ACTION_CLOSE = 3;
    private static final int ACTION_NEXT = 4;
    private static final int ACTION_PREVIEW = 5;

    private boolean checkPlaying = false;

    static List<ObjectSong> listSongService;
    private int position;
    private MediaPlayer mediaPlayer;
    private Uri uri;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        listSongService = listSongs;

        position = intent.getIntExtra("position", 0);

        if (mediaPlayer != null){
            stopMedia();
            createMedia();
            startMedia();
            sendNotification();
        } else {
            createMedia();
            startMedia();
            sendNotification();
        }

        int actionMusic = intent.getIntExtra("action_service", 0);
        getActionMusic(actionMusic);


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMedia();
    }

    private void getActionMusic(int action){
        switch (action){
            case ACTION_PAUSE:
                pauseMusic();
//                sendNotification();
                break;
            case ACTION_PLAY:
                playMusic();
//                sendNotification();
                break;
            case ACTION_CLOSE:
                stopSelf();
                break;
            case ACTION_NEXT:
                nextMusic();
                break;
            case ACTION_PREVIEW:
                previewMusic();
                break;
        }
    }

    private void pauseMusic(){
        Toast.makeText(this, "Click pause", Toast.LENGTH_SHORT).show();
        if (mediaPlayer != null && checkPlaying){
            Log.wtf("Service", "pauseMusic");
            mediaPlayer.pause();
            checkPlaying = false;
        }
    }

    private void playMusic(){
        Toast.makeText(this, "Click play", Toast.LENGTH_SHORT).show();
        if (mediaPlayer != null && !checkPlaying){
            Log.wtf("Service", "playMusic");
            mediaPlayer.start();
            checkPlaying = true;
        }
    }

    private void nextMusic(){
        Toast.makeText(this, "Click next", Toast.LENGTH_SHORT).show();
        if (mediaPlayer.isPlaying()){
            stopMedia();
            position = ((position + 1) % listSongService.size());
            createMedia();
//            setData();
            startMedia();
        }
        else {
            position = ((position + 1) % listSongService.size());
            createMedia();
//            setData();
        }
    }

    private void previewMusic(){
        if (mediaPlayer.isPlaying()) {
            stopMedia();
            position = (((position - 1) + listSongService.size()) % listSongService.size());
            createMedia();
//            setData();
            startMedia();
        }
        else {
            position = (((position - 1) + listSongService.size()) % listSongService.size());
            createMedia();
//            setData();
        }
    }


    private void sendNotification(){
        Log.wtf("Service", "send notification");
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

//        Uri uri = Uri.parse(listSongService.get(position).getAlbum());
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), Integer.parseInt(listSongService.get(position).getUrlSong()));

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_custom);
        remoteViews.setTextViewText(R.id.notification_song_name, listSongService.get(position).getSongName());
        remoteViews.setTextViewText(R.id.notifi_artist, listSongService.get(position).getArtistName());
//        remoteViews.setImageViewResource(R.id.notifi_relaytive_layout, Integer.parseInt(listSongService.get(position).getAlbum()));

        remoteViews.setImageViewResource(R.id.notifi_pause_play, R.drawable.ic_baseline_pause_24_brown);
//        remoteViews.setImageViewResource(R.id.notifi_pause_play, R.drawable.ic_baseline_play_arrow_24_brown);

        //click pause/play
        if (checkPlaying){
            remoteViews.setOnClickPendingIntent(R.id.notifi_pause_play, getPendingIntent(this, ACTION_PAUSE));
            remoteViews.setImageViewResource(R.id.notifi_pause_play, R.drawable.ic_baseline_play_arrow_24_brown);
            Log.wtf("Service", "check play");
        } else {
            remoteViews.setOnClickPendingIntent(R.id.notifi_pause_play, getPendingIntent(this, ACTION_PLAY));
            remoteViews.setImageViewResource(R.id.notifi_pause_play, R.drawable.ic_baseline_pause_24_brown);
        }

        //click next song
        remoteViews.setOnClickPendingIntent(R.id.notifi_nextSong, getPendingIntent(this, ACTION_NEXT));

        //click preview song
        remoteViews.setOnClickPendingIntent(R.id.notifi_backSong, getPendingIntent(this, ACTION_PREVIEW));

        //click close
        remoteViews.setOnClickPendingIntent(R.id.notifi_close, getPendingIntent(this, ACTION_CLOSE));

        //set custom view
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setCustomContentView(remoteViews)
                .setSound(null)
                .setSmallIcon(R.drawable.icon_music_24)
                .build();

        startForeground(1, notification);
    }

    private PendingIntent getPendingIntent(Context context, int action){
        Intent intent = new Intent(this, MyBroadCastReceiver.class);
        intent.putExtra("action_music", action);
        return PendingIntent.getBroadcast(context, action, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void createMedia(){
        uri = Uri.parse(listSongService.get(position).getUrlSong());
        mediaPlayer = MediaPlayer.create(this, uri);
    }

    private void startMedia(){
        mediaPlayer.start();
        checkPlaying = true;
        Log.wtf("Service", "startMedia");
    }

    private void stopMedia(){
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}
