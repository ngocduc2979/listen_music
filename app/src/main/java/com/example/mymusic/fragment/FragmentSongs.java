package com.example.mymusic.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mymusic.AppConfig;
import com.example.mymusic.DataPlayer;
import com.example.mymusic.adapter.AdapterSongs;
import com.example.mymusic.datamodel.Song;
import com.example.mymusic.OnMusicListener;
import com.example.mymusic.R;
import com.example.mymusic.service_music.PlayerService;
import com.example.mymusic.activity.PlayerActivity;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.List;

public class FragmentSongs extends Fragment implements OnMusicListener {

    private RecyclerView recyclerView;
    public List<Song> listSongs = new ArrayList<>();
    private AdapterSongs songsAdapter;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());


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
        DataPlayer.getInstance().setPlaylist(listSongs);
    }

    private void initView(View view){
        recyclerView = view.findViewById(R.id.recycler_list_songs);
    }

    @Override
    public void onMusic(int position) {
        PlayerActivity.launch(getContext(), listSongs, position);
        AppConfig.getInstance(getContext()).setCurPosition(position);
        DataPlayer.getInstance().setPlayPosition(position);
    }

}


