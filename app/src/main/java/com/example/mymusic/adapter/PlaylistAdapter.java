package com.example.mymusic.adapter;

import static com.example.mymusic.fragment.FragmentPlaylist.KEY_LIST;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusic.listener.OnPlaylistListener;
import com.example.mymusic.database.PlaylistDatabase;
import com.example.mymusic.R;
import com.example.mymusic.savedata.TinyDB;

import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>{

    private ArrayList<String> listPlaylist = new ArrayList<>();
    private ArrayList<String> listResult = new ArrayList<>();
    private Context context;
    private SQLiteDatabase db;
    private PlaylistDatabase playlistDatabase;
    private List list = new ArrayList();
    private OnPlaylistListener onPlaylistListener;

    private Button btSave;
    private Button btCancel;
    private EditText edtPlaylistName;
    private TextView tvTitle;

    public void setOnPlaylistListener(OnPlaylistListener onPlaylistListener) {
        this.onPlaylistListener = onPlaylistListener;
    }

    public PlaylistAdapter(ArrayList<String> listPlaylist, Context context) {
        this.listPlaylist = listPlaylist;
        this.context = context;
    }

    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);
        return new PlaylistAdapter.PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaylistViewHolder holder, int position) {
        holder.tvPlaylistName.setText(listPlaylist.get(position).toString());
        String sizeTable = "SELECT * FROM " + "'" + listPlaylist.get(position).toString() + "'";

//        db = SQLiteDatabase.openOrCreateDatabase("playlist.db", null);
//        String size = db.rawQuery(sizeTable, null).toString();

        playlistDatabase = new PlaylistDatabase(context, "playlist.db", null, 1);
//        String size = playlistDatabase.getData(sizeTable).toString();

        list.clear();

        Cursor cursor = playlistDatabase.getData(sizeTable);
        while (cursor.moveToNext()){
            String name = cursor.getString(0);
            list.add(name);
        }

        holder.tvSizeList.setText(String.valueOf(list.size()) + " Bài hát");

        holder.layoutPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPlaylistListener != null){
                    onPlaylistListener.onPlaylist(position, listPlaylist);
                }
            }
        });

        holder.imvSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPupupMenu(view, position);

            }
        });
    }

    private void showPupupMenu(View view, int pos){
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.playlist_item_menu);
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.edit:
                        renamePlaylist(pos);
                        return true;
                    case R.id.delete:
                        deletePlaylist(pos);
                        return true;
                    default: return false;
                }
            }
        });
    }

    private void deletePlaylist(int pos){
        String tableName = listPlaylist.get(pos);

        String deleteTable = "DROP TABLE " + "'" + tableName + "'";
        playlistDatabase.querryData(deleteTable);

        TinyDB tinyDB = new TinyDB(context);
        listResult = tinyDB.getListString(KEY_LIST);
        listResult.remove(pos);
        tinyDB.putListString(KEY_LIST, listResult);
        Log.wtf("FragmentSong", "delete");
        listPlaylist = listResult;
        notifyDataSetChanged();
    }

    private void renamePlaylist(int pos){

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.create_playlist_dialog);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        btCancel        = dialog.findViewById(R.id.bt_cancel);
        btSave          = dialog.findViewById(R.id.bt_save);
        edtPlaylistName = dialog.findViewById(R.id.edt_playlist_name);
        tvTitle         = dialog.findViewById(R.id.tv_title);

        playlistDatabase = new PlaylistDatabase(context, "playlist.db", null, 1);
        String tableName = listPlaylist.get(pos);

        tvTitle.setText("Rename");
        edtPlaylistName.setText(tableName);

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rename = "ALTER TABLE " + "'" + tableName + "' " + "RENAME TO" + " '" + edtPlaylistName.getText().toString() + "';";
                playlistDatabase.querryData(rename);

                TinyDB tinyDB = new TinyDB(context);
                listPlaylist.remove(tableName);
                listPlaylist.add(edtPlaylistName.getText().toString());
                tinyDB.putListString(KEY_LIST, listPlaylist);
                dialog.dismiss();
                notifyDataSetChanged();
            }
        });

        dialog.show();

    }

    @Override
    public int getItemCount() {
        return listPlaylist.size();
    }


    public class PlaylistViewHolder extends RecyclerView.ViewHolder{

        TextView tvPlaylistName;
        TextView tvSizeList;
        ConstraintLayout layoutPlaylist;
        ImageView imvSetting;

        public PlaylistViewHolder(View itemView) {
            super(itemView);

            tvPlaylistName = itemView.findViewById(R.id.playlist_name);
            tvSizeList      = itemView.findViewById(R.id.size);
            layoutPlaylist  = itemView.findViewById(R.id.playlist);
            imvSetting      = itemView.findViewById(R.id.setting_playlist);
        }
    }
}
