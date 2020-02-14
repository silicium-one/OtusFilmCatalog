package com.silicium.otusfilmcatalog.logic.model;

import androidx.annotation.NonNull;

import com.silicium.otusfilmcatalog.logic.controller.MetricsStorage;

import org.jetbrains.annotations.Contract;

import java.util.UUID;

public class FilmDescriptionFactory {
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

    //todo: добавить работу с разными локалями
    @NonNull
    public static String getReadableGenre(@NonNull FilmDescription.FilmGenre genre) {
        switch (genre) {
            case comedy:
                return "Комедия";
            case series:
                return "Сериал";
            case cartoon:
                return "Мультфильм";
            default:
                throw new UnsupportedOperationException("Неизвестный жанр");
        }
    }
}
