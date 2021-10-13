package com.example.mymusic.fragment;

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
import com.example.mymusic.datamodel.Song;
import com.example.mymusic.listener.OnAtistListener;
import com.example.mymusic.R;

import java.util.ArrayList;
import java.util.List;


public class FragmentArtist extends Fragment implements OnAtistListener {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private AdapterArtist artistAdapter;
    public List<Song> listSongsArtist = new ArrayList<>();
    static List<Song> list = new ArrayList<>();

    boolean checkArtist;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadAllSong();

        loadListArtist();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artists, container, false);

        Log.wtf("ArtistFragment", "onCreateView");

        recyclerView = view.findViewById(R.id.recycler_list_songs);
        setAdapter();

        return view;
    }

    private void loadListArtist(){
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

    private void loadAllSong() {
        list.clear();
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

                list.add(new Song(songName, singerName, url, album, duration));
            }
        }
    }

    @Override
    public void onArtist(int position) {
        ActivityArtistSong.launch(getContext(), listSongsArtist, list, position);
    }
}