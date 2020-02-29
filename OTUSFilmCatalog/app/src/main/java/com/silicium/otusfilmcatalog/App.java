package com.silicium.otusfilmcatalog;

import android.app.Application;
import android.content.res.Resources;

import androidx.annotation.NonNull;

import com.silicium.otusfilmcatalog.logic.controller.FilmDescriptionFromTMDBFetcher;
import com.silicium.otusfilmcatalog.logic.model.IFilmDescriptionStorage;
import com.yandex.mapkit.MapKitFactory;

import org.jetbrains.annotations.Contract;

public class App extends Application {
    private static Resources resources;
    private static Application application;
    private static IFilmDescriptionStorage storage;

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

    @Contract(pure = true)
    @NonNull
    public static IFilmDescriptionStorage getFilmDescriptionStorage() {
        return storage;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        resources = getResources();
        application = this;
        storage = FilmDescriptionFromTMDBFetcher.getInstance();
        //storage = FilmDescriptionStorage.getInstance();
        MapKitFactory.setApiKey(BuildConfig.YANDEX_MAPKIT_API_KEY);
    }
}