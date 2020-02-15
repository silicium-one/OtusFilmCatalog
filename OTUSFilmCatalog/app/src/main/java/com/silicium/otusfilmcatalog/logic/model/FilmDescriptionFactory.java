package com.silicium.otusfilmcatalog.logic.model;

import androidx.annotation.NonNull;

import com.silicium.otusfilmcatalog.logic.controller.MetricsStorage;

import org.jetbrains.annotations.Contract;

import java.util.Locale;
import java.util.UUID;

public class FilmDescriptionFactory {
    @NonNull
    public static FilmDescription getFilmDescriptionFromTMDBJson(@NonNull MovieDescriptionJson movieDescriptionJson) {
        FilmDescription ret = getFilmDescription(Integer.toString(movieDescriptionJson.id));

        ret.Name = movieDescriptionJson.title;
        ret.Description = movieDescriptionJson.overview;
        ret.Url = String.format(Locale.getDefault(), "https://www.themoviedb.org/movie/%d?language=ru-RU", movieDescriptionJson.id);
        ret.Genre.addAll(movieDescriptionJson.genreIds);

        return ret;
    }

    @NonNull
    public static FilmDescription getNewFilmDescription() {
        return getFilmDescription(UUID.randomUUID().toString());
    }

    @Contract("_ -> new")
    @NonNull
    private static FilmDescription getFilmDescription(@NonNull String ID) {
        return new FilmDescription(ID, MetricsStorage.getMetricNotifier());
    }

    @Contract(" -> new")
    @NonNull
    public static FilmDescription getStubFilmDescription() {
        return getFilmDescription("");
    }
}
