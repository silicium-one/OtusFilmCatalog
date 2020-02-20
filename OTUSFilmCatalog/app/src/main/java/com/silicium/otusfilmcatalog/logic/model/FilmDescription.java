package com.silicium.otusfilmcatalog.logic.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.silicium.otusfilmcatalog.App;
import com.silicium.otusfilmcatalog.R;

import java.util.HashSet;
import java.util.Set;

public class FilmDescription {

    @NonNull
    public final String ID;

    @NonNull
    public final Set<Integer> genres = new HashSet<>();

    @NonNull
    private final IMetricNotifier metricNotifier;

    @NonNull
    public String name = "";

    @NonNull
    public String description = "";

    @NonNull
    public String url = "https://www.themoviedb.org/movie/0";

    @NonNull
    public String coverUrl = "";

    @NonNull
    public Bitmap cover = BitmapFactory.decodeResource(App.getAppResources(), R.drawable.film_no_image);

    @NonNull
    public String coverPreviewUrl = "";

    @NonNull
    public Bitmap coverPreview = BitmapFactory.decodeResource(App.getAppResources(), R.drawable.film_no_image);

    public float popularity;

    public int voteCount;

    public float voteAverage;

    @NonNull
    public String releaseDate = ""; //todo: сделать датой

    private boolean isFavorite;

    FilmDescription(@NonNull String ID, @NonNull IMetricNotifier metricNotifier) {
        this.ID = ID;
        this.metricNotifier = metricNotifier;
    }

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
