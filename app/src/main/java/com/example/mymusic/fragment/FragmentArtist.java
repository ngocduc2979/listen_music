package com.example.mymusic.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mymusic.activity.ActivityArtistSong;
import com.example.mymusic.adapter.AdapterArtist;
import com.example.mymusic.ObjectSong;
import com.example.mymusic.OnAtistListener;
import com.example.mymusic.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.mymusic.fragment.FragmentSongs.listSongs;


public class FragmentArtist extends Fragment implements OnAtistListener {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private AdapterArtist artistAdapter;
    public static List<ObjectSong> listSongsArtist = new ArrayList<>();
    static List<ObjectSong> list = new ArrayList<>();

    boolean checkArtist;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.wtf("ArtistFragment", "onCreate");

        loadSong();

        loadListSong();

        for (int i = 0; i < listSongsArtist.size(); i++){
            Log.wtf("checklist", listSongsArtist.get(i).getArtistName());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artists, container, false);

        Log.wtf("ArtistFragment", "onCreateView");

        recyclerView = view.findViewById(R.id.recycler_list_songs);
        setAdapter();

        return view;
    }

    private void loadListSong(){
        checkArtist = true;

        listSongsArtist.clear();

        for (int i = 0; i < list.size(); i++){
            for (int j = (i+1); j < (list.size()); j++){
                if (list.get(i).getArtistName().equalsIgnoreCase(list.get(j).getArtistName())){
                    checkArtist = false;
                }
            }
            if (checkArtist){
                listSongsArtist.add(list.get(i));
            }
            checkArtist = true;
            }
        }


    private void setAdapter(){
        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        artistAdapter = new AdapterArtist(listSongsArtist, getContext());
        artistAdapter.setOnAtistListener(this);
        recyclerView.setAdapter(artistAdapter);
        artistAdapter.notifyDataSetChanged();
    }

    private void loadSong() {
        list.clear();
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

                list.add(new ObjectSong(songName, singerName, url, album, duration));
            }
        }
    }

    @Override
    public void onArtist(int i) {
        Intent intent = new Intent(getContext(), ActivityArtistSong.class);
        intent.putExtra("positionArtist", i);
        startActivity(intent);
    }
}