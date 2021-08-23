 package com.example.mymusic.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.example.mymusic.ObjectSong;
import com.example.mymusic.R;
import com.example.mymusic.service_music.ServiceMusic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.example.mymusic.fragment.FragmentSongs.listSongs;

public class ActivityPlayMusic extends AppCompatActivity {

    public static final String EXTRA_MUSIC = "MUSIC";
    private ObjectSong songs;
    private MediaPlayer mediaPlayer;
    private int position;
//    private static List<ObjectSong> listSong;
    private ArrayList<ObjectSong> listSong = new ArrayList<>();
    private Uri uri;
    private Thread updateSeekbar;
    private Handler handler = new Handler();

    private SeekBar seekBar;
    private ImageButton imbShuffe;
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

    private Boolean checkShuffe = false;
    private String checkRepeat = "no repeat";

    private boolean checkPlay;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            position = intent.getIntExtra("position", 0);
            checkPlay = intent.getBooleanExtra("check_play", true);
            int action = intent.getIntExtra("action_to_playMusic", 0);

            setLayoutPlayMusic(action);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_playmusic);

        Log.wtf("Play music", "on create");

        init();

        listSong = getIntent().getParcelableArrayListExtra("list_from_fragmentSong_to_playMusic");
//        listSong = listSongs;

        position = getIntent().getIntExtra("position", 0);

        uri = Uri.parse(listSong.get(position).getUrlSong());
        mediaPlayer = MediaPlayer.create(this, uri);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("send_data_to_activity_play_music"));

        setData();

        //click Shuffe button
        imbShuffe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseShuffe();
            }
        });

        //click repeat button
        imbRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickRepeat();
            }
        });

        //click play/pause
//        imbPlay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                clickPause_Play();
//            }
//        });

        //click Next Song
//        imbForward.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                clickNextSong();
//            }
//        });

        //click preview song
