package com.example.mymusic.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.mymusic.R;
import com.example.mymusic.adapter.AdapterViewPager;
import com.example.mymusic.fragment.FragmentArtist;
import com.example.mymusic.fragment.FragmentPlaylist;
import com.example.mymusic.fragment.FragmentSongs;
import com.google.android.material.tabs.TabLayout;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AdapterViewPager viewPagerAdapter;

    private List<Fragment> listFragment = new ArrayList<>();
    private List<String> listTittle = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Request Permission
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            requestPermission();
//        }

        initView();
        setAdapter();
    }

    private void setAdapter(){
//        TabLayoutAdapter tabLayoutAdapter = new TabLayoutAdapter(getSupportFragmentManager());
//        viewPager.setAdapter(tabLayoutAdapter);
//        tabLayout.setupWithViewPager(viewPager);

        Log.wtf("MainActivity", "setAdapter");

        viewPagerAdapter = new AdapterViewPager(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new FragmentSongs(), "Songs");
        viewPagerAdapter.addFragment(new FragmentArtist(), "Artists");
        viewPagerAdapter.addFragment(new FragmentPlaylist(), "Playlists");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPagerAdapter.notifyDataSetChanged();
    }

    private void initView(){
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
    }


//    private void requestPermission() {
//        PermissionListener permissionlistener = new PermissionListener() {
//            @Override
//            public void onPermissionGranted() {
//                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onPermissionDenied(List<String> deniedPermissions) {
//                Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
//            }
//        };
//        TedPermission.with(this)
//                .setPermissionListener(permissionlistener)
//                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
//                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                .check();
//    }
}