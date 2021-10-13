package com.example.mymusic.fragment;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymusic.listener.OnPlaylistListener;
import com.example.mymusic.database.PlaylistDatabase;
import com.example.mymusic.R;
import com.example.mymusic.activity.SelectSong;
import com.example.mymusic.activity.PlaylistSongActivity;
import com.example.mymusic.savedata.TinyDB;
import com.example.mymusic.adapter.PlaylistAdapter;

import java.util.ArrayList;


public class FragmentPlaylist extends Fragment implements OnPlaylistListener {

    private TextView tvTotalPlaylist;
    private TextView tvEdit;
    private LinearLayout layoutEdit;
    private LinearLayout layoutCreate;
    private Button btSave;
    private Button btCancel;
    private EditText edtPlaylistName;
    private TextView tvTitle;
    private ArrayList<String> listPlaylist = new ArrayList();

    private PlaylistDatabase playlistDatabase;
    private RecyclerView recyclerView;
    private PlaylistAdapter playlistAdapter;

    public static final String KEY_LIST = "list";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_albums, container, false);
        Log.wtf("FragmentPlaylis", "Oncreate view");
        initView(view);
        loadList();
        setAdapter();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.wtf("FragmentPlaylis", "Oncreate resume");
        loadList();
        setAdapter();
    }

    private void loadList(){
        TinyDB tinyDB = new TinyDB(getContext());
        listPlaylist = tinyDB.getListString(KEY_LIST);
        tvTotalPlaylist.setText(String.valueOf(listPlaylist.size()));

//        listPlaylist.remove("a s");
//        tinyDB.putListString(KEY_LIST, listPlaylist);
    }

    private void setAdapter(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        playlistAdapter = new PlaylistAdapter(listPlaylist, getContext());
        playlistAdapter.setOnPlaylistListener(this);
        recyclerView.setAdapter(playlistAdapter);
        playlistAdapter.notifyDataSetChanged();
    }

    private void initView(View view) {
        tvTotalPlaylist = view.findViewById(R.id.size_playlist);
        tvEdit          = view.findViewById(R.id.tv_edit);
        layoutCreate    = view.findViewById(R.id.layout_add);
        layoutEdit      = view.findViewById(R.id.layout_edit);
        recyclerView    = view.findViewById(R.id.list_playlist);
        tvTitle         = view.findViewById(R.id.tv_title);


        playlistDatabase = new PlaylistDatabase(getContext(), "playlist.db", null, 1);
//        db = SQLiteDatabase.openOrCreateDatabase("playlist.db", null);


        layoutCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(view.getContext());
                dialog.setContentView(R.layout.create_playlist_dialog);
                dialog.setCancelable(true);
                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                btCancel        = dialog.findViewById(R.id.bt_cancel);
                btSave          = dialog.findViewById(R.id.bt_save);
                edtPlaylistName = dialog.findViewById(R.id.edt_playlist_name);
                tvTitle         = dialog.findViewById(R.id.tv_title);

                tvTitle.setText("Create new playlist");

                btCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                btSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        createTable(dialog);
                    }
                });

                dialog.show();
            }
        });
    }



    public void createTable(Dialog dialog){
        TinyDB tinyDB = new TinyDB(getContext());
        listPlaylist = tinyDB.getListString(KEY_LIST);

        String tableName = edtPlaylistName.getText().toString();

        boolean checkExist = true;

        for (int i = 0; i < listPlaylist.size(); i++){
            if (tableName.equals(listPlaylist.get(i).toString())){
                checkExist = false;
            }
        }

        if (checkExist){
            String sql = "CREATE TABLE IF NOT EXISTS " + "'" + tableName + "' "+
                    "(songName text PRIMARY KEY, artistName text, urlSong text, album text, duration text)";
            playlistDatabase.querryData(sql);

            listPlaylist.add(tableName);
            tinyDB.putListString(KEY_LIST, listPlaylist);
            SelectSong.lunch(getContext(), edtPlaylistName.getText().toString());
            dialog.dismiss();
        } else {
            Toast.makeText(getContext(),"Playlist Already exist", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onPlaylist(int position, ArrayList<String> listSong) {
        PlaylistSongActivity.launch(getContext(), position, listPlaylist);
    }
}