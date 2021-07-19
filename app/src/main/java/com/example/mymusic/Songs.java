package com.example.mymusic;

public class Songs {

    private String songName;
    private String artistName;
    private String urlSong;
    private String album;
    private String duration;

    public Songs(String songName, String artistName, String urlSong, String album, String duration) {
        this.songName = songName;
        this.artistName = artistName;
        this.urlSong = urlSong;
        this.album = album;
        this.duration = duration;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getUrlSong() {
        return urlSong;
    }

    public void setUrlSong(String urlSong) {
        this.urlSong = urlSong;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
