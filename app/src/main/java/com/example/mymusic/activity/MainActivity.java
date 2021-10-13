package com.example.mymusic.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mymusic.savedata.AppConfig;
import com.example.mymusic.savedata.DataPlayer;
import com.example.mymusic.R;
import com.example.mymusic.adapter.AdapterViewPager;
import com.example.mymusic.datamodel.Song;
import com.example.mymusic.fragment.FragmentArtist;
import com.example.mymusic.fragment.FragmentPlaylist;
import com.example.mymusic.fragment.FragmentSongs;
import com.example.mymusic.service_music.PlayerService;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.crash.FirebaseCrash;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AdapterViewPager viewPagerAdapter;
    private RelativeLayout layout_play_music;
    private TextView tvSongName;
    private TextView tvArtistName;
    private ImageView imvImageCover;
    private ImageButton imbPausePlay;
    private ImageButton imbNext;
    private ImageButton imbBack;

    private List<Fragment> listFragment = new ArrayList<>();
    private List<String> listTittle = new ArrayList<>();

    private boolean isPlaying;

    private BroadcastReceiver maninActivity = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.wtf("FragmentSong_Broadcast", action);

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.wtf("Main", "onCreate");

        initView();

        requestPermission();
    }

    @Override
    public void onStart() {
        super.onStart();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayerService.ACTION_NEW_PLAY);
        intentFilter.addAction(PlayerService.ACTION_UPDATE_STATE_PLAY);
        intentFilter.addAction(PlayerService.ACTION_UPDATE_SONG_INFO);
        intentFilter.addAction(PlayerService.ACTION_CLOSE_PLAYER);
        registerReceiver(maninActivity, intentFilter);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateStatePlay();
        updateCurrentSong();

        FirebaseCrash.log("Button clicked!");

        if (AppConfig.getInstance(getApplicationContext()).getSongName() == null){
            layout_play_music.setVisibility(View.GONE);
        } else {
            layout_play_music.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(maninActivity);
    }

    private void setAdapter(){
//        TabLayoutAdapter tabLayoutAdapter = new TabLayoutAdapter(getSupportFragmentManager());
//        viewPager.setAdapter(tabLayoutAdapter);
//        tabLayout.setupWithViewPager(viewPager);

        Log.wtf("MainActivity", "setAdapter");

        viewPagerAdapter = new AdapterViewPager(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new FragmentSongs(), "Songs");
        viewPagerAdapter.addFragment(new FragmentArtist(), "Artists");
        viewPagerAdapter.addFragment(new FragmentPlaylist(), "Playlists");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPagerAdapter.notifyDataSetChanged();
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

//        if (path != null){
//            byte[] albumArt = getAlbumArt(path);
//
//            Glide.with(this)
//                    .load(albumArt)
//                    .centerCrop()
//                    .placeholder(R.drawable.background_default_song)
//                    .into(imvImageCover);
//        }
    }

    private void initView(){
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
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
                AppConfig.getInstance(v.getContext()).setIsNewPlay(false);
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

    private byte[] getAlbumArt(String path){
        MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
        mediaMetadata.setDataSource(path);
        return mediaMetadata.getEmbeddedPicture();
    }

    private void requestPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
//                initView();
                setAdapter();
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }
}