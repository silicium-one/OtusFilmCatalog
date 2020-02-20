package com.silicium.otusfilmcatalog.logic.model;

import androidx.annotation.NonNull;

import com.silicium.otusfilmcatalog.logic.controller.MetricsStorage;

import org.jetbrains.annotations.Contract;

import java.util.Locale;
import java.util.UUID;

public class FilmDescriptionFactory {
    @NonNull
    public static FilmDescription getFilmDescriptionFromTMDBJson(@NonNull MovieDescriptionJson movieDescriptionJson) {
        final FilmDescription ret = getFilmDescription(Integer.toString(movieDescriptionJson.id));

        ret.name = movieDescriptionJson.title;
        ret.description = movieDescriptionJson.overview;
        ret.url = String.format(Locale.getDefault(), "https://www.themoviedb.org/movie/%d?language=ru-RU", movieDescriptionJson.id);
        ret.genres.addAll(movieDescriptionJson.genreIds);

        ret.coverUrl = "http://image.tmdb.org/t/p/w1280" + movieDescriptionJson.posterPath;
        ret.coverPreviewUrl = "http://image.tmdb.org/t/p/w300" + movieDescriptionJson.posterPath;

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
