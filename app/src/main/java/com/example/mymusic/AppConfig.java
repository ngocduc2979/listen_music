package com.example.mymusic;

import android.content.Context;
import android.content.SharedPreferences;

public class AppConfig {
    private final String KEY_SHUFFLE = "shuffle";

    private static AppConfig instance;
    private final SharedPreferences sharedPreferences;

    public AppConfig getInstance(Context context) {
        if (instance == null) {
            instance = new AppConfig(context);
        }

        return instance;
    }

    private AppConfig(Context context) {
       sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
    }

    public void setShuffle(boolean isShuffle) {
        sharedPreferences.edit().putBoolean(KEY_SHUFFLE, isShuffle).apply();
    }

    public boolean isShuffle() {
        return sharedPreferences.getBoolean(KEY_SHUFFLE, false);
    }
}
