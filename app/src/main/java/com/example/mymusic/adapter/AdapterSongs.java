package com.example.mymusic.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mymusic.savedata.AppConfig;
import com.example.mymusic.datamodel.Song;
import com.example.mymusic.listener.OnMusicListener;
import com.example.mymusic.R;

import java.util.HashMap;
import java.util.List;

public class AdapterSongs extends RecyclerView.Adapter<AdapterSongs.MusicViewHolder> {
    private final String TAG = getClass().getSimpleName();

    private List<Song> listMusic;
    private Context context;
    private OnMusicListener onMusicListener;

    public void setOnMusicListener(OnMusicListener onMusicListener) {
        this.onMusicListener = onMusicListener;
    }

    public AdapterSongs(List<Song> listMusic, Context context) {
        this.listMusic = listMusic;
        this.context = context;
    }

    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_song_listsong, parent, false);

        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSongs.MusicViewHolder holder, int position) {
        Song song = listMusic.get(position);

        holder.tvSongName.setText(song.getSongName());
        holder.tvSongName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.tvSongName.setSelected(true);

        holder.tvSingerName.setText(song.getArtistName());

        byte[] albumArt = getAlbumArt(listMusic.get(position).getUrlSong());
        Log.w("art", listMusic.get(position).getUrlSong());

        Glide.with(context)
                .load(albumArt)
                .centerCrop()
                .placeholder(R.drawable.music_default_cover)
                .into(holder.imvImageCover);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onMusicListener != null){
                    onMusicListener.onMusic(position);
                    AppConfig.getInstance(view.getContext()).setIsNewPlay(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (listMusic != null){
            return listMusic.size();
        }
        return 0;
    }

    public class MusicViewHolder extends RecyclerView.ViewHolder{

        ImageView imvImageCover;
        TextView tvSongName;
        TextView tvSingerName;
        LinearLayout linearLayout;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);

            imvImageCover   = itemView.findViewById(R.id.image_cover_id);
            tvSongName      = itemView.findViewById(R.id.song_name_id);
            tvSingerName    = itemView.findViewById(R.id.singer_name_id);
            linearLayout    = itemView.findViewById(R.id.layout_listView);
        }
    }

    //get Image
    private byte[] getAlbumArt(String path){
        Log.wtf(TAG, "getAlbumArt: " + path);
        MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
        mediaMetadata.setDataSource(path);
        return mediaMetadata.getEmbeddedPicture();
    }
}
