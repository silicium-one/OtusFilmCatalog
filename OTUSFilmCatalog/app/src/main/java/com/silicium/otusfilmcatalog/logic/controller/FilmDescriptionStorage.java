package com.silicium.otusfilmcatalog.logic.controller;

import android.graphics.BitmapFactory;

import com.silicium.otusfilmcatalog.App;
import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.model.FilmDescription;
import com.silicium.otusfilmcatalog.logic.model.FilmDescriptionFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FilmDescriptionStorage {
    private Map<String, FilmDescription> Films;

    private static volatile FilmDescriptionStorage instance = null;
    public static FilmDescriptionStorage getInstance()
    {
        if (instance == null)
            synchronized (FilmDescriptionStorage.class) {
                if (instance == null)
                    instance = new FilmDescriptionStorage();
            }
        return instance;
    }
    private FilmDescriptionStorage() {
        super();
        Films = new HashMap<>();

        FilmDescription film1 = FilmDescriptionFactory.GetFilmDescription("film1");
        film1.Name = "Детство Шелдона";
        film1.Description = App.getAppResources().getString(R.string.film1);
        film1.Url = "https://www.kinopoisk.ru/film/1040419/";
        film1.Cover = BitmapFactory.decodeResource(App.getAppResources(),R.drawable.film1);

        FilmDescription film2 = FilmDescriptionFactory.GetFilmDescription("film2");
        film2.Name = "Теория большого взрыва";
        film2.Description = App.getAppResources().getString(R.string.film2);
        film2.Url = "https://www.kinopoisk.ru/film/306084/";
        film2.Cover = BitmapFactory.decodeResource(App.getAppResources(),R.drawable.film2);

        FilmDescription film3 = FilmDescriptionFactory.GetFilmDescription("film3");
        film3.Name = "Кролик Багз или Дорожный Бегун";
        film3.Description = App.getAppResources().getString(R.string.film3);
        film3.Url = "https://www.kinopoisk.ru/film/33821/";
        film3.Cover = BitmapFactory.decodeResource(App.getAppResources(),R.drawable.film3);

        Films.put(film1.ID, film1);
        Films.put(film2.ID, film2);
        Films.put(film3.ID, film3);
    }

    /**
     * Список ID фильмов
     * @return список ключей доступных в базе фильмов
     */
    public Collection<FilmDescription> getFilms()
    {
        return Films.values();
    }

    public boolean containsID(String ID)
    {
        return Films.containsKey(ID);
    }

    public FilmDescription GetFilmByID(String ID) {
        try {
            return Films.get(ID);
        }
        catch (Exception ex)
        {
            return FilmDescriptionFactory.GetStubFilmDescription();
        }
    }

    public void addFilm(FilmDescription film) {
        Films.put(film.ID, film);
    }
}
