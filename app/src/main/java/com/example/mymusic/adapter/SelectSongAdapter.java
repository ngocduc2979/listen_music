package com.example.mymusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusic.listener.OnSelectSongListener;
import com.example.mymusic.R;
import com.example.mymusic.datamodel.Song;
import com.github.lguipeng.library.animcheckbox.AnimCheckBox;

import java.util.ArrayList;
import java.util.List;

public class SelectSongAdapter extends RecyclerView.Adapter<SelectSongAdapter.SelectSongViewHolder> {

    private List<Song> selectSongList = new ArrayList<>();
    private List<Song> list = new ArrayList<>();
    private Context context;
    private OnSelectSongListener onSelectSongListener;

    public void setOnPlaylistListener(OnSelectSongListener onSelectSongListener) {
        this.onSelectSongListener = onSelectSongListener;
    }

    public SelectSongAdapter(List<Song> selectSongList, Context context) {
        this.selectSongList = selectSongList;
        this.context = context;
    }

    @Override
    public SelectSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_song, parent, false);
        return new SelectSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectSongViewHolder holder, int position) {
//        holder.tvSongName.setText(selectSongList.get(position).getSongName());
//        holder.tvArtsistName.setText(selectSongList.get(position).getArtistName());
//        holder.checkBox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (onSelectSongListener != null){
//                    if (holder.checkBox.isChecked()){
//                        list.add(selectSongList.get(position));
//                    } else {
//                        list.remove(selectSongList.get(position));
//                    }
//                    onSelectSongListener.onPlaylist(list);
//                }
//            }
//        });

        holder.tvSongName.setText(selectSongList.get(position).getSongName());
        holder.tvArtistName.setText(selectSongList.get(position).getArtistName());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.animCheckBox.isChecked()){
                    holder.animCheckBox.setChecked(false);
                    list.remove(selectSongList.get(position));
                } else {
                    holder.animCheckBox.setChecked(true);
                    list.add(selectSongList.get(position));
                }
            }
        });

        onSelectSongListener.onPlaylist(list);

    }

    @Override
    public int getItemCount() {
        return selectSongList.size();
    }

    public class SelectSongViewHolder extends RecyclerView.ViewHolder{

        private RelativeLayout relativeLayout;
        private TextView tvSongName;
        private TextView tvArtistName;
        private AnimCheckBox animCheckBox;

        public SelectSongViewHolder(View itemView) {
            super(itemView);
            relativeLayout          = itemView.findViewById(R.id.layout_listView);
            tvSongName              = itemView.findViewById(R.id.song_name);
            tvArtistName            = itemView.findViewById(R.id.artist_name);
            animCheckBox            = itemView.findViewById(R.id.checkbox);
        }
    }
}
