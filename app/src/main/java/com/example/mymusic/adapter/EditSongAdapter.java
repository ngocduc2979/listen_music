package com.example.mymusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusic.listener.OnEditSongListener;
import com.example.mymusic.R;
import com.example.mymusic.datamodel.Song;
import com.github.lguipeng.library.animcheckbox.AnimCheckBox;

import java.util.ArrayList;
import java.util.List;

public class EditSongAdapter extends RecyclerView.Adapter<EditSongAdapter.EditSongViewHolder> {

    private List<Song> listEditSong = new ArrayList();
    private List<Song> list = new ArrayList<>();
    private Context context;
    private OnEditSongListener onEditSongListener;

    public void setOnEditSongListener (OnEditSongListener onEditSongListener){
        this.onEditSongListener = onEditSongListener;
    }

    public EditSongAdapter(List<Song> listEditSong, Context context) {
        this.listEditSong = listEditSong;
        this.context = context;
    }

    @NonNull
    @Override
    public EditSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_song, parent, false);
        return new EditSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EditSongViewHolder holder, int position) {
        holder.tvSongName.setText(listEditSong.get(position).getSongName());
        holder.tvArtistName.setText(listEditSong.get(position).getArtistName());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.animCheckBox.isChecked()){
                    holder.animCheckBox.setChecked(false);
                    list.remove(listEditSong.get(position));
                } else {
                    holder.animCheckBox.setChecked(true);
                    list.add(listEditSong.get(position));
                }
            }
        });
        onEditSongListener.onEditSong(list);
    }

    @Override
    public int getItemCount() {
        return listEditSong.size();
    }

    public class EditSongViewHolder extends RecyclerView.ViewHolder{
        private RelativeLayout relativeLayout;
        private TextView tvSongName;
        private TextView tvArtistName;
        private AnimCheckBox animCheckBox;

        public EditSongViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout          = itemView.findViewById(R.id.layout_listView);
            tvSongName              = itemView.findViewById(R.id.song_name);
            tvArtistName            = itemView.findViewById(R.id.artist_name);
            animCheckBox            = itemView.findViewById(R.id.checkbox);
        }
    }
}
