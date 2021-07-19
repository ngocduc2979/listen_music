package com.example.mymusic;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.mymusic.PlaylistFragment;
import com.example.mymusic.ArtistsFragment;
import com.example.mymusic.SongsFragment;

public class TabLayoutAdapter extends FragmentStatePagerAdapter {


    public TabLayoutAdapter(FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new SongsFragment();
            case 1: return new ArtistsFragment();
            case 2: return new PlaylistFragment();
            default: return new SongsFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position){
            case 0: title = "Songs";
                    break;
            case 1: title = "Artists";
                    break;
            case 2: title = "Playlists";
                    break;
        }
        return title;
    }
}
