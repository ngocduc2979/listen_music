package com.example.mymusic.fragment;

import android.Manifest;
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

import com.example.mymusic.activity.ActivityPlayMusic;
import com.example.mymusic.activity.MainActivity;
import com.example.mymusic.adapter.AdapterSongs;
import com.example.mymusic.ObjectSong;
import com.example.mymusic.OnMusicListener;
import com.example.mymusic.R;
import com.example.mymusic.service_music.ServiceMusic;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.List;

public class FragmentSongs extends Fragment implements OnMusicListener {

    private RecyclerView recyclerView;
    public static List<ObjectSong> listSongs = new ArrayList<>();
    private AdapterSongs songsAdapter;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.wtf("SongFragment", "permission");
            requestPermission();
        }

        loadSong();

        Log.wtf("SongFragment", "onCreate");

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        recyclerView = view.findViewById(R.id.recycler_list_songs);
        setAdapter();
        Log.wtf("SongFragment", "onCreateView");
        return view;
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
                String songName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

                listSongs.add(new ObjectSong(songName, singerName, url, album, duration));
            }
        }
    }

    private void requestPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getContext(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        TedPermission.with(getContext())
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }


    @Override
    public void onMusic(int position) {
        Intent intentService = new Intent(getContext(), ServiceMusic.class);
        intentService.putExtra("position", position);
        getActivity().startService(intentService);

//        Intent intent = new Intent(getContext(), ActivityPlayMusic.class);
//        intent.putExtra("position", position);
//        startActivity(intent);
    }
}


