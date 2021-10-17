package com.example.mymusic.adapter;

import static com.example.mymusic.notification.NotificationChannelClass.CHANNEL_ID;

import android.app.Notification;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.commit451.nativestackblur.NativeStackBlur;
import com.example.mymusic.savedata.AppConfig;
import com.example.mymusic.datamodel.Song;
import com.example.mymusic.listener.OnMusicListener;
import com.example.mymusic.R;
import com.example.mymusic.savedata.DataPlayer;

import java.util.HashMap;
import java.util.List;

import wseemann.media.FFmpegMediaMetadataRetriever;

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

        Glide.with(context)
                .load(getAlbumArt(song.getUrlSong()))
//                .load(Uri.parse(song.getUrlSong()))
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
    private byte[] getAlbumArt(String path) {
        FFmpegMediaMetadataRetriever mediaMetadata = new FFmpegMediaMetadataRetriever ();

        try {
            mediaMetadata.setDataSource(path);
        } catch (Exception e) {
        }

        return mediaMetadata.getEmbeddedPicture();
    }
}
