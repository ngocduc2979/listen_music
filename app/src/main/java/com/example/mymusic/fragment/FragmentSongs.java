package com.example.mymusic.fragment;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mymusic.savedata.AppConfig;
import com.example.mymusic.savedata.DataPlayer;
import com.example.mymusic.adapter.AdapterSongs;
import com.example.mymusic.datamodel.Song;
import com.example.mymusic.listener.OnMusicListener;
import com.example.mymusic.R;
import com.example.mymusic.activity.PlayerActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FragmentSongs extends Fragment implements OnMusicListener {

    private RecyclerView recyclerView;
    public List<Song> listSongs = new ArrayList<>();
    private AdapterSongs songsAdapter;
    private final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());


    @Override
    public void onCreate(Bundle savedInstanceState) {

        loadSong();

        Log.wtf("SongFragment", "onCreate");



        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        initView(view);
        setAdapter();
        Log.wtf("SongFragment", "onCreateView");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSong();
        Log.wtf("SongFragment", "onResume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setAdapter(){

        Log.wtf("SongFragment", "setAdapter");
        recyclerView.setLayoutManager(linearLayoutManager);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        songsAdapter = new AdapterSongs(listSongs, getContext());
        songsAdapter.setOnMusicListener(this);
        recyclerView.setAdapter(songsAdapter);
        songsAdapter.notifyDataSetChanged();
    }

    private void loadSong() {
        listSongs.clear();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC;
        Cursor cursor = getActivity().getContentResolver().query(uri, null, selection, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                @SuppressLint("Range") String singerName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                @SuppressLint("Range") String songName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                @SuppressLint("Range") String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                @SuppressLint("Range") String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

//                if (cursor.moveToNext()) {
//                        Long albumId = Long.valueOf(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
//                        Cursor cursorAlbum = getActivity().getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
//                                new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
//                                MediaStore.Audio.Albums._ID + "=" + albumId, null, null);
//
//                        if(cursorAlbum != null && cursorAlbum.moveToNext()){
//                            String albumCoverPath = cursorAlbum.getString(cursorAlbum.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
//
//                            if (!url.endsWith(".amr")) {
//                                listSongs.add(new Song(songName, singerName, url, album, duration));
//                            }
//                        }
//                }
                if (!url.endsWith(".amr")) {
                    listSongs.add(new Song(songName, singerName, url, album, duration));
                }
            }
        }

        Collections.sort(listSongs, new Comparator<Song>() {
            @Override
            public int compare(Song song, Song song2) {
                return song.getSongName().trim().compareTo(song2.getSongName().trim());
            }
        });

    }

    private void initView(View view){
        recyclerView = view.findViewById(R.id.recycler_list_songs);
    }

    @Override
    public void onMusic(int position) {
        PlayerActivity.launch(getContext(), listSongs, position);
        AppConfig.getInstance(getContext()).setCurPosition(position);
        DataPlayer.getInstance().setPlayPosition(position);
        DataPlayer.getInstance().setPlaylist(listSongs);
    }
}


