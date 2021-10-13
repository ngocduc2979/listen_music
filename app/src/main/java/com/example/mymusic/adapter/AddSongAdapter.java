package com.example.mymusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusic.listener.OnAddSongListener;
import com.example.mymusic.R;
import com.example.mymusic.datamodel.Song;
import com.github.lguipeng.library.animcheckbox.AnimCheckBox;

import java.util.ArrayList;
import java.util.List;

public class AddSongAdapter extends RecyclerView.Adapter<AddSongAdapter.AddSongViewHolder> {

    private List<Song> listSong = new ArrayList();
    private List<Song> list = new ArrayList<>();
    private OnAddSongListener onAddSongListener;
    private Context context;

    public void setOnAddSongListener(OnAddSongListener onAddSongListener) {
        this.onAddSongListener = onAddSongListener;
    }

    public AddSongAdapter(List<Song> listSong, Context context) {
        this.listSong = listSong;
        this.context = context;
    }

    @NonNull
    @Override
    public AddSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_song, parent, false);
        return new AddSongViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull AddSongViewHolder holder, int position) {
        holder.tvSongName.setText(listSong.get(position).getSongName());
        holder.tvArtistName.setText(listSong.get(position).getArtistName());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.animCheckBox.isChecked()){
                    holder.animCheckBox.setChecked(false);
                    list.remove(listSong.get(position));
                } else {
                    holder.animCheckBox.setChecked(true);
                    list.add(listSong.get(position));
                }
            }
        });

        holder.animCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.animCheckBox.isChecked()){
                    list.remove(listSong.get(position));
                } else {
                    list.add(listSong.get(position));
                }
            }
        });


        onAddSongListener.onAddSong(list);
    }

    @Override
    public int getItemCount() {
        return listSong.size();
    }

    public class AddSongViewHolder extends RecyclerView.ViewHolder{
        private RelativeLayout relativeLayout;
        private TextView tvSongName;
        private TextView tvArtistName;
        private AnimCheckBox animCheckBox;

        public AddSongViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout          = itemView.findViewById(R.id.layout_listView);
            tvSongName              = itemView.findViewById(R.id.song_name);
            tvArtistName            = itemView.findViewById(R.id.artist_name);
            animCheckBox            = itemView.findViewById(R.id.checkbox);

        }
    }
}
