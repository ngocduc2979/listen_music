package com.example.mymusic;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Random;

import static com.example.mymusic.SongsFragment.listSongs;

public class PlayMusic extends AppCompatActivity {

    public static final String EXTRA_MUSIC = "MUSIC";
    private Songs songs;
    private MediaPlayer mediaPlayer;
    private int position;
    private static List<Songs> listSong;
    private Uri uri;
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

    private Thread updateSeekbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_playmusic);
        init();
        listSong = listSongs;
        position = getIntent().getIntExtra("position", 0);
        if (mediaPlayer != null){
            stopMedia();
            createMedia();
            setData();
            startMedia();
        }
        else {
            createMedia();
            setData();
            startMedia();
        }

        //next song when current song end
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                autoNext();
            }
        });

        //click Shuffe button
        imbShuffe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseShuffe();
            }
        });

        //click play/pause
        imbPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPause_Play();
            }
        });

        //click Next Song
        imbForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickNextSong();
            }
        });

        //click preview song
        imbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPriviewSong();
            }
        });

        //back layout
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    //choose shuffe
    private void chooseShuffe(){
        if (checkShuffe == false){
            Random random = new Random();
            position = random.nextInt(listSong.size());
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    position = ((position) % listSong.size());
                    uri = Uri.parse(listSong.get(position).getUrlSong());
                    createMedia();
                    seekBar.setProgress(0);
                    setData();
                    startMedia();
                }
            });
            imbShuffe.setBackgroundResource(R.drawable.ic_baseline_shuffle_24);
            checkShuffe = true;
        }
        else {
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    autoNext();
                }
            });
            imbShuffe.setBackgroundResource(R.drawable.ic_baseline_shuffle_24_black);
            checkShuffe = false;
        }
    }

    //Auto next song
    private void autoNext(){
        position = ((position + 1) % listSong.size());
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
        tvSongName.setText(listSong.get(position).getSongName());
        tvSongName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tvSongName.setSelected(true);

        tvSinggerName.setText(listSong.get(position).getArtistName());
        tvSinggerName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tvSinggerName.setSelected(true);

        tvDuration.setText(getDuration(Integer.parseInt(listSong.get(position).getDuration()) / 1000));
        seekBar.setMax(mediaPlayer.getDuration() / 1000);

        //select time seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mediaPlayer != null){
                    mediaPlayer.seekTo(i * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //current time seekbar
//        PlayMusic.this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (mediaPlayer != null){
//                    int current = mediaPlayer.getCurrentPosition() / 1000;
//                    seekBar.setProgress(current);
//                    tvCurrent.setText(formatTime(current));
//                }
//                handler.postDelayed(this, 1000);
//            }
//        });

        //set Album Image
        MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
        mediaMetadata.setDataSource(listSong.get(position).getUrlSong());
        byte[] art = mediaMetadata.getEmbeddedPicture();
        Glide.with(this)
                .load(art)
                .centerCrop()
                .placeholder(R.drawable.thumb_player)
                .into(imvImage);
    }

    private void startMedia(){
        mediaPlayer.start();
        Log.wtf("PlayMusic", "start");
    }

    private String getDuration(int duration){
        String setDuration = "";
        String second   = String.valueOf(duration % 60);
        String minutes  = String.valueOf(duration / 60);
        return setDuration        = minutes + ":" + second;
    }

    private String formatTime(int current){
        String totalout = "";
        String totalNew = "";
        String second   = String.valueOf(current % 60);
        String minutes  = String.valueOf(current / 60);
        totalout        = minutes + ":" + second;
        totalNew        = minutes + ":" + "0" + second;

        if (second.length() == 1){
            return totalNew;
        }
        return totalout;
    }

    private void createMedia(){
        uri = Uri.parse(listSong.get(position).getUrlSong());
        mediaPlayer = MediaPlayer.create(this, uri);
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
}
