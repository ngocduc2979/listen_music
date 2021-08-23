package com.example.mymusic.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcel;
import android.os.Parcelable;
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
    public static ArrayList<ObjectSong> listSongs = new ArrayList<>();
    private AdapterSongs songsAdapter;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

    private RelativeLayout layoutPlayMusic;
    private ImageButton imbPausePlay;
    private ImageButton imbNext;
    private ImageButton imbBack;
    private TextView tvSongName;
    private TextView tvArtistName;
    private ImageView imvImageCover;

    private int position;
    private boolean checkPlay;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            position = intent.getIntExtra("position", 0);
            checkPlay = intent.getBooleanExtra("check_play", true);
            int action = intent.getIntExtra("action_to_fragmentSong", 0);

            setLayoutPlayMusic(action);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.wtf("SongFragment", "permission");
            requestPermission();
        }

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter("send_data_to_fragmentSong"));

        loadSong();

        Log.wtf("SongFragment", "onCreate");

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        recyclerView = view.findViewById(R.id.recycler_list_songs);
        initView(view);
        setAdapter();
        Log.wtf("SongFragment", "onCreateView");
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
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
                loadSong();
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

    private void initView(View view){
        layoutPlayMusic = view.findViewById(R.id.layout_play_music);
        imvImageCover   = view.findViewById(R.id.imv_imageCover);
        tvSongName      = view.findViewById(R.id.tv_song_name);
        tvArtistName    = view.findViewById(R.id.tv_artist_name);
        imbPausePlay    = view.findViewById(R.id.imb_pause_play);
        imbBack         = view.findViewById(R.id.imb_back);
        imbNext         = view.findViewById(R.id.imb_next);
    }

    private byte[] getAlbumArt(String path){
        MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
        mediaMetadata.setDataSource(path);
        return mediaMetadata.getEmbeddedPicture();
    }


    @Override
    public void onMusic(int position) {
        Intent intentService = new Intent(getContext(), ServiceMusic.class);
        intentService.putExtra("position", position);
        getActivity().startService(intentService);

        Intent intent = new Intent(getContext(), ActivityPlayMusic.class);
        intent.putParcelableArrayListExtra("list_from_fragmentSong_to_playMusic", listSongs);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    private void setLayoutPlayMusic(int action){
        switch (action){
            case ServiceMusic.ACTION_START:
                layoutPlayMusic.setVisibility(View.VISIBLE);
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
                layoutPlayMusic.setVisibility(View.GONE);
                break;
        }
    }

    private void setInfoSong(){
        tvSongName.setText(listSongs.get(position).getSongName());
        tvArtistName.setText(listSongs.get(position).getArtistName());

        byte[] albumArt = getAlbumArt(listSongs.get(position).getUrlSong());

        Glide.with(this)
                .load(albumArt)
                .centerCrop()
                .placeholder(R.drawable.background_default_song)
                .into(imvImageCover);

        layoutPlayMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ActivityPlayMusic.class);
                startActivity(intent);
            }
        });

        imbPausePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPlay){
                    sendActionToService(ServiceMusic.ACTION_PAUSE);
                } else {
                    sendActionToService(ServiceMusic.ACTION_PLAY);
                }
            }
        });

        imbNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendActionToService(ServiceMusic.ACTION_NEXT);
            }
        });

        imbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendActionToService(ServiceMusic.ACTION_PREVIEW);
            }
        });
    }

    private void checkPlaying(){
        if (checkPlay){
            imbPausePlay.setBackgroundResource(R.drawable.ic_baseline_pause_24_brown);
        } else {
            imbPausePlay.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24_brown);
        }
    }

    private void sendActionToService(int action){
        Intent intent = new Intent(getContext(), ServiceMusic.class);
        intent.setAction(ServiceMusic.ACTION_PAUSE_MUSIC);
        intent.putParcelableArrayListExtra("list_from_fragmentSong", listSongs);
        intent.putExtra("action_service_from_fragmentSong", action);
        getActivity().startService(intent);
    }
}


