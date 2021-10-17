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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mymusic.savedata.AppConfig;
import com.example.mymusic.savedata.DataPlayer;
import com.example.mymusic.listener.OnSongArtistListener;
import com.example.mymusic.datamodel.Song;
import com.example.mymusic.R;
import com.example.mymusic.adapter.AdapterArtistSong;
import com.example.mymusic.service_music.PlayerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import wseemann.media.FFmpegMediaMetadataRetriever;


public class ActivityArtistSong extends AppCompatActivity implements OnSongArtistListener {

    private static final String EXTRA_CUR_POSITION = "com.example.mymusic.CUR_POSITIOM";
    private static final String EXTRA_ARTIST_LIST = "com.example.mymusic.PLAYLIST";
    private static final String EXTRA_ALL_SONG_LIST = "com.example.mymusic.ALL_SONG_LIST";

    private static List<Song> listArtist = new ArrayList<>();
    private static List<Song> listSong = new ArrayList<>();
    private static List<Song> listSongResult = new ArrayList<>();
    private AdapterArtistSong adapterArtistSong;
    private LinearLayoutManager linearLayoutManager;

    private int curPosition;

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private ImageView imvImageArtistCover;

    private RelativeLayout layout_play_music;
    private TextView tvSongName;
    private TextView tvArtistName;
    private ImageView imvImageCover;
    private ImageButton imbPausePlay;
    private ImageButton imbNext;
    private ImageButton imbBack;

    private boolean isPlaying;

    public static void launch(Context context, List<Song> listSongsArtist, List<Song> listAllSong, int curPosition) {
        Intent intent = new Intent(context, ActivityArtistSong.class);

        intent.putExtra(EXTRA_CUR_POSITION, curPosition);
        intent.putParcelableArrayListExtra(EXTRA_ARTIST_LIST, (ArrayList<Song>) listSongsArtist);
        intent.putParcelableArrayListExtra(EXTRA_ALL_SONG_LIST, (ArrayList<Song>) listAllSong);

        context.startActivity(intent);
    }

    private BroadcastReceiver artistSongBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.wtf("SongArtist_Broadcast", action);

