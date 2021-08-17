package com.example.mymusic.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdapterViewPager extends FragmentPagerAdapter {
    private List<Fragment> listFragment = new ArrayList<>();
    private List<String> listTitle = new ArrayList<>();

    public AdapterViewPager(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment, String title){
        listFragment.add(fragment);
        listTitle.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return listFragment.get(position);
    }

    @Override
    public int getCount() {
        return listFragment.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return listTitle.get(position);
    }
}
