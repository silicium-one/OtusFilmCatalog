package com.silicium.otusfilmcatalog;

import android.app.Application;
import android.content.res.Resources;

import androidx.annotation.NonNull;

public class App extends Application {
    private static Resources resources;
    private static Application application;

    @NonNull
    public static Resources getAppResources() {
        return resources;
    }

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