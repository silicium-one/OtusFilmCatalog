package com.silicium.otusfilmcatalog;

import android.app.Application;
import android.content.res.Resources;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

public class App extends Application {
    private static Resources resources;
    private static Application application;

    @Contract(pure = true)
    @NonNull
    public static Resources getAppResources() {
        return resources;
    }

    @Contract(pure = true)
    @NonNull
    public static Application getApplication() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        resources = getResources();
        application = this;
    }
}