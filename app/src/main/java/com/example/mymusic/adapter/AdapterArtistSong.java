package com.example.mymusic.adapter;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mymusic.ObjectSong;
import com.example.mymusic.OnAtistListener;
import com.example.mymusic.R;

import java.util.List;

public class AdapterArtistSong extends RecyclerView.Adapter<AdapterArtistSong.SongViewHolder> {

    private List<ObjectSong> listArtistSong;
    private Context context;

    public AdapterArtistSong(List<ObjectSong> listArtistSong, Context context) {
        this.listArtistSong = listArtistSong;
        this.context = context;
    }

    public class SongViewHolder extends RecyclerView.ViewHolder{
        ImageView imvImageCover;
        TextView tvSongName;
        TextView tvSingerName;
        LinearLayout linearLayout;

        public SongViewHolder(View itemView) {
            super(itemView);
            imvImageCover   = itemView.findViewById(R.id.image_cover_id);
            tvSongName      = itemView.findViewById(R.id.song_name_id);
            tvSingerName    = itemView.findViewById(R.id.singer_name_id);
            linearLayout    = itemView.findViewById(R.id.layout_listView);
        }
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_song_listsong, parent, false);

        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterArtistSong.SongViewHolder holder, int position) {

        holder.tvSongName.setText(listArtistSong.get(position).getSongName());
        holder.tvSongName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.tvSongName.setSelected(true);

        holder.tvSingerName.setText(listArtistSong.get(position).getArtistName());

        byte[] albumArt = getAlbumArt(listArtistSong.get(position).getUrlSong());

        Glide.with(context)
                .load(albumArt)
                .centerCrop()
                .placeholder(R.drawable.image_cover)
                .into(holder.imvImageCover);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return listArtistSong.size();
    }

    //get Image
    private byte[] getAlbumArt(String path){
        MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
        mediaMetadata.setDataSource(path);
        return mediaMetadata.getEmbeddedPicture();
    }
}
