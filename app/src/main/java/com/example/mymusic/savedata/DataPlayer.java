package com.example.mymusic.savedata;

import com.example.mymusic.datamodel.Song;

import java.util.List;
import java.util.Random;

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

    public List<Song> getPlaylist(){
        return DataPlayer.getInstance().playlist;
    }

    public void setPlayPosition(int position) {
        this.playPosition = position;
    }

    public int getPlayPosition() {
        return playPosition;
    }

    public Song getCurrentSong() {
        return playlist.get(playPosition);
    }

    public boolean hasNextSong() {
        return playPosition < playlist.size() - 1;
    }

    public Song getNextSong() {
        playPosition = ((playPosition + 1) % playlist.size());

        return playlist.get(playPosition);
    }

    public int getNextPosition(){
        return playPosition = ((playPosition + 1) % playlist.size());
    }

    public Song getShuffeNextSong(){
        Random random = new Random();
        playPosition = random.nextInt(playlist.size());
        return  playlist.get(playPosition);
    }

    public int getShuffeNextPosition(){
        Random random = new Random();
        return  playPosition = random.nextInt(playlist.size());
    }

    public boolean hasPreviousSong() {
        return playPosition > 0;
    }

    public Song getPreviousSong() {
        playPosition--;

        return playlist.get(playPosition);
    }

    public int getPreviewPosition(){
        if (playPosition == 0){
            return playPosition = playlist.size() - 1;
        } else {
            return playPosition = playPosition - 1;
        }
    }
}
