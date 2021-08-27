package com.example.mymusic.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {

    private String songName;
    private String artistName;
    private String urlSong;
    private String album;
    private String duration;

    public Song(String songName, String artistName, String urlSong, String album, String duration) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.songName);
        dest.writeString(this.artistName);
        dest.writeString(this.urlSong);
        dest.writeString(this.album);
        dest.writeString(this.duration);
    }

    public void readFromParcel(Parcel source) {
        this.songName = source.readString();
        this.artistName = source.readString();
        this.urlSong = source.readString();
        this.album = source.readString();
        this.duration = source.readString();
    }

    protected Song(Parcel in) {
        this.songName = in.readString();
        this.artistName = in.readString();
        this.urlSong = in.readString();
        this.album = in.readString();
        this.duration = in.readString();
    }

    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel source) {
            return new Song(source);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
}
