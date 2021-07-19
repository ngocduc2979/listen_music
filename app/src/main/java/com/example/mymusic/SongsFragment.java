package com.example.mymusic;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.List;

public class SongsFragment extends Fragment implements OnMusicListener{

    private RecyclerView recyclerView;
    static List<Songs> listSongs = new ArrayList<>();
    private SongsAdapter songsAdapter;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        recyclerView = view.findViewById(R.id.recycler_list_songs);

        setAdapter();
        loadSong();

        return view;
    }

    private void setAdapter(){
        recyclerView.setLayoutManager(linearLayoutManager);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        songsAdapter = new SongsAdapter(listSongs, getContext());
        songsAdapter.setOnMusicListener(this::onMusic);
        recyclerView.setAdapter(songsAdapter);
        songsAdapter.notifyDataSetChanged();
    }

    private void loadSong() {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC;
        Cursor cursor = getActivity().getContentResolver().query(uri, null, selection, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String singerName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String songName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                String imageCover = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

                listSongs.add(new Songs(songName, singerName, imageCover, url, duration));
            }
        }
    }

    @Override
    public void onMusic(int i) {
        Intent intent = new Intent(getContext(), PlayMusic.class);
        intent.putExtra("position", i);
        startActivity(intent);
    }
}


