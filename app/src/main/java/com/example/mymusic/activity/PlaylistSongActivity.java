package com.example.mymusic.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mymusic.listener.OnPlaylistSongListener;
import com.example.mymusic.database.PlaylistDatabase;
import com.example.mymusic.R;
import com.example.mymusic.adapter.PlaylistSongAdapter;
import com.example.mymusic.datamodel.Song;
import com.example.mymusic.savedata.AppConfig;
import com.example.mymusic.savedata.DataPlayer;
import com.example.mymusic.service_music.PlayerService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class PlaylistSongActivity extends AppCompatActivity implements OnPlaylistSongListener {
    private static final String KEY_POS = "position";
    private static final String KEY_LIST_PLAYLIST = "playlist_list";
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private PlaylistSongAdapter playlistSongAdapter;
    private final ArrayList<Song> playlistSong = new ArrayList<>();
    private ArrayList<String> playlist = new ArrayList<>();
    private PlaylistDatabase playlistDatabase;

    private boolean isPlaying = false;
    private String tableName;

    private RelativeLayout layout_play_music;
    private TextView tvSongName;
    private TextView tvArtistName;
    private ImageView imvImageCover;
    private ImageButton imbPausePlay;
    private ImageButton imbNext;
    private ImageButton imbBack;
    private LinearLayout layout_add;
    private LinearLayout layout_edit;

    public static void launch(Context context, int position, ArrayList<String> playlist){
        Intent intent = new Intent(context, PlaylistSongActivity.class);
        intent.putExtra(KEY_POS, position);
        intent.putStringArrayListExtra(KEY_LIST_PLAYLIST, playlist);
        context.startActivity(intent);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_song_layout);

        initView();
        setAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayerService.ACTION_NEW_PLAY);
        intentFilter.addAction(PlayerService.ACTION_UPDATE_STATE_PLAY);
        intentFilter.addAction(PlayerService.ACTION_UPDATE_SONG_INFO);
        intentFilter.addAction(PlayerService.ACTION_CLOSE_PLAYER);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateStatePlay();
        updateCurrentSong();
        loadList();
        setAdapter();

    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
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
                    .placeholder(R.drawable.background_default_song)
                    .into(imvImageCover);
        }
    }

    private void setAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        playlistSongAdapter = new PlaylistSongAdapter(playlistSong, getApplicationContext());
        playlistSongAdapter.setOnPlaylistSongListener(this);
        recyclerView.setAdapter(playlistSongAdapter);
        playlistSongAdapter.notifyDataSetChanged();
    }

    private void sortByName(){
        Collections.sort(playlistSong, new Comparator<Song>() {
            @Override
            public int compare(Song song1, Song song2) {
                return song1.getSongName().compareTo(song2.getSongName());
            }
        });
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycler_list);

        layout_play_music = findViewById(R.id.layout_play_music);
        tvSongName      = findViewById(R.id.tv_song_name);
        tvArtistName    = findViewById(R.id.tv_artist_name);
        imbPausePlay    = findViewById(R.id.imb_pause_play);
        imbBack         = findViewById(R.id.imb_back);
        imbNext         = findViewById(R.id.imb_next);
        imvImageCover   = findViewById(R.id.imv_imageCover);
        layout_add      = findViewById(R.id.layout_add);
        layout_edit     = findViewById(R.id.layout_edit);

        if (AppConfig.getInstance(getApplicationContext()).getSongName() == null){
            layout_play_music.setVisibility(View.GONE);
        } else {
            layout_play_music.setVisibility(View.VISIBLE);
        }

        loadList();

        layout_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditSongActivity.launch(PlaylistSongActivity.this, playlistSong, tableName);
            }
        });

        layout_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddSongActivity.launch(PlaylistSongActivity.this, playlistSong, tableName);
            }
        });

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

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
    }

    private void setInfoSong(Song song){
        tvSongName.setText(song.getSongName());
        tvArtistName.setText(song.getArtistName());

        byte[] albumArt = getAlbumArt(song.getUrlSong());

        Glide.with(this)
                .load(albumArt)
                .centerCrop()
                .placeholder(R.drawable.background_default_song)
                .into(imvImageCover);
    }

    private void loadList() {
        playlistSong.clear();
        playlist = getIntent().getStringArrayListExtra(KEY_LIST_PLAYLIST);
        int position = getIntent().getIntExtra(KEY_POS, 0);
        tableName = playlist.get(position).toString();
        toolbar.setTitle(tableName);
        String sql = "SELECT * FROM " + "'" + tableName + "'";

        playlistDatabase = new PlaylistDatabase(this, "playlist.db", null, 1);
        Cursor cursor = playlistDatabase.getData(sql);
        while (cursor.moveToNext()){
            String name = cursor.getString(0);
            String artist = cursor.getString(1);
            String urlSong = cursor.getString(2);
            String album = cursor.getString(3);
            String duration = cursor.getString(4);
            playlistSong.add(new Song(name, artist, urlSong, album, duration));
        }

        sortByName();
    }

    private byte[] getAlbumArt(String path){
        MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
        mediaMetadata.setDataSource(path);
        return mediaMetadata.getEmbeddedPicture();
    }

    @Override
    public void onPlaylistSong(int pos) {
        PlayerActivity.launch(this, playlistSong, pos);
        AppConfig.getInstance(this).setCurPosition(pos);
        DataPlayer.getInstance().setPlaylist(playlistSong);
        DataPlayer.getInstance().setPlayPosition(pos);
    }
}
