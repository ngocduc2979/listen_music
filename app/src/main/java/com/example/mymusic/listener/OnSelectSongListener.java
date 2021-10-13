package com.example.mymusic.listener;

import com.example.mymusic.datamodel.Song;

import java.util.List;

public interface OnSelectSongListener {
    public void onPlaylist(List<Song> list);
}
