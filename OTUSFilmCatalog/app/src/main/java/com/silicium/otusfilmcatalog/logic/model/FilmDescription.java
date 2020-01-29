package com.silicium.otusfilmcatalog.logic.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.silicium.otusfilmcatalog.App;
import com.silicium.otusfilmcatalog.R;

import java.util.HashSet;
import java.util.Set;

public class FilmDescription {

    public enum FilmGenre {
        comedy,
        series,
        cartoon,
    }

    FilmDescription(@NonNull String ID, @NonNull IMetricNotifier metricNotifier)
    {
        this.ID = ID;
        this.metricNotifier = metricNotifier;
    }

    private final IMetricNotifier metricNotifier;

    @NonNull
    public final String ID;
    @NonNull
    public String Name = "";
    @NonNull
    public String Description = "";
    @NonNull
    public String Url = "https://www.kinopoisk.ru/error";
    @NonNull
    public Bitmap Cover = BitmapFactory.decodeResource(App.getAppResources(), R.drawable.film_no_image);
    @NonNull
    public Bitmap CoverPreview = BitmapFactory.decodeResource(App.getAppResources(), R.drawable.film_no_image);
    public final Set<FilmGenre> Genre = new HashSet<>();

    private boolean isFavorite;

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        if (favorite != isFavorite) {
            isFavorite = favorite;
            if (favorite)
                metricNotifier.increment(metricNotifier.FAVORITES_TAG);
            else
                metricNotifier.decrement(metricNotifier.FAVORITES_TAG);
        }
    }
}


