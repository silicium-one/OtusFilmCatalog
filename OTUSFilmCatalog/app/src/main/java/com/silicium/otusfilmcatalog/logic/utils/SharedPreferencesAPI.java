package com.silicium.otusfilmcatalog.logic.utils;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.silicium.otusfilmcatalog.App;

import java.util.HashSet;
import java.util.Set;

public class SharedPreferencesAPI {

    private final static String PREFS_NAME = App.getApplication().getPackageName() + ".SharedPreferencesAPI";

    public static void saveStringsSet(@NonNull String key, @NonNull Set<String> set) {
        SharedPreferences.Editor prefs = App.getApplication().getApplicationContext().getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putStringSet(key, set);
        prefs.apply();
    }

    @NonNull
    public static Set<String> loadStringsSet(@NonNull String key) {
        SharedPreferences prefs = App.getApplication().getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
        return prefs.getStringSet(key, new HashSet<String>());
    }
}
