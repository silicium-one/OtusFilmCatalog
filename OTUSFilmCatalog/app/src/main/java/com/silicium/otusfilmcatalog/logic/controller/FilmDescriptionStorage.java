package com.silicium.otusfilmcatalog.logic.controller;

import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.silicium.otusfilmcatalog.App;
import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.model.FilmDescription;
import com.silicium.otusfilmcatalog.logic.model.FilmDescriptionFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FilmDescriptionStorage {
    @Nullable
    private static volatile FilmDescriptionStorage instance = null;
    @NonNull
    private final Map<String, FilmDescription> Films;

    private FilmDescriptionStorage() {
        super();
        Films = new HashMap<>();

        FilmDescription film1 = FilmDescriptionFactory.getFilmDescription("film1");
        film1.Name = "Детство Шелдона";
        film1.Description = App.getAppResources().getString(R.string.film1);
        film1.Url = "https://www.kinopoisk.ru/film/1040419/";
        film1.Cover = BitmapFactory.decodeResource(App.getAppResources(), R.drawable.film1);
        film1.CoverPreview = BitmapFactory.decodeResource(App.getAppResources(), R.drawable.film1);
        film1.Genre.add(FilmDescription.FilmGenre.comedy);
        film1.Genre.add(FilmDescription.FilmGenre.series);
        addFilm(film1);

        FilmDescription film2 = FilmDescriptionFactory.getFilmDescription("film2");
        film2.Name = "Теория большого взрыва";
        film2.Description = App.getAppResources().getString(R.string.film2);
        film2.Url = "https://www.kinopoisk.ru/film/306084/";
        film2.Cover = BitmapFactory.decodeResource(App.getAppResources(), R.drawable.film2);
        film2.CoverPreview = BitmapFactory.decodeResource(App.getAppResources(), R.drawable.film2);
        film2.Genre.add(FilmDescription.FilmGenre.comedy);
        film2.Genre.add(FilmDescription.FilmGenre.series);
        addFilm(film2);

        FilmDescription film3 = FilmDescriptionFactory.getFilmDescription("film3");
        film3.Name = "Кролик Багз или Дорожный Бегун";
        film3.Description = App.getAppResources().getString(R.string.film3);
        film3.Url = "https://www.kinopoisk.ru/film/33821/";
        film3.Cover = BitmapFactory.decodeResource(App.getAppResources(), R.drawable.film3);
        film3.CoverPreview = BitmapFactory.decodeResource(App.getAppResources(), R.drawable.film3);
        film3.Genre.add(FilmDescription.FilmGenre.comedy);
        film3.Genre.add(FilmDescription.FilmGenre.series);
        film3.Genre.add(FilmDescription.FilmGenre.cartoon);
        addFilm(film3);
    }

    @NonNull
    public static FilmDescriptionStorage getInstance() {
        if (instance == null)
            synchronized (FilmDescriptionStorage.class) {
                if (instance == null)
                    instance = new FilmDescriptionStorage();
            }
        //noinspection ConstantConditions
        return instance;
    }

    /**
     * Список ID фильмов
     *
     * @return список ключей доступных в базе фильмов
     */
    @NonNull
    public Collection<String> getFilmsIDs() {
        return Films.keySet();
    }

    /**
     * Список ID фильмов из избранного списка
     *
     * @return список ключей доступных в базе фильмов
     */
    @NonNull
    public Collection<String> getFavoriteFilmsIDs() {
        Collection<String> ret = new ArrayList<>();
        for (FilmDescription film : getFilms())
            if (film.isFavorite())
                ret.add(film.ID);
        return ret;
    }

    /**
     * Список фильмов
     *
     * @return список доступных в базе фильмов
     */
    @NonNull
    private Collection<FilmDescription> getFilms() {
        return Films.values();
    }

    public boolean containsID(@NonNull String ID) {
        return Films.containsKey(ID);
    }

    @NonNull
    public FilmDescription getFilmByID(@NonNull String ID) {
        try {
            FilmDescription ret = Films.get(ID);
            return ret == null ? FilmDescriptionFactory.getStubFilmDescription() : ret;
        } catch (Exception ex) {
            return FilmDescriptionFactory.getStubFilmDescription();
        }
    }

    public void addFilm(@NonNull FilmDescription film) { //TODO: подумать о том, что бы максимально вынести генерацию метрик в FilmDescription
        Films.put(film.ID, film);
        MetricsStorage.getMetricNotifier().increment(MetricsStorage.TOTAL_TAG);
        if (film.Genre.contains(FilmDescription.FilmGenre.cartoon))
            MetricsStorage.getMetricNotifier().increment(MetricsStorage.CARTOON_TAG);
    }
}

