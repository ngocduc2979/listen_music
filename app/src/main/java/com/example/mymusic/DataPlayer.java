package com.example.mymusic;

import com.example.mymusic.datamodel.Song;

import java.util.List;

public class DataPlayer {
    private static DataPlayer instance;

    private List<Song> playlist;

    private int playPosition = 0;

    private DataPlayer() {
    }

    public static DataPlayer getInstance() {
        if (instance == null) {
            instance = new DataPlayer();
        }

        return instance;
    }

    public void setPlaylist(List<Song> playlist) {
        this.playlist = playlist;
    }

    public void setPlayPosition(int position) {
        this.playPosition = position;
    }

    public Song getCurrentSong() {
        return playlist.get(playPosition);
    }

    public boolean hasNextSong() {
        return playPosition < playlist.size() - 1;
    }

    public Song getNextSong() {
        return playlist.get(playPosition + 1);
    }

    public boolean hasPreviousSong() {
        return playPosition > 0;
    }

    public Song getPreviousSong() {
        return playlist.get(playPosition - 1);
    }
}
