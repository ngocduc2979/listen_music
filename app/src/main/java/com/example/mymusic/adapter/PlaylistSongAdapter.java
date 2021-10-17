package com.example.mymusic.adapter;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mymusic.listener.OnPlaylistSongListener;
import com.example.mymusic.R;
import com.example.mymusic.datamodel.Song;
import com.example.mymusic.savedata.AppConfig;

import java.util.ArrayList;
import java.util.List;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class PlaylistSongAdapter extends RecyclerView.Adapter<PlaylistSongAdapter.ViewHolder> {
    private List<Song> playlistSong = new ArrayList<>();
    private Context context;
    private OnPlaylistSongListener onPlaylistSongListener;

    public void setOnPlaylistSongListener(OnPlaylistSongListener onPlaylistSongListener) {
        this.onPlaylistSongListener = onPlaylistSongListener;
    }

    public PlaylistSongAdapter(List<Song> playlistSong, Context context) {
        this.playlistSong = playlistSong;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_song_listsong, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvSongName.setText(playlistSong.get(position).getSongName());
        holder.tvArtistName.setText(playlistSong.get(position).getArtistName());

        Glide.with(context)
                .load(getAlbumArt(playlistSong.get(position).getUrlSong()))
                .centerCrop()
                .placeholder(R.drawable.music_default_cover)
                .into(holder.imvAlbum);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPlaylistSongListener != null){
                    onPlaylistSongListener.onPlaylistSong(position);
                    AppConfig.getInstance(view.getContext()).setIsNewPlay(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlistSong.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvSongName;
        private final TextView tvArtistName;
        private final ImageView imvAlbum;
        private final LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSongName      = itemView.findViewById(R.id.song_name_id);
            tvArtistName    = itemView.findViewById(R.id.singer_name_id);
            imvAlbum        = itemView.findViewById(R.id.image_cover_id);
            linearLayout    = itemView.findViewById(R.id.layout_listView);
        }
    }

    private byte[] getAlbumArt(String path) {
        FFmpegMediaMetadataRetriever mediaMetadata = new FFmpegMediaMetadataRetriever ();

        try {
            mediaMetadata.setDataSource(path);
        } catch (Exception e) {
        }

        return mediaMetadata.getEmbeddedPicture();
    }
}
