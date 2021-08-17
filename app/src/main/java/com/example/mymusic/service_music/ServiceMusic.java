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
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mymusic.MyBroadCastReceiver;
import com.example.mymusic.ObjectSong;
import com.example.mymusic.R;
import com.example.mymusic.activity.ActivityPlayMusic;
import com.example.mymusic.activity.MainActivity;

import java.util.List;
import java.util.Random;

import static com.example.mymusic.NotificationChannelClass.CHANNEL_ID;
import static com.example.mymusic.fragment.FragmentSongs.listSongs;

public class ServiceMusic extends Service {

    public static final int ACTION_PAUSE = 1;
    public static final int ACTION_PLAY = 2;
    public static final int ACTION_CLOSE = 3;
    public static final int ACTION_NEXT = 4;
    public static final int ACTION_PREVIEW = 5;
    public static final int ACTION_START = 6;
    public static final int ACTION_REPEAT_ONE = 7;
    public static final int ACTION_REPEAT_ALL = 8;
    public static final int ACTION_SHUFFE = 9;

    private boolean checkPlaying = false;
    private boolean checkShuffe = false;
    private String checkRepeat = "no repeat";

    static List<ObjectSong> listSongService;
    private int position;
    private MediaPlayer mediaPlayer;
    private Uri uri;



    @Override
    public void onCreate() {
        super.onCreate();
        Log.wtf("Service", "On create");
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
        Log.wtf("Service", String.valueOf(position));

        if (mediaPlayer != null){
            stopMedia();
            createMedia();
            startMedia();
            sendNotification();
            setAutoNext();
        } else {
            createMedia();
            startMedia();
            sendNotification();
            setAutoNext();
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
                break;
            case ACTION_PLAY:
                playMusic();
                break;
            case ACTION_CLOSE:
                stopSelf();
                sendActionToFragmentSong(ACTION_CLOSE);
                sendActionToActivityPlayMusic(ACTION_CLOSE);
                break;
            case ACTION_NEXT:
                nextMusic();
                break;
            case ACTION_PREVIEW:
                previewMusic();
                break;
            case ACTION_REPEAT_ONE:
                break;
            case ACTION_REPEAT_ALL:
                break;
            case ACTION_SHUFFE:
                break;
        }
    }

    private void pauseMusic(){
        Toast.makeText(this, "Click pause", Toast.LENGTH_SHORT).show();
        if (mediaPlayer != null && checkPlaying){
            Log.wtf("Service", "pauseMusic");
            mediaPlayer.pause();
            checkPlaying = false;
            sendNotification();
            sendActionToFragmentSong(ACTION_PAUSE);
            sendActionToActivityPlayMusic(ACTION_PAUSE);
        }
    }

    private void playMusic(){
        Toast.makeText(this, "Click play", Toast.LENGTH_SHORT).show();
        if (mediaPlayer != null && !checkPlaying){
            Log.wtf("Service", "playMusic");
            mediaPlayer.start();
            checkPlaying = true;
//            sendNotificationMediaStyle();
            sendActionToFragmentSong(ACTION_PLAY);
            sendActionToActivityPlayMusic(ACTION_PLAY);
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
//            sendNotificationMediaStyle();
            sendActionToFragmentSong(ACTION_NEXT);
            sendActionToActivityPlayMusic(ACTION_NEXT);
        }
        else {
            position = ((position + 1) % listSongService.size());
            createMedia();
//            setData();
//            sendNotificationMediaStyle();
            sendActionToFragmentSong(ACTION_NEXT);
            sendActionToActivityPlayMusic(ACTION_NEXT);
        }
    }

    private void previewMusic(){
        if (mediaPlayer.isPlaying()) {
            stopMedia();
            position = (((position - 1) + listSongService.size()) % listSongService.size());
            createMedia();
//            setData();
            startMedia();
//            sendNotificationMediaStyle();
            sendActionToFragmentSong(ACTION_PREVIEW);
            sendActionToActivityPlayMusic(ACTION_PREVIEW);
        }
        else {
            position = (((position - 1) + listSongService.size()) % listSongService.size());
            createMedia();
//            setData();
//            sendNotificationMediaStyle();
            sendActionToFragmentSong(ACTION_PREVIEW);
            sendActionToActivityPlayMusic(ACTION_PREVIEW);
        }
    }

    private void setAutoNext(){
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                autoNext();
            }
        });
    }

    private void autoNext(){
        if (!checkShuffe){
            position = ((position + 1) % listSongService.size());
        } else {
            Random random = new Random();
            position = random.nextInt(listSongService.size());
        }
        createMedia();
//        seekBar.setProgress(0);
//        setData();
        startMedia();
    }


    private void sendNotification(){
        Log.wtf("Service", "send notification");
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

//        Uri uri = Uri.parse(listSongService.get(position).getAlbum());

        byte[] albumArt = getAlbumArt(listSongs.get(position).getUrlSong());
        Bitmap bitmap = BitmapFactory.decodeByteArray(albumArt, 0, albumArt.length);


        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_custom);
        remoteViews.setTextViewText(R.id.notification_song_name, listSongService.get(position).getSongName());
        remoteViews.setTextViewText(R.id.notifi_artist, listSongService.get(position).getArtistName());
