package com.example.mymusic.adapter;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mymusic.datamodel.Song;
import com.example.mymusic.OnAtistListener;
import com.example.mymusic.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterArtist extends RecyclerView.Adapter<AdapterArtist.ArtistViewHolder> {

    private List<Song> listArtists = new ArrayList();
    private Context context;
    private OnAtistListener onAtistListener;

    public AdapterArtist(List listArtists, Context context) {
        this.listArtists = listArtists;
        this.context = context;
    }

    public void setOnAtistListener(OnAtistListener onAtistListener) {
        this.onAtistListener = onAtistListener;
    }

    @Override
    public ArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artist, parent, false);

        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterArtist.ArtistViewHolder holder, int position) {

        holder.tvArtistName.setText(listArtists.get(position).getArtistName());

        byte[] albumArt = getAlbumArt(listArtists.get(position).getUrlSong());
        Log.w("art", listArtists.get(position).getUrlSong());

        Glide.with(context)
                .load(albumArt)
                .centerCrop()
                .placeholder(R.drawable.background_default_song)
                .into(holder.imvImageArtist);

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAtistListener != null){
                    onAtistListener.onArtist(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return listArtists.size();
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder{

        ImageView imvImageArtist;
        TextView tvArtistName;
        ConstraintLayout constraintLayout;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);

            imvImageArtist   = itemView.findViewById(R.id.image_artist);
            tvArtistName      = itemView.findViewById(R.id.artist_name);
            constraintLayout = itemView.findViewById(R.id.ConstraintLayout);
        }
    }

    //get Image
    private byte[] getAlbumArt(String path){
        MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
        mediaMetadata.setDataSource(path);
        return mediaMetadata.getEmbeddedPicture();
    }
}