//        imbBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                clickPriviewSong();
//            }
//        });

        //back layout
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        Log.wtf("PlayMusic", "onResume");
        super.onResume();
    }

    //choose shuffe
    private void chooseShuffe(){
        if (checkShuffe == false){
            imbShuffe.setBackgroundResource(R.drawable.ic_baseline_shuffle_24);
            checkShuffe = true;
            Log.wtf("clickShuffe", "shuffe = true");
        }
        else {
            imbShuffe.setBackgroundResource(R.drawable.ic_baseline_shuffle_24_black);
            checkShuffe = false;
            Log.wtf("clickShuffe", "shuffe = false");
        }
    }

    //Click repeat
    private void clickRepeat(){
        if (checkRepeat.equals("no repeat")){
            imbRepeat.setBackgroundResource(R.drawable.ic_baseline_repeat_one_24);
            checkRepeat = "repeat one";
        } else if (checkRepeat.equals("repeat one")){
            imbRepeat.setBackgroundResource(R.drawable.ic_baseline_repeat_24_purple);
            checkRepeat = "repeat all";
        } else if (checkRepeat.equals("repeat all")){
            imbRepeat.setBackgroundResource(R.drawable.ic_baseline_repeat_24_black);
            checkRepeat = "no repeat";
        }
    }

    //Auto next song
    private void autoNext(){
        if (!checkShuffe){
            position = ((position + 1) % listSong.size());
        } else {
            Random random = new Random();
            position = random.nextInt(listSong.size());
        }
        createMedia();
        seekBar.setProgress(0);
        setData();
        startMedia();
    }


    //next song
    private void clickNextSong(){
        if (mediaPlayer.isPlaying()){
            stopMedia();
            position = ((position + 1) % listSong.size());
            createMedia();
            seekBar.setProgress(0);
            setData();
            startMedia();
        }
        else {
            position = ((position + 1) % listSong.size());
            createMedia();
            seekBar.setProgress(0);
            setData();
        }
    }

    //Preview song
    private void clickPriviewSong(){
        if (mediaPlayer.isPlaying()) {
            stopMedia();
            position = (((position - 1) + listSong.size()) % listSong.size());
            createMedia();
            seekBar.setProgress(0);
            setData();
            startMedia();
        }
        else {
            position = (((position - 1) + listSong.size()) % listSong.size());
            createMedia();
            seekBar.setProgress(0);
            setData();
        }
    }

    // Pause/Pay
    private void clickPause_Play(){
        if (mediaPlayer.isPlaying()){
            imbPlay.setBackgroundResource(R.drawable.play);
            mediaPlayer.pause();
        }
        else {
            imbPlay.setBackgroundResource(R.drawable.pause);
            mediaPlayer.start();
        }
    }

    //set data
    private void setData(){

        Log.wtf("setDataMusic", "setData");
        tvSongName.setText(listSong.get(position).getSongName());
        tvSongName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tvSongName.setSelected(true);

        tvSinggerName.setText(listSong.get(position).getArtistName());
        tvSinggerName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tvSinggerName.setSelected(true);

        //set Album Image
        MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
        mediaMetadata.setDataSource(listSong.get(position).getUrlSong());
        byte[] art = mediaMetadata.getEmbeddedPicture();

        Glide.with(this)
                .load(art)
                .centerCrop()
                .placeholder(R.drawable.background_default_song)
                .into(imvImage);

        setSeekbar();

        //next song when current song end
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                autoNext();
                Log.wtf("setOnCompletionListener", "next Song");
            }
        });

    }

    private void startMedia(){
        mediaPlayer.start();
        Log.wtf("PlayMusicStart", "start");
    }

    private void setSeekbar(){
        //Set max duration and max seekbar = max duration
        SimpleDateFormat formatTime = new SimpleDateFormat("mm:ss");
        tvDuration.setText(formatTime.format(mediaPlayer.getDuration()));
        seekBar.setMax(mediaPlayer.getDuration());

        //select time seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        //current time seekbar
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()){
                    SimpleDateFormat current = new SimpleDateFormat("mm:ss");
                    tvCurrent.setText(current.format(mediaPlayer.getCurrentPosition()));
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    handler.postDelayed(this, 500);
                }
            }
        }, 100);

    }


    private void createMedia(){
        uri = Uri.parse(listSong.get(position).getUrlSong());
        mediaPlayer = MediaPlayer.create(this, uri);
        Log.wtf("createMedia", "createMedia");
    }

    private void stopMedia(){
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    private void init(){
        seekBar         = findViewById(R.id.seekbar);
        imbBack         = findViewById(R.id.back);
        imbForward      = findViewById(R.id.forward);
        imbPlay         = findViewById(R.id.play);
        imbShuffe       = findViewById(R.id.shuffe);
        imbRepeat       = findViewById(R.id.repeat);
        imvImage        = findViewById(R.id.image);
        tvSongName      = findViewById(R.id.song_name);
        tvSinggerName   = findViewById(R.id.author_name);
        toolbar         = findViewById(R.id.toolbar);
        tvDuration      = findViewById(R.id.duration);
        tvCurrent       = findViewById(R.id.currentPlay);
    }

    private void setLayoutPlayMusic(int action){
        switch (action){
            case ServiceMusic.ACTION_START:
                setInfoSong();
                checkPlaying();
                break;
            case ServiceMusic.ACTION_PLAY:
                checkPlaying();
                break;
            case ServiceMusic.ACTION_PAUSE:
                checkPlaying();
                break;
            case ServiceMusic.ACTION_NEXT:
                break;
            case ServiceMusic.ACTION_PREVIEW:
                break;
            case ServiceMusic.ACTION_CLOSE:
                break;
        }
    }

    private void setInfoSong(){
        tvSongName.setText(listSong.get(position).getSongName());
        tvSongName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tvSongName.setSelected(true);

        tvSinggerName.setText(listSong.get(position).getArtistName());
        tvSinggerName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tvSinggerName.setSelected(true);

        //set Album Image
        MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
        mediaMetadata.setDataSource(listSong.get(position).getUrlSong());
        byte[] art = mediaMetadata.getEmbeddedPicture();

        Glide.with(this)
                .load(art)
                .centerCrop()
                .placeholder(R.drawable.background_default_song)
                .into(imvImage);

        tvDuration.setText(listSong.get(position).getDuration());

        imbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendActionToService(ServiceMusic.ACTION_PREVIEW);
            }
        });

        imbForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendActionToService(ServiceMusic.ACTION_NEXT);
            }
        });

        if (checkPlay){
            imbPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendActionToService(ServiceMusic.ACTION_PAUSE);
                    Log.wtf("PlayMusic", "click play");
                }
            });
        } else {
            imbPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendActionToService(ServiceMusic.ACTION_PLAY);
                    Log.wtf("PlayMusic", "click pause");
                }
            });
        }
    }

    private void checkPlaying(){
        if(checkPlay){
            imbPlay.setBackgroundResource(R.drawable.ic_baseline_pause_24);
        } else {
            imbPlay.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
        }
    }

    private void sendActionToService(int action){
        Intent intent = new Intent(this, ServiceMusic.class);
        intent.putExtra("action_service_from_playMusic", action);
        startService(intent);
    }
}
