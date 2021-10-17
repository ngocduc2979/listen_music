package com.example.mymusic.service_music;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.commit451.nativestackblur.NativeStackBlur;
import com.example.mymusic.savedata.AppConfig;
import com.example.mymusic.savedata.DataPlayer;
import com.example.mymusic.broadcast.MyBroadCastReceiver;
import com.example.mymusic.activity.PlayerActivity;
import com.example.mymusic.datamodel.Song;
import com.example.mymusic.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.mymusic.notification.NotificationChannelClass.CHANNEL_ID;

public class PlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {
    private final String TAG = getClass().getSimpleName();

    public static final String EXTRA_CURRENT_PROGRESS = "current_progress";
    public static final String EXTRA_DURATION = "duration";
    public static final String EXTRA_STATE_PLAY = "state_play";
    public static final String EXTRA_PROGRESS = "progress";
    public static final String EXTRA_SONG = "song";
    public static final String EXTRA_ISPLAYING = "isplaying";

    public static final String ACTION_NEW_PLAY = "com.example.mymusic.NEW_PLAY";
    public static final String ACTION_PLAY_PAUSE_MUSIC = "com.example.mymusic.PLAY, Pause";
    public static final String ACTION_PAUSE_MUSIC = "com.example.mymusic.PAUSE";
    public static final String ACTION_SEEK = "com.example.mymusic.SEEK";
    public static final String ACTION_NEXT_SONG = "com.example.mymusic.NEXT";
    public static final String ACTION_PREVIOUS_SONG = "com.example.mymusic.PREVIOUS";
    public static final String ACTION_CLOSE_PLAYER = "com.example.mymusic.CLOSE";

    public static final String ACTION_UPDATE_SONG_INFO = "com.example.mymusic.UPDATE_SONG_INFO";
    public static final String ACTION_UPDATE_STATE_PLAY = "com.example.mymusic.UPDATE_STATE_PLAY";
    public static final String ACTION_UPDATE_PROGRESS_SONG = "com.example.mymusic.UPDATE_PROGRESS";

    private static final String EXTRA_PLAYLIST = "playlist";
    private static final String EXTRA_CUR_POSITION = "cur_position";

    private boolean isPlaying = false;
    private int durationCurSong = 0;
    private MediaPlayer mediaPlayer;
    private PendingIntent pendingIntent;
    private RemoteViews remoteViews;

    private List<Song> listShuffle = new ArrayList<>();
//    public static void launch(Context context, List<Song> playlist, int curPosition) {
//        Intent intent = new Intent(context, PlayerService.class);
//
//        intent.putExtra(EXTRA_CUR_POSITION, curPosition);
//        intent.putParcelableArrayListExtra(EXTRA_PLAYLIST, (ArrayList<Song>) playlist);
//
//        context.startService(intent);
//    }

