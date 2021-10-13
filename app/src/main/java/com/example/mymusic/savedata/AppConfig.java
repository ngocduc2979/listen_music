package com.example.mymusic.savedata;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mymusic.datamodel.Song;

public class AppConfig {
    private final String KEY_SHUFFLE = "shuffle";
    private final String KEY_REPEAT = "repeat";
    private final String KEY_STATE_PLAY = "state_play";
    private final String KEY_SONG_NAME = "song_name";
    private final String KEY_ARTIST_NAME = "song_artist";
    private final String KEY_SONG_PATH = "song_path";
    private final String KEY_CURPOSITION = "curposition";
    private final String KEY_IS_NEWPLAY = "isNewplay";
    private final String KEY_LIST = "List";

    private static AppConfig instance;
    private final SharedPreferences sharedPreferences;

    public static AppConfig getInstance(Context context) {
        if (instance == null) {
            instance = new AppConfig(context);
        }
        return instance;
    }

    private AppConfig(Context context) {
       sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
    }

    public void setShuffle(boolean isShuffle) {
        sharedPreferences.edit().putBoolean(KEY_SHUFFLE, isShuffle).apply();
    }

    public boolean isShuffle() {
        return sharedPreferences.getBoolean(KEY_SHUFFLE, false);
    }

    public void setRepeat(String repeat){
        sharedPreferences.edit().putString(KEY_REPEAT, repeat).apply();
    }

    public String repeat(){
        return sharedPreferences.getString(KEY_REPEAT, "no repeat");
    }

    public void setStatePlay(boolean isPlaying){
        sharedPreferences.edit().putBoolean(KEY_STATE_PLAY, isPlaying).apply();
    }

    public boolean getStatePlay(){
        return sharedPreferences.getBoolean(KEY_STATE_PLAY, false);
    }

    public void setCurrentSongName(Song song){
        sharedPreferences.edit().putString(KEY_SONG_NAME, song.getSongName()).apply();
    }

    public String getSongName(){
        return sharedPreferences.getString(KEY_SONG_NAME, null);
    }

    public void setCurrentSongArtist(Song song){
        sharedPreferences.edit().putString(KEY_ARTIST_NAME, song.getArtistName()).apply();
    }

    public String getSongArtist(){
        return sharedPreferences.getString(KEY_ARTIST_NAME, null);
    }

    public void setCurrentSongPath(Song song){
        sharedPreferences.edit().putString(KEY_SONG_PATH, song.getUrlSong()).apply();
    }

    public String getSongPath(){
        return sharedPreferences.getString(KEY_SONG_PATH, null);
    }

    public void setCurPosition(int position){
        sharedPreferences.edit().putInt(KEY_CURPOSITION, position).apply();
    }

    public int getCurposition(){
        return sharedPreferences.getInt(KEY_CURPOSITION, 0);
    }

    public void setIsNewPlay(boolean isIsNewPlay){
        sharedPreferences.edit().putBoolean(KEY_IS_NEWPLAY, isIsNewPlay).apply();
    }

    public boolean getIsNewPlay(){
        return sharedPreferences.getBoolean(KEY_IS_NEWPLAY, false);
    }

}
