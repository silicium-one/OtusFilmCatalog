package com.silicium.otusfilmcatalog.logic.model;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.util.HashSet;
import java.util.Set;

public class FilmDescription {

    public enum FilmGenre {
        comedy,
        series,
        cartoon,
    }

    FilmDescription(String ID, @NonNull IMetricNotifier metricNotifier)
    {
        this.ID = ID;
        this.metricNotifier = metricNotifier;
    }

    private final IMetricNotifier metricNotifier;

    public final String ID;
    public String Name;
    public String Description;
    public String Url;
    public Bitmap Cover;
    public Bitmap CoverPreview;
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