    private final Handler updateProgressHandler = new Handler();
    private final Runnable updateProgress = new Runnable() {
        @Override
        public void run() {
            int cur = mediaPlayer.getCurrentPosition();

            sendBroadcastUpdateProgress(cur, durationCurSong);

            updateProgressHandler.postDelayed(this, 1000L);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        initPlayer();

        Log.wtf(TAG, "On create");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        String action = intent.getAction();

        Log.wtf("onStartCommand", action);

        if (action.equals(ACTION_NEW_PLAY)) {
            newPlay();
        } else if (action.equals(ACTION_PLAY_PAUSE_MUSIC)) {
            play();
        } else if (action.equals(ACTION_SEEK)) {
            int progress = intent.getIntExtra(EXTRA_PROGRESS, 0);
            seekTo(progress);
        } else if (action.equals(ACTION_NEXT_SONG)){
            next();
        } else if (action.equals(ACTION_PREVIOUS_SONG)){
            previous();
        } else if (action.equals(ACTION_CLOSE_PLAYER)){
            sendBroadcastClosePlayer();
            stopSelf();
        }
        return START_STICKY;
    }


    private void sendBroadcastNewPlay(){
        Intent intent = new Intent(ACTION_NEW_PLAY);
        sendBroadcast(intent);
    }

    private void sendBroadcastUpdateProgress(int cur, int duration) {
        Intent intent = new Intent(ACTION_UPDATE_PROGRESS_SONG);
        intent.putExtra(EXTRA_DURATION, duration);
        intent.putExtra(EXTRA_CURRENT_PROGRESS, cur);

        sendBroadcast(intent);
    }

    private void sendBroadcastUpdateStatePlay() {
        Intent intent = new Intent(ACTION_UPDATE_STATE_PLAY);
        intent.putExtra(EXTRA_STATE_PLAY, mediaPlayer.isPlaying());
        sendBroadcast(intent);
    }

    private void sendBroadcastClosePlayer() {
        Intent intent = new Intent(ACTION_CLOSE_PLAYER);
        sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onDestroy() {
        mediaPlayer.release();
        updateProgressHandler.removeCallbacks(updateProgress);

        super.onDestroy();
    }

    private void initPlayer() {
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();

        isPlaying = true;
        durationCurSong = mp.getDuration();
        updateProgressHandler.post(updateProgress);
        sendBroadcastUpdateStatePlay();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // goi khi het bai hat
        if (AppConfig.getInstance(this).getRepeat().equals("no repeat")){
            if (AppConfig.getInstance(this).isShuffle()){
                shuffeNext();
            } else {
                next();
            }
        } else if (AppConfig.getInstance(this).getRepeat().equals("repeat one")){
            repeatOne();
        } else if (AppConfig.getInstance(this).getRepeat().equals("repeat all")){

        }
        sendNotification();
    }


    private void play() {

        Log.wtf(TAG, "play");

        if (isPlaying) {
            if (mediaPlayer.isPlaying()) {
                pause();
            } else {
                mediaPlayer.start();
                AppConfig.getInstance(this).setIsPlaying(true);
            }

            sendNotification();
//            sendNotificationMediaStyle();
            sendBroadcastUpdateStatePlay();
        } else {
            newPlay();
        }
    }

    private void newPlay() {
        Song curSong = DataPlayer.getInstance().getCurrentSong();

        AppConfig.getInstance(this).setCurrentSongName(curSong);
        AppConfig.getInstance(this).setCurrentSongArtist(curSong);
        AppConfig.getInstance(this).setCurrentSongPath(curSong);

        AppConfig.getInstance(this).setIsPlaying(true);

        sendBroadcastNewPlay();
        sendBroadcastUpdateStatePlay();
        sendNotification();
        startNewSong(curSong);
    }

    private void requestUpdateUISongInfo() {
        Intent intent = new Intent(ACTION_UPDATE_SONG_INFO);
        sendBroadcast(intent);
    }

    private void startNewSong(Song song) {
        Log.wtf(TAG, "startNewSong " + song.getSongName() + " " + song.getUrlSong());

        try {
            isPlaying = false;
            mediaPlayer.reset();
            mediaPlayer.setDataSource(this, Uri.parse(song.getUrlSong()));
            mediaPlayer.prepareAsync();

            requestUpdateUISongInfo();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void repeatOne(){
        Song curSong = DataPlayer.getInstance().getCurrentSong();
        startNewSong(curSong);

        AppConfig.getInstance(this).setCurrentSongName(curSong);
        AppConfig.getInstance(this).setCurrentSongArtist(curSong);
        AppConfig.getInstance(this).setCurrentSongPath(curSong);
    }

    private void pause() {
        Log.wtf(TAG, "pause");
        mediaPlayer.pause();
        AppConfig.getInstance(this).setIsPlaying(false);
    }

    private void shuffeNext(){
        int shufflePosition = DataPlayer.getInstance().getShuffeNextPosition();

        AppConfig.getInstance(this).setCurPosition(shufflePosition);
        AppConfig.getInstance(this).setCurrentSongName(DataPlayer.getInstance().getPlaylist().get(shufflePosition));
        AppConfig.getInstance(this).setCurrentSongArtist(DataPlayer.getInstance().getPlaylist().get(shufflePosition));
        AppConfig.getInstance(this).setCurrentSongPath(DataPlayer.getInstance().getPlaylist().get(shufflePosition));

        sendNotification();
        requestUpdateUISongInfo();

        startNewSong(DataPlayer.getInstance().getPlaylist().get(shufflePosition));
    }

    private void next() {
        Log.wtf(TAG, "next");

        AppConfig.getInstance(this).setIsPlaying(true);

        if (AppConfig.getInstance(this).getRepeat().equals("no repeat")){
            if(AppConfig.getInstance(this).isShuffle()){
                shuffeNext();
            } else {
                int nextPosition = DataPlayer.getInstance().getNextPosition();

                AppConfig.getInstance(this).setCurPosition(nextPosition);
                AppConfig.getInstance(this).setPlaylist(DataPlayer.getInstance().getPlaylist());
                AppConfig.getInstance(this).setCurrentSongName(DataPlayer.getInstance().getPlaylist().get(nextPosition));
                AppConfig.getInstance(this).setCurrentSongArtist(DataPlayer.getInstance().getPlaylist().get(nextPosition));
                AppConfig.getInstance(this).setCurrentSongPath(DataPlayer.getInstance().getPlaylist().get(nextPosition));

                sendNotification();
                requestUpdateUISongInfo();
                startNewSong(DataPlayer.getInstance().getPlaylist().get(nextPosition));
            }
        } else if (AppConfig.getInstance(this).getRepeat().equals("repeat one")) {
            int curPosition = DataPlayer.getInstance().getPlayPosition();
            sendNotification();
            requestUpdateUISongInfo();
            startNewSong(DataPlayer.getInstance().getPlaylist().get(curPosition));
        }

    }

    private void previous() {
        AppConfig.getInstance(this).setIsPlaying(true);

        if (AppConfig.getInstance(this).getRepeat().equals("no repeat")){
            if(AppConfig.getInstance(this).isShuffle()){
                shuffeNext();
            } else {
                int previousPosition = DataPlayer.getInstance().getPreviewPosition();

                AppConfig.getInstance(this).setCurPosition(previousPosition);
                AppConfig.getInstance(this).setPlaylist(DataPlayer.getInstance().getPlaylist());
                AppConfig.getInstance(this).setCurrentSongName(DataPlayer.getInstance().getPlaylist().get(previousPosition));
                AppConfig.getInstance(this).setCurrentSongArtist(DataPlayer.getInstance().getPlaylist().get(previousPosition));
                AppConfig.getInstance(this).setCurrentSongPath(DataPlayer.getInstance().getPlaylist().get(previousPosition));

                sendNotification();
                requestUpdateUISongInfo();

                startNewSong(DataPlayer.getInstance().getPlaylist().get(previousPosition));
            }
        } else if (AppConfig.getInstance(this).getRepeat().equals("repeat one")) {
            int curPosition = DataPlayer.getInstance().getPlayPosition();
            sendNotification();
            requestUpdateUISongInfo();
            startNewSong(DataPlayer.getInstance().getPlaylist().get(curPosition));
        }

    }

    private void seekTo(int progress) {
        if (progress > 0) {
            int seekTo = (progress * durationCurSong) / 100;

            mediaPlayer.seekTo(seekTo);
        }
    }


    private void sendNotification(){

        Song song = DataPlayer.getInstance().getCurrentSong();

//        remoteViews = new RemoteViews(getPackageName(), R.layout.notification_custom);

//        setInfoNotification(song, remoteViews);
//        setClickNotificationRemoteView();

        //set custom view
//        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentIntent(pendingIntent)
//                .setCustomContentView(remoteViews)
//                .setSound(null)
//                .setSmallIcon(R.drawable.icon_music_24)
////                .setLargeIcon(bitmap)
////                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
//                .build();
//
//        startForeground(1, notification);



        byte[] albumart = getAlbumArt(DataPlayer.getInstance().getCurrentSong().getUrlSong());

        Bitmap bm;

        if (albumart != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(albumart, 0, albumart.length);
            Bitmap resizeBitmap = Bitmap.createScaledBitmap(bitmap, 250, 250, false);
            bm = NativeStackBlur.process(resizeBitmap, 0);
        } else {
            Drawable drawable = this.getResources().getDrawable(R.drawable.music_default_cover);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Bitmap resizeBitmap = Bitmap.createScaledBitmap(bitmap, 250, 250, false);
            bm = NativeStackBlur.process(resizeBitmap, 0);
        }

        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(this, "tag");

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
            if (AppConfig.getInstance(this).getIsPlaying()){
                //Media style view
                Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.icon_app)
                        .setContentTitle(song.getSongName())
                        .setContentText(song.getArtistName())
                        .setLargeIcon(bm)
                        .setContentIntent(setClickNotifiMediaStye())
                        .addAction(R.drawable.ic_baseline_skip_previous_24_black07, "privious", getPendingIntent(this, ACTION_PREVIOUS_SONG))
                        .addAction(R.drawable.ic_baseline_pause_24, "pause", getPendingIntent(this, ACTION_PLAY_PAUSE_MUSIC))
                        .addAction(R.drawable.ic_baseline_skip_next_24_black07, "next", getPendingIntent(this, ACTION_NEXT_SONG))
                        .addAction(R.drawable.ic_baseline_close_24_color_black, "close", getPendingIntent(this, ACTION_CLOSE_PLAYER))
                        .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                .setShowActionsInCompactView(0, 1, 2)
                                .setMediaSession(mediaSessionCompat.getSessionToken()))
                        .build();

                startForeground(1, notification);
            } else {
                //Media style view
                Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.icon_app)
                        .setContentTitle(song.getSongName())
                        .setContentText(song.getArtistName())
                        .setLargeIcon(bm)
                        .setContentIntent(setClickNotifiMediaStye())
                        .addAction(R.drawable.ic_baseline_skip_previous_24_black07, "privious", getPendingIntent(this, ACTION_PREVIOUS_SONG))
                        .addAction(R.drawable.ic_baseline_play_arrow_24, "pause", getPendingIntent(this, ACTION_PLAY_PAUSE_MUSIC))
                        .addAction(R.drawable.ic_baseline_skip_next_24_black07, "next", getPendingIntent(this, ACTION_NEXT_SONG))
                        .addAction(R.drawable.ic_baseline_close_24_color_black, "close", getPendingIntent(this, ACTION_CLOSE_PLAYER))
                        .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                .setShowActionsInCompactView(0, 1, 2)
                                .setMediaSession(mediaSessionCompat.getSessionToken()))
                        .build();

                startForeground(1, notification);
            }
        } else {
            if (AppConfig.getInstance(this).getIsPlaying()){
                //Media style view
                Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.icon_app)
                        .setContentTitle(song.getSongName())
                        .setContentText(song.getArtistName())
                        .setLargeIcon(bm)
                        .setContentIntent(setClickNotifiMediaStye())
                        .addAction(R.drawable.ic_baseline_skip_previous_24_black07, "privious", getPendingIntent(this, ACTION_PREVIOUS_SONG))
                        .addAction(R.drawable.ic_baseline_pause_24, "pause", getPendingIntent(this, ACTION_PLAY_PAUSE_MUSIC))
                        .addAction(R.drawable.ic_baseline_skip_next_24_black07, "next", getPendingIntent(this, ACTION_NEXT_SONG))
                        .addAction(R.drawable.ic_baseline_close_24_color_black, "close", getPendingIntent(this, ACTION_CLOSE_PLAYER))
                        .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                .setShowActionsInCompactView(0, 1, 2))
                        .build();