//        remoteViews.setImageViewResource(R.id.notifi_relaytive_layout, Integer.parseInt(listSongService.get(position).getAlbum()));

        remoteViews.setImageViewResource(R.id.notifi_pause_play, R.drawable.ic_baseline_pause_24_brown);

        //click pause/play
        if (checkPlaying){
            remoteViews.setOnClickPendingIntent(R.id.notifi_pause_play, getPendingIntent(this, ACTION_PAUSE));
            remoteViews.setImageViewResource(R.id.notifi_pause_play, R.drawable.ic_baseline_pause_24_brown);
            Log.wtf("Service", "check play");
        } else {
            remoteViews.setOnClickPendingIntent(R.id.notifi_pause_play, getPendingIntent(this, ACTION_PLAY));
            remoteViews.setImageViewResource(R.id.notifi_pause_play, R.drawable.ic_baseline_play_arrow_24_brown);
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
                .setLargeIcon(bitmap)
                .build();



        startForeground(1, notification);
    }

//    private void sendNotificationMediaStyle(){
//
//        byte[] albumArt = getAlbumArt(listSongs.get(position).getUrlSong());
//        Bitmap bitmap = BitmapFactory.decodeByteArray(albumArt, 0, albumArt.length);
//
////        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background_default_song);
//
//        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(this, "sendNotification");
//
//        NotificationCompat.Builder notificationBuider = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setSmallIcon(R.drawable.icon_music_24)
//                .setSubText("Play Music")
//                .setContentTitle(listSongService.get(position).getSongName())
//                .setContentText(listSongService.get(position).getArtistName())
//                .setLargeIcon(bitmap);
//
//        if(checkPlaying){
//            notificationBuider.addAction(R.drawable.ic_baseline_skip_previous_24_brown, "back music", getPendingIntent(this, ACTION_PREVIEW))
//                                .addAction(R.drawable.ic_baseline_play_arrow_24_brown, "play", getPendingIntent(this, ACTION_PAUSE))
//                                .addAction(R.drawable.ic_baseline_skip_next_24_brown, "next", getPendingIntent(this, ACTION_NEXT))
//                                .addAction(R.drawable.ic_baseline_close_24_brown, "close", getPendingIntent(this, ACTION_CLOSE))
//                                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
//                                .setShowActionsInCompactView(0, 1, 2 /* #1: pause button */)
//                                .setMediaSession(mediaSessionCompat.getSessionToken()));
//        } else {
//            notificationBuider.addAction(R.drawable.ic_baseline_skip_previous_24_brown, "back music", getPendingIntent(this, ACTION_PREVIEW))
//                                .addAction(R.drawable.ic_baseline_pause_24_brown, "play", getPendingIntent(this, ACTION_PLAY))
//                                .addAction(R.drawable.ic_baseline_skip_next_24_brown, "next", getPendingIntent(this, ACTION_NEXT))
//                                .addAction(R.drawable.ic_baseline_close_24_brown, "close", getPendingIntent(this, ACTION_CLOSE))
//                                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
//                                .setShowActionsInCompactView(0, 1, 2 /* #1: pause button */)
//                                .setMediaSession(mediaSessionCompat.getSessionToken()));
//        }
//
//        startForeground(1, notificationBuider.build());
//    }

    private PendingIntent getPendingIntent(Context context, int action){
        Intent intent = new Intent(this, MyBroadCastReceiver.class);
        intent.putExtra("action_music", action);
        return PendingIntent.getBroadcast(context, action, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void createMedia(){
        uri = Uri.parse(listSongService.get(position).getUrlSong());
        mediaPlayer = MediaPlayer.create(this, uri);
        Log.wtf("Service", "create Media");
    }

    private void startMedia(){
        mediaPlayer.start();
        checkPlaying = true;
        Log.wtf("Service", "startMedia");
        sendActionToFragmentSong(ACTION_START);
    }

    private void stopMedia(){
        mediaPlayer.stop();
        mediaPlayer.release();
        Log.wtf("Service", "stop media");
    }

    private void sendActionToFragmentSong(int action){
        Intent intent = new Intent("send_data_to_fragmentSong");
        intent.putExtra("position", position);
        intent.putExtra("action", action);
        intent.putExtra("check_play", checkPlaying);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendActionToActivityPlayMusic(int action){
        Intent intent = new Intent("send_data_to_activity_play_music");
        intent.putExtra("position", position);
        intent.putExtra("action", action);
        intent.putExtra("check_play", checkPlaying);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private byte[] getAlbumArt(String path){
        MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
        mediaMetadata.setDataSource(path);
        return mediaMetadata.getEmbeddedPicture();
    }
}