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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusic.listener.OnAddSongListener;
import com.example.mymusic.database.PlaylistDatabase;
import com.example.mymusic.R;
import com.example.mymusic.adapter.AddSongAdapter;
import com.example.mymusic.datamodel.Song;

import java.util.ArrayList;
import java.util.List;

public class AddSongActivity extends AppCompatActivity implements OnAddSongListener {

    private static final String KEY_LIST_SONG_PLAYLIST = "list song playlist";
    private static final String KEY_TABLE_NAME = "table name";

    private RecyclerView recyclerView;
    private ImageView imvAddSong;
    private androidx.appcompat.widget.Toolbar toolbar1;

    private final ArrayList<Song> listAllSong = new ArrayList<>();
    private ArrayList<Song> listSongDefault = new ArrayList<>();
    private List<Song> listAddSong = new ArrayList<>();
    private String tableName;

    private AddSongAdapter addSongAdapter;
    private PlaylistDatabase playlistDatabase;

    public static void launch(Context context, ArrayList<Song> listSong, String tableName){
        Intent intent = new Intent(context, AddSongActivity.class);
        intent.putParcelableArrayListExtra(KEY_LIST_SONG_PLAYLIST, listSong);
        intent.putExtra(KEY_TABLE_NAME, tableName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_song_layout);

        initView();
        loadSong();
//        setDataListAddSong();
        setAdapter();
        clickAdd();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setAdapter();
    }

    private void clickAdd(){
        imvAddSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                playlistDatabase = new PlaylistDatabase(getApplicationContext(), "playlist.db", null, 1);
                tableName = getIntent().getStringExtra(KEY_TABLE_NAME);

                for (int i = 0; i < listAddSong.size(); i++) {
                    String addSong = "INSERT INTO " + "'" + tableName + "'" + " (songName, artistName, urlSong, album, duration)" +
                            "VALUES (" + "'" + listAddSong.get(i).getSongName() + "', " +
                            "'" + listAddSong.get(i).getArtistName() + "', " +
                            "'" + listAddSong.get(i).getUrlSong() + "', " +
                            "'" + listAddSong.get(i).getAlbum() + "', " +
                            "'" + listAddSong.get(i).getDuration() + "')";
                    playlistDatabase.querryData(addSong);
                }

                onBackPressed();
            }
        });
    }

    private void initView(){
        imvAddSong = findViewById(R.id.add_song);
        recyclerView = findViewById(R.id.list_add_song);
        toolbar1 = findViewById(R.id.toolbar);



        toolbar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setAdapter(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        addSongAdapter = new AddSongAdapter(listAllSong, this);
        addSongAdapter.setOnAddSongListener(this);
        recyclerView.setAdapter(addSongAdapter);
        addSongAdapter.notifyDataSetChanged();
    }

    private void loadSong(){
        listAllSong.clear();
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
                    listAllSong.add(new Song(songName, singerName, url, album, duration));
                }
            }
        }
        listSongDefault = getIntent().getParcelableArrayListExtra(KEY_LIST_SONG_PLAYLIST);

        for (int i = 0; i < listSongDefault.size(); i++){
            String name = listSongDefault.get(i).getSongName();
            String artist = listSongDefault.get(i).getArtistName();
            String url = listSongDefault.get(i).getUrlSong();
            String album = listSongDefault.get(i).getAlbum();
            String duration = listSongDefault.get(i).getDuration();
            for (int j = 0; j < listAllSong.size(); j++){
                String nameAllsong = listAllSong.get(j).getSongName();
                String artistAllsong = listAllSong.get(j).getArtistName();
                String urlAllsong = listAllSong.get(j).getUrlSong();
                String albumAllsong = listAllSong.get(j).getAlbum();
                String durationAllsong = listAllSong.get(j).getDuration();
                if (name.equals(nameAllsong) && artist.equals(artistAllsong) && url.equals(urlAllsong)
                && album.equals(albumAllsong) && duration.equals(durationAllsong)){
                    listAllSong.remove(j);
                }
            }
        }
    }


    @Override
    public void onAddSong(List<Song> list) {
        listAddSong = list;
    }
}
