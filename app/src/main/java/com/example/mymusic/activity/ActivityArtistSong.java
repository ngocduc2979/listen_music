package com.example.mymusic.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusic.ObjectSong;
import com.example.mymusic.R;
import com.example.mymusic.adapter.AdapterArtistSong;

import java.util.ArrayList;
import java.util.List;

import static com.example.mymusic.fragment.FragmentArtist.listSongsArtist;
import static com.example.mymusic.fragment.FragmentSongs.listSongs;

public class ActivityArtistSong extends AppCompatActivity {

    private static List<ObjectSong> listArtist = new ArrayList<>();
    private static List<ObjectSong> listSong = new ArrayList<>();
    private static List<ObjectSong> listSongResult = new ArrayList<>();
    private AdapterArtistSong adapterArtistSong;
    private LinearLayoutManager linearLayoutManager;

    private int position;

    private RecyclerView recyclerView;
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listsong_artist);

        initView();

        position = getIntent().getIntExtra("positionArtist", 0);
        listArtist  = listSongsArtist;
        listSong    = listSongs;

        loadSong();
        setAdapter();
        setToolbar();

    }

    private void setToolbar(){
        toolbar.setTitle(listSongResult.get(position).getArtistName());

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initView(){
        toolbar = findViewById(R.id.toolbar_artist);
        recyclerView = findViewById(R.id.recycler_list_songs);
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    private void setAdapter(){
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterArtistSong = new AdapterArtistSong(listSongResult, this);
        recyclerView.setAdapter(adapterArtistSong);
        adapterArtistSong.notifyDataSetChanged();
    }

    private void loadSong(){
        listSongResult.clear();
        for (int i = 0; i < listSong.size(); i++){
            if (listSong.get(i).getArtistName().equalsIgnoreCase(listArtist.get(position).getArtistName())){
                listSongResult.add(listSong.get(i));
            }
        }
    }
}
