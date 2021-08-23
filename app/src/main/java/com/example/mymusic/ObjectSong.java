package com.example.mymusic;

import android.os.Parcel;
import android.os.Parcelable;

public class ObjectSong implements Parcelable {

    private String songName;
    private String artistName;
    private String urlSong;
    private String album;
    private String duration;

    public ObjectSong(String songName, String artistName, String urlSong, String album, String duration) {
        this.songName = songName;
        this.artistName = artistName;
        this.urlSong = urlSong;
        this.album = album;
        this.duration = duration;
    }

    public ObjectSong(Parcel input) {
        songName = input.readString();
        artistName = input.readString();
        urlSong = input.readString();
        album = input.readString();
        duration = input.readString();
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

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ObjectSong createFromParcel(Parcel input) {
            return new ObjectSong(input);
        }
        public ObjectSong[] newArray(int size) {
            return new ObjectSong[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(songName);
        dest.writeString(artistName);
        dest.writeString(urlSong);
        dest.writeString(album);
        dest.writeString(duration);
    }
}
