package com.silicium.otusfilmcatalog.logic.model;

import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.silicium.otusfilmcatalog.App;
import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.controller.MetricsStorage;

import java.util.UUID;

public class FilmDescriptionFactory {
    @NonNull
    public static FilmDescription getNewFilmDescription() {
        FilmDescription ret = new FilmDescription(UUID.randomUUID().toString(), MetricsStorage.getMetricNotifier());

        ret.Name = "";
        ret.Description = "";
        ret.Url = "https://www.kinopoisk.ru/error";
        ret.Cover = BitmapFactory.decodeResource(App.getAppResources(), R.drawable.film_no_image);
        ret.CoverPreview = BitmapFactory.decodeResource(App.getAppResources(), R.drawable.film_no_image);

        return ret;
    }

    @NonNull
    public static FilmDescription getFilmDescription(String ID) {
        FilmDescription ret = new FilmDescription(ID, MetricsStorage.getMetricNotifier());

        ret.Name = "";
        ret.Description = "";
        ret.Url = "https://www.kinopoisk.ru/error";
        ret.Cover = BitmapFactory.decodeResource(App.getAppResources(), R.drawable.film_no_image);
        ret.CoverPreview = BitmapFactory.decodeResource(App.getAppResources(), R.drawable.film_no_image);

        return ret;
    }

    @NonNull
    public static FilmDescription getStubFilmDescription() {
        FilmDescription ret = new FilmDescription("", MetricsStorage.getMetricNotifier());

        ret.Name = "";
        ret.Description = "";
        ret.Url = "https://www.kinopoisk.ru/error";
        ret.Cover = BitmapFactory.decodeResource(App.getAppResources(), R.drawable.film_no_image);
        ret.CoverPreview = BitmapFactory.decodeResource(App.getAppResources(), R.drawable.film_no_image);

        return ret;
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
