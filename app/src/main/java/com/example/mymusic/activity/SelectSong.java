package com.example.mymusic.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusic.database.PlaylistDatabase;
import com.example.mymusic.R;
import com.example.mymusic.adapter.SelectSongAdapter;
import com.example.mymusic.datamodel.Song;

import java.util.ArrayList;
import java.util.List;

import com.example.mymusic.listener.OnSelectSongListener;

public class SelectSong extends AppCompatActivity implements OnSelectSongListener {

    private List<Song> listSongs = new ArrayList<>();
    private List<Song> list = new ArrayList<>();

    private SelectSongAdapter selectSongAdapter;
    private RecyclerView recyclerView;
    private PlaylistDatabase playlistDatabase;

    public static final String KEY_TABLE_NAME = "com.example.mymusic.tablename";

    public static void lunch(Context context, String tableName){
        Intent intent = new Intent(context, SelectSong.class);
        intent.putExtra(KEY_TABLE_NAME, tableName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_song_layout);

        iniView();
        loadSong();
        setAdapter();

    }

    private void setAdapter(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        selectSongAdapter = new SelectSongAdapter(listSongs, this);
        selectSongAdapter.setOnPlaylistListener(this);
        recyclerView.setAdapter(selectSongAdapter);
        selectSongAdapter.notifyDataSetChanged();
    }

    private void iniView(){
        recyclerView = findViewById(R.id.list_song);
        Toolbar toolbar             = findViewById(R.id.toolbar);
        ImageView imvAddSong        = findViewById(R.id.add_song);


        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        imvAddSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPlaylist();
                onBackPressed();
            }
        });
    }

    private void createPlaylist() {

        //AUTOINCREMENT
        String tableName = getIntent().getStringExtra(KEY_TABLE_NAME);

        String createTable = "CREATE TABLE IF NOT EXISTS " + "'" + tableName + "'" +
                "(songName Text PRIMARY KEY, artistName Text, urlSong Text, album Text, Duration Text)";

//        db = SQLiteDatabase.openOrCreateDatabase("playlist.db", null);
//        db.execSQL(createTable);

//        for (int i = 0; i < listSongs.size(); i++) {
//            if (isChecked) {
//                String addSong = "SELECT INTO " + tableName + " (songName, artistName, urlSong, album, Duration)" +
//                        "VALUES " + listSongs.get(i).getSongName() + listSongs.get(i).getArtistName()
//                        + listSongs.get(i).getUrlSong() + listSongs.get(i).getAlbum()
//                        + listSongs.get(i).getDuration();
//                db.execSQL(addSong);
//            }
//        }

        playlistDatabase = new PlaylistDatabase(this, "playlist.db", null, 1);
        playlistDatabase.querryData(createTable);

        for (int i = 0; i < list.size(); i++) {
                String addSong = "INSERT INTO " + "'" + tableName + "' " +
                        "VALUES (" + "'" + list.get(i).getSongName() + "', " +
                        "'" + list.get(i).getArtistName() + "', " +
                        "'" + list.get(i).getUrlSong() + "', " +
                        "'" + list.get(i).getAlbum() + "', " +
                        "'" + list.get(i).getDuration() + "')";
                playlistDatabase.querryData(addSong);
        }
    }

    private void loadSong(){
        listSongs.clear();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String singerName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String songName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

                if (!url.endsWith(".amr")) {
                    listSongs.add(new Song(songName, singerName, url, album, duration));
                }
            }
        }
    }

    @Override
    public void onPlaylist(List<Song> listSong) {
        list = listSong;
    }
}