                startForeground(1, notification);
            } else {
                //Media style view
                Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.icon_app)
                        .setContentTitle(song.getSongName())
                        .setContentText(song.getArtistName())
                        .setLargeIcon(bm)
                        .setContentIntent(setClickNotifiMediaStye())
                        .addAction(R.drawable.ic_baseline_skip_previous_24_black07, "privious", getPendingIntent(this, ACTION_PREVIOUS_SONG))
                        .addAction(R.drawable.ic_baseline_play_arrow_24, "pause", getPendingIntent(this, ACTION_PLAY_PAUSE_MUSIC))
                        .addAction(R.drawable.ic_baseline_skip_next_24_black07, "next", getPendingIntent(this, ACTION_NEXT_SONG))
                        .addAction(R.drawable.ic_baseline_close_24_color_black, "close", getPendingIntent(this, ACTION_CLOSE_PLAYER))
                        .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                .setShowActionsInCompactView(0, 1, 2))
                        .build();

                startForeground(1, notification);
            }
        }


    }

    private void setInfoNotification(Song song, RemoteViews remoteViews){
        remoteViews.setTextViewText(R.id.notification_song_name, song.getSongName());
        remoteViews.setTextViewText(R.id.notifi_artist, song.getArtistName());

        long start = System.currentTimeMillis();

        byte[] albumart = getAlbumArt(DataPlayer.getInstance().getCurrentSong().getUrlSong());

        if (albumart != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(albumart, 0, albumart.length);

            Bitmap resizeBitmap = Bitmap.createScaledBitmap(bitmap, 250, 250, false);

            Bitmap bm = NativeStackBlur.process(resizeBitmap, 0);
            remoteViews.setImageViewBitmap(R.id.imv_background, bm);
        } else {
            Drawable drawable = this.getResources().getDrawable(R.drawable.music_default_cover);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

            Bitmap resizeBitmap = Bitmap.createScaledBitmap(bitmap, 250, 250, false);

            Bitmap bm = NativeStackBlur.process(resizeBitmap, 0);
            remoteViews.setImageViewBitmap(R.id.imv_background, bm);
        }

//        Log.wtf("checkTime", String.valueOf(bitmap.getWidth()) + " " + "width");
//        Log.wtf("checkTime", String.valueOf(bitmap.getHeight())  + " " + "height");
//        Log.wtf("checkTime", String.valueOf(System.currentTimeMillis() - start));
    }

    private PendingIntent setClickNotifiMediaStye() {
        Intent intent = new Intent(this, PlayerActivity.class);
        AppConfig.getInstance(this).setIsNewPlay(false);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntentWithParentStack(intent);
        return pendingIntent =  taskStackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void setClickNotificationRemoteView(){
        Intent intent = new Intent(this, PlayerActivity.class);

        AppConfig.getInstance(this).setIsNewPlay(false);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntentWithParentStack(intent);

        remoteViews.setImageViewResource(R.id.notifi_pause_play, R.drawable.pause);

        pendingIntent = taskStackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);

        //click pause/play
//        if (mediaPlayer.isPlaying()){
//            remoteViews.setOnClickPendingIntent(R.id.notifi_pause_play, getPendingIntent(this, ACTION_PLAY_PAUSE_MUSIC));
//            remoteViews.setImageViewResource(R.id.notifi_pause_play, R.drawable.pause);
//            Log.wtf("Service", "check play");
//        } else {
//            remoteViews.setOnClickPendingIntent(R.id.notifi_pause_play, getPendingIntent(this, ACTION_PLAY_PAUSE_MUSIC));
//            remoteViews.setImageViewResource(R.id.notifi_pause_play, R.drawable.ic_baseline_play_arrow_24);
//        }

        if (AppConfig.getInstance(this).getIsPlaying()){
            remoteViews.setOnClickPendingIntent(R.id.notifi_pause_play, getPendingIntent(this, ACTION_PLAY_PAUSE_MUSIC));
            remoteViews.setImageViewResource(R.id.notifi_pause_play, R.drawable.pause);
            Log.wtf("Service", "check play");
        } else {
            remoteViews.setOnClickPendingIntent(R.id.notifi_pause_play, getPendingIntent(this, ACTION_PLAY_PAUSE_MUSIC));
            remoteViews.setImageViewResource(R.id.notifi_pause_play, R.drawable.ic_baseline_play_arrow_24);
        }

        //click next song
        remoteViews.setOnClickPendingIntent(R.id.notifi_nextSong, getPendingIntent(this, ACTION_NEXT_SONG));
        //click preview song
        remoteViews.setOnClickPendingIntent(R.id.notifi_backSong, getPendingIntent(this, ACTION_PREVIOUS_SONG));
        //click close
        remoteViews.setOnClickPendingIntent(R.id.notifi_close, getPendingIntent(this, ACTION_CLOSE_PLAYER));
    }

    private PendingIntent getPendingIntent(Context context, String action){
        Intent intent = new Intent(this, MyBroadCastReceiver.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private byte[] getAlbumArt(String path){
        MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
        mediaMetadata.setDataSource(path);
        return mediaMetadata.getEmbeddedPicture();
    }
}