            if (action.equals(PlayerService.ACTION_NEW_PLAY)){
                layout_play_music.setVisibility(View.VISIBLE);
            } else if (action.equals(PlayerService.ACTION_UPDATE_STATE_PLAY)){
                isPlaying = intent.getBooleanExtra(PlayerService.EXTRA_STATE_PLAY, false);
                Log.wtf("check", String.valueOf(isPlaying));
                if (isPlaying){
                    imbPausePlay.setBackgroundResource(R.drawable.ic_baseline_pause_24_brown);
                } else {
                    imbPausePlay.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24_brown);
                }
                AppConfig.getInstance(context).setStatePlay(isPlaying);
            } else if (action.equals(PlayerService.ACTION_UPDATE_SONG_INFO)) {
                setInfoSong(DataPlayer.getInstance().getCurrentSong());
            } else if (action.equals(PlayerService.ACTION_CLOSE_PLAYER)){
                layout_play_music.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listsong_artist);

        initView();

        loadSong();
        setAdapter();
        setToolbar();
    }

    @Override
    public void onStart() {
        super.onStart();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayerService.ACTION_NEW_PLAY);
        intentFilter.addAction(PlayerService.ACTION_UPDATE_STATE_PLAY);
        intentFilter.addAction(PlayerService.ACTION_UPDATE_SONG_INFO);
        intentFilter.addAction(PlayerService.ACTION_CLOSE_PLAYER);
        registerReceiver(artistSongBroadCast, intentFilter);
    }

    @Override
    public void onResume() {
        super.onResume();

        updateStatePlay();
        updateCurrentSong();
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(artistSongBroadCast);
    }

    private void updateStatePlay(){
        isPlaying = AppConfig.getInstance(this).getStatePlay();
        if (isPlaying){
            imbPausePlay.setBackgroundResource(R.drawable.ic_baseline_pause_24_brown);
        } else {
            imbPausePlay.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24_brown);
        }
    }

    private void updateCurrentSong(){
        String songName = AppConfig.getInstance(this).getSongName();
        String artistName = AppConfig.getInstance(this).getSongArtist();
        String path = AppConfig.getInstance(this).getSongPath();

        tvSongName.setText(songName);
        tvArtistName.setText(artistName);

        if (path != null){
            byte[] albumArt = getAlbumArt(path);

            Glide.with(this)
                    .load(albumArt)
                    .centerCrop()
                    .placeholder(R.drawable.music_default_cover)
                    .into(imvImageCover);
        }
    }

    private void setToolbar(){
        toolbar.setTitle(listArtist.get(curPosition).getArtistName());

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initView(){
        listArtist = getIntent().getParcelableArrayListExtra(EXTRA_ARTIST_LIST);
        curPosition = getIntent().getIntExtra(EXTRA_CUR_POSITION, 0);
        listSong = getIntent().getParcelableArrayListExtra(EXTRA_ALL_SONG_LIST);

        toolbar = findViewById(R.id.toolbar_artist);
        recyclerView = findViewById(R.id.recycler_list_songs);
        imvImageArtistCover = findViewById(R.id.image_cover_artist);

        layout_play_music = findViewById(R.id.layout_play_music);
        tvSongName      = findViewById(R.id.tv_song_name);
        tvArtistName    = findViewById(R.id.tv_artist_name);
        imbPausePlay    = findViewById(R.id.imb_pause_play);
        imbBack         = findViewById(R.id.imb_back);
        imbNext         = findViewById(R.id.imb_next);
        imvImageCover   = findViewById(R.id.imv_imageCover);

        layout_play_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int curPosition = AppConfig.getInstance(v.getContext()).getCurposition();
                AppConfig.getInstance(v.getContext()).setIsNewPlay(false);
                PlayerActivity.launch(v.getContext(), DataPlayer.getInstance().getPlaylist(), curPosition);
            }
        });

        imbPausePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PlayerService.class);
                intent.setAction(PlayerService.ACTION_PLAY_PAUSE_MUSIC);
                startService(intent);
            }
        });

        imbNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PlayerService.class);
                intent.setAction(PlayerService.ACTION_NEXT_SONG);
                startService(intent);
            }
        });

        imbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PlayerService.class);
                intent.setAction(PlayerService.ACTION_PREVIOUS_SONG);
                startService(intent);
            }
        });

        if (AppConfig.getInstance(getApplicationContext()).getSongName() == null){
            layout_play_music.setVisibility(View.GONE);
        } else {
            layout_play_music.setVisibility(View.VISIBLE);
        }
    }

    private void setInfoSong(Song song){
        tvSongName.setText(song.getSongName());
        tvArtistName.setText(song.getArtistName());

        Glide.with(this)
                .load(getAlbumArt(song.getUrlSong()))
                .centerCrop()
                .placeholder(R.drawable.music_default_cover)
                .into(imvImageCover);

    }


    private void setAdapter(){
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapterArtistSong = new AdapterArtistSong(listSongResult, this);
        adapterArtistSong.setOnSongArtistListener(this);
        recyclerView.setAdapter(adapterArtistSong);
        adapterArtistSong.notifyDataSetChanged();
    }

    private void loadSong(){
        listSongResult.clear();
        for (int i = 0; i < listSong.size(); i++){
            if (listSong.get(i).getArtistName().equalsIgnoreCase(listArtist.get(curPosition).getArtistName())){
                listSongResult.add(listSong.get(i));
            }
        }

        Random random = new Random();
        int position = random.nextInt(listSongResult.size());

        Glide.with(this)
                .load(getAlbumArt(listSongResult.get(position).getUrlSong()))
                .centerCrop()
                .placeholder(R.drawable.music_default_cover)
                .into(imvImageArtistCover);
    }

    private byte[] getAlbumArt(String path) {
        MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever ();

        try {
            mediaMetadata.setDataSource(path);
        } catch (Exception e) {
        }

        return mediaMetadata.getEmbeddedPicture();
    }



    @Override
    public void onSongArtist(int i) {
        PlayerActivity.launch(this, listSongResult, i);
        DataPlayer.getInstance().setPlayPosition(i);
        AppConfig.getInstance(this).setCurPosition(i);
        AppConfig.getInstance(this).setPlaylist(listSongResult);
        DataPlayer.getInstance().setPlaylist(listSongResult);

        Log.wtf("ArtistSong", String.valueOf(DataPlayer.getInstance().getPlayPosition()) + " " + "position");
        Log.wtf("ArtistSong", String.valueOf(DataPlayer.getInstance().getPlaylist().size()));
    }
}
