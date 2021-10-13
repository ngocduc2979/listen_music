package com.example.mymusic.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusic.listener.OnEditSongListener;
import com.example.mymusic.database.PlaylistDatabase;
import com.example.mymusic.R;
import com.example.mymusic.adapter.EditSongAdapter;
import com.example.mymusic.datamodel.Song;

import java.util.ArrayList;
import java.util.List;

public class EditSongActivity extends AppCompatActivity implements OnEditSongListener {

    private static final String KEY_LIST_SONG = "list song";
    private static final String KEY_TABLE_NAME = "table name";

    private List<Song> listEditSong = new ArrayList<>();
    private List<Song> list = new ArrayList<>();
    private EditSongAdapter editSongAdapter;
    private PlaylistDatabase playlistDatabase;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ImageView imvDelete;
    private TextView tvEditSong;

    private String tableName;

    public static void launch(Context context, ArrayList<Song> list, String tableName){
        Intent intent = new Intent(context, EditSongActivity.class);
        intent.putParcelableArrayListExtra(KEY_LIST_SONG, list);
        intent.putExtra(KEY_TABLE_NAME, tableName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_song_layout);

        initView();
        setAdapter();
        clickDelete();

    }

    private void setAdapter(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        editSongAdapter = new EditSongAdapter(listEditSong, this);
        editSongAdapter.setOnEditSongListener(this);
        recyclerView.setAdapter(editSongAdapter);
        editSongAdapter.notifyDataSetChanged();
    }

    private void loadList(){
        listEditSong = getIntent().getParcelableArrayListExtra(KEY_LIST_SONG);
    }

    private void initView() {
        imvDelete = findViewById(R.id.add_song);
        recyclerView = findViewById(R.id.list_add_song);
        toolbar = findViewById(R.id.toolbar);
        tvEditSong = findViewById(R.id.add_song_text);

        imvDelete.setBackgroundResource(R.drawable.ic_baseline_delete_24);
        tvEditSong.setText("Delete");
        toolbar.setTitle("Edit");

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        loadList();
    }

    private void clickDelete(){
        imvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                playlistDatabase = new PlaylistDatabase(getApplicationContext(), "playlist.db", null, 1);
                tableName = getIntent().getStringExtra(KEY_TABLE_NAME);

                for (int i = 0; i<list.size(); i++){
                    Log.wtf("deleteCheck", String.valueOf(list.get(i).getSongName()));
                }


                for (int i = 0; i < list.size(); i++) {
                    String deleteSong = "DELETE FROM " + "'" + tableName + "' " + "WHERE songName = " +
                            "'" + list.get(i).getSongName() + "' AND artistName = " +
                            "'" + list.get(i).getArtistName() + "' AND urlSong = " +
                            "'" + list.get(i).getUrlSong() + "' AND album = " +
                            "'" + list.get(i).getAlbum() + "' AND duration = " +
                            "'" + list.get(i).getDuration() + "'";
                    playlistDatabase.querryData(deleteSong);
                }
                onBackPressed();
            }
        });
    }


    @Override
    public void onEditSong(List<Song> listSong) {
        list = listSong;
    }
}
