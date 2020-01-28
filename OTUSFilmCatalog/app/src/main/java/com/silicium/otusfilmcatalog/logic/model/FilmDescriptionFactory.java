package com.silicium.otusfilmcatalog.logic.model;

import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.silicium.otusfilmcatalog.App;
import com.silicium.otusfilmcatalog.R;

import java.util.UUID;

public class FilmDescriptionFactory {
    @NonNull
    public static FilmDescription GetNewFilmDescription ()
    {
        FilmDescription ret = new FilmDescription(UUID.randomUUID().toString());

        ret.Name = "";
        ret.Description = "";
        ret.Url = "https://www.kinopoisk.ru/error";
        ret.Cover = BitmapFactory.decodeResource(App.getAppResources(), R.drawable.film_no_image);

        return ret;
    }

    @NonNull
    public static FilmDescription GetFilmDescription (String ID)
    {
        FilmDescription ret = new FilmDescription(ID);

        ret.Name = "";
        ret.Description = "";
        ret.Url = "https://www.kinopoisk.ru/error";
        ret.Cover = BitmapFactory.decodeResource(App.getAppResources(), R.drawable.film_no_image);

        return ret;
    }

    @NonNull
    public static FilmDescription GetStubFilmDescription ()
    {
        FilmDescription ret = new FilmDescription("");

        ret.Name = "";
        ret.Description = "";
        ret.Url = "https://www.kinopoisk.ru/error";
        ret.Cover = BitmapFactory.decodeResource(App.getAppResources(), R.drawable.film_no_image);

        return ret;
    }

    //todo: добавить работу с разными локалями
    @NonNull
    public static String GetReadableGenre(@NonNull FilmDescription.FilmGenre genre)
    {
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
