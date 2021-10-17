package com.example.mymusic.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.mymusic.savedata.AppConfig;
import com.example.mymusic.savedata.DataPlayer;
import com.example.mymusic.R;
import com.example.mymusic.datamodel.Song;
import com.example.mymusic.service_music.PlayerService;

import java.util.ArrayList;
import java.util.List;

import eightbitlab.com.blurview.BlurView;
import wseemann.media.FFmpegMediaMetadataRetriever;

public class PlayerActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private static final String EXTRA_PLAYLIST = "playlist";
    private static final String EXTRA_CUR_POSITION = "cur_position";
    private static final String KEY_NO_REPEAT = "no repeat";
    private static final String KEY_REPEAT_ONE = "repeat one";
    private static final String KEY_REPEAT_ALL = "repeat all";

    public static void launch(Context context, List<Song> playlist, int curPosition) {
        Intent intent = new Intent(context, PlayerActivity.class);

        intent.putExtra(EXTRA_CUR_POSITION, curPosition);
        intent.putParcelableArrayListExtra(EXTRA_PLAYLIST, (ArrayList<Song>) playlist);

        context.startActivity(intent);
    }

    private SeekBar seekBar;
    private ImageButton imbShuffle;
    private ImageButton imbBack;
    private ImageButton imbPlay;
    private ImageButton imbForward;
    private ImageButton imbRepeat;
    private ImageView imvImage;
    private Toolbar toolbar;
    private TextView tvSongName;
    private TextView tvSinggerName;
    private TextView tvDuration;
    private TextView tvCurrent;
    private BlurView blurView;
    private ConstraintLayout playerLayout;
    private ImageView viewBackground;

    private List<Song> playlist;

    private int curPosition;
    private boolean checkShuffle = false;
    private String checkRepeat = "no repeat";

    private boolean isPlaying;


    private BroadcastReceiver playerBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.wtf("PlayerActivity_Broadcast", action);

            if (action.equals(PlayerService.ACTION_UPDATE_PROGRESS_SONG)) {
                int cur = intent.getIntExtra(PlayerService.EXTRA_CURRENT_PROGRESS, 0);
                int duration = intent.getIntExtra(PlayerService.EXTRA_DURATION, 0);

                if (duration > 0) {
                    seekBar.setProgress((cur * 100) / duration);

                    tvCurrent.setText(getTimeLabel(cur));
                    tvDuration.setText(getTimeLabel(duration));
                }
            } else if (action.equals(PlayerService.ACTION_UPDATE_STATE_PLAY)) {
                isPlaying = intent.getBooleanExtra(PlayerService.EXTRA_STATE_PLAY, false);
                Log.wtf("check player", String.valueOf(isPlaying));
//                imbPlay.setImageResource(isPlaying ? R.drawable.pause : R.drawable.play);

                if (isPlaying){
                    imbPlay.setBackgroundResource(R.drawable.ic_baseline_pause_24_white);
                } else {
                    imbPlay.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24_white);
                }
                AppConfig.getInstance(context).setStatePlay(isPlaying);

            } else if (action.equals(PlayerService.ACTION_UPDATE_SONG_INFO)) {
                showSongInfo(DataPlayer.getInstance().getCurrentSong());
            }
        }
    };

    private String getTimeLabel(int milliseconds) {
        int minute = (milliseconds / 1000) / 60;
        int second = (milliseconds / 1000) % 60;

        if (minute < 10) {
            if (second < 10) {
                return "0" + minute + ":" + "0" + second;
            } else {
                return "0" + minute + ":" + second;
            }
        } else {
            if (second >= 10) {
                return minute + ":" + second;
            } else {
                return minute + ":" + "0" + second;
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_playmusic);

        init();

        setShuffleIcon();
        setRepeatIcon();
        play();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateStatePlay();
        updateCurrentSong();
    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayerService.ACTION_UPDATE_PROGRESS_SONG);
        intentFilter.addAction(PlayerService.ACTION_UPDATE_STATE_PLAY);
        intentFilter.addAction(PlayerService.ACTION_UPDATE_SONG_INFO);

        registerReceiver(playerBroadcast, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(playerBroadcast);
    }

    private void updateStatePlay(){
        isPlaying = AppConfig.getInstance(this).getStatePlay();
        if (isPlaying){
            imbPlay.setBackgroundResource(R.drawable.ic_baseline_pause_24_white);
        } else {
            imbPlay.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24_white);
        }
    }

    private void updateCurrentSong(){
        String songName = AppConfig.getInstance(this).getSongName();
        String artistName = AppConfig.getInstance(this).getSongArtist();
        String path = AppConfig.getInstance(this).getSongPath();

        tvSongName.setText(songName);
        tvSinggerName.setText(artistName);

        if (path != null){
            Glide.with(this)
                    .load(getAlbumArt(path))
                    .centerCrop()
                    .placeholder(R.drawable.music_default_cover)
                    .into(imvImage);
        }
    }

    private void setShuffleIcon(){
        if (AppConfig.getInstance(this).isShuffle()){
            imbShuffle.setBackgroundResource(R.drawable.ic_baseline_shuffle_24);
            checkShuffle = true;
        } else {
            imbShuffle.setBackgroundResource(R.drawable.ic_baseline_shuffle_24_white);
            checkShuffle = false;
        }
    }

    private void setRepeatIcon(){
        if (AppConfig.getInstance(this).getRepeat().equals(KEY_NO_REPEAT)){
            imbRepeat.setBackgroundResource(R.drawable.ic_baseline_repeat_24_all_white);
            checkRepeat = KEY_NO_REPEAT;
        } else if (AppConfig.getInstance(this).getRepeat().equals(KEY_REPEAT_ONE)){
            checkRepeat = KEY_REPEAT_ONE;
            imbRepeat.setBackgroundResource(R.drawable.ic_baseline_repeat_one_24);
        } else if (AppConfig.getInstance(this).getRepeat().equals(KEY_REPEAT_ALL)){
            checkRepeat = KEY_REPEAT_ALL;
            imbRepeat.setBackgroundResource(R.drawable.ic_baseline_repeat_24);
        }
    }

    private void init() {
        playlist = getIntent().getParcelableArrayListExtra(EXTRA_PLAYLIST);
        curPosition = getIntent().getIntExtra(EXTRA_CUR_POSITION, 0);

        seekBar         = findViewById(R.id.seekbar);
        imbBack         = findViewById(R.id.back);
        imbForward      = findViewById(R.id.forward);
        imbPlay         = findViewById(R.id.play);
        imbShuffle       = findViewById(R.id.shuffe);
        imbRepeat       = findViewById(R.id.repeat);
        imvImage        = findViewById(R.id.image);
        tvSongName      = findViewById(R.id.song_name);
        tvSinggerName   = findViewById(R.id.author_name);
        toolbar         = findViewById(R.id.toolbar);
        tvDuration      = findViewById(R.id.duration);
        tvCurrent       = findViewById(R.id.currentPlay);
        playerLayout    = findViewById(R.id.player_background);


        imbPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
                intent.setAction(PlayerService.ACTION_PLAY_PAUSE_MUSIC);
                startService(intent);
            }
        });

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previous();
            }
        });

        imbForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
        });

        imbShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuffle();
            }
        });

        imbRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeat();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    private void repeat(){
        if (checkRepeat.equals(KEY_NO_REPEAT)){
            checkRepeat = KEY_REPEAT_ONE;
            imbRepeat.setBackgroundResource(R.drawable.ic_baseline_repeat_one_24);
        } else if (checkRepeat.equals(KEY_REPEAT_ONE)){
            checkRepeat = KEY_REPEAT_ALL;
            imbRepeat.setBackgroundResource(R.drawable.ic_baseline_repeat_24_purple);
        } else if (checkRepeat.equals(KEY_REPEAT_ALL)){
            checkRepeat = KEY_NO_REPEAT;
            imbRepeat.setBackgroundResource(R.drawable.ic_baseline_repeat_24_all_white);
        }
        AppConfig.getInstance(this).setRepeat(checkRepeat);
    }

    private void shuffle(){
        if (!checkShuffle){
            checkShuffle = true;
            imbShuffle.setBackgroundResource(R.drawable.ic_baseline_shuffle_24);
            Log.wtf("checkrepeat", "no");
        } else {
            checkShuffle = false;
            imbShuffle.setBackgroundResource(R.drawable.ic_baseline_shuffle_24_white);
            Log.wtf("checkrepeat", "yes");
        }
        AppConfig.getInstance(this).setShuffle(checkShuffle);
    }

    private void showSongInfo(Song song) {
        tvSongName.setText(song.getSongName());
        tvSinggerName.setText(song.getArtistName());

        Glide.with(this)
                .load(getAlbumArt(song.getUrlSong()))
                .centerCrop()
                .placeholder(R.drawable.music_default_cover)
                .into(imvImage);

    }

    private void seekTo(int progress) {
        Intent intent = new Intent(this, PlayerService.class);
        intent.setAction(PlayerService.ACTION_SEEK);
        intent.putExtra(PlayerService.EXTRA_PROGRESS, progress);

        startService(intent);
    }

    private void play() {
//        DataPlayer dataPlayer = DataPlayer.getInstance();
//        dataPlayer.setPlaylist(playlist);
//        dataPlayer.setPlayPosition(curPosition);

        Log.wtf("Player", "play");

        boolean checkNewPlay = AppConfig.getInstance(this).getIsNewPlay();

        if (checkNewPlay){
            Intent intent = new Intent(this, PlayerService.class);
            intent.setAction(PlayerService.ACTION_NEW_PLAY);
            startService(intent);
        }
    }

    private void next(){
        Intent intent = new Intent(this, PlayerService.class);
        intent.setAction(PlayerService.ACTION_NEXT_SONG);
        startService(intent);
    }

    private void previous(){
        Intent intent = new Intent(this, PlayerService.class);
        intent.setAction(PlayerService.ACTION_PREVIOUS_SONG);
        startService(intent);
    }

    //get Image
    private byte[] getAlbumArt(String path) {
        FFmpegMediaMetadataRetriever mediaMetadata = new FFmpegMediaMetadataRetriever ();

        try {
            mediaMetadata.setDataSource(path);
        } catch (Exception e) {
        }

        return mediaMetadata.getEmbeddedPicture();
    }
}
