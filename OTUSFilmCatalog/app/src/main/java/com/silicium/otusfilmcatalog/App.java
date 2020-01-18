package com.silicium.otusfilmcatalog;

import android.app.Application;
import android.content.res.Resources;

public class App extends Application {
    private static Resources resources;
    private static Application application;

    @Override
    public void onCreate() {
        super.onCreate();

        resources = getResources();
        application = this;
    }

    public static Resources getAppResources() {
        return resources;
    }

    public static Application getApplication() {
        return application;
    }
}