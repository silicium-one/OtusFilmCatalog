package com.silicium.otusfilmcatalog.logic.controller;

import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.silicium.otusfilmcatalog.App;
import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.model.ErrorResponse;
import com.silicium.otusfilmcatalog.logic.model.FilmDescription;
import com.silicium.otusfilmcatalog.logic.model.FilmDescriptionFactory;
import com.silicium.otusfilmcatalog.logic.model.IFilmDescriptionStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FilmDescriptionStorage implements IFilmDescriptionStorage {

    @Nullable
    private static volatile IFilmDescriptionStorage instance = null;
    @NonNull
    private final Map<String, FilmDescription> Films = new HashMap<>();
    private FilmDescriptionStorage() {
    }

    @NonNull
    public static IFilmDescriptionStorage getInstance() {
        if (instance == null)
            synchronized (FilmDescriptionStorage.class) {
                if (instance == null)
                    instance = new FilmDescriptionStorage();
            }
        //noinspection ConstantConditions
        return instance;
    }

    @Override
    public void getFilmsIDsNextPageAsync(@NonNull Consumer<Collection<String>> callback, @Nullable Consumer<ErrorResponse> errorResponse) {
        int total = Films.size();
        if (total >= getFilmsPerPage() * getTotalPages()) {
            if (errorResponse != null)
                errorResponse.accept(new ErrorResponse(R.string.limitExceed));

            return;
        }

        Collection<String> ret = new ArrayList<>(getFilmsPerPage());
        for (int i = 0; i < getFilmsPerPage(); i++) {
            switch (i % 3) {
                case 0:
                    FilmDescription film1 = FilmDescriptionFactory.getNewFilmDescription();
                    film1.name = "Детство Шелдона";
                    film1.description = App.getAppResources().getString(R.string.film1);
                    film1.url = "https://www.kinopoisk.ru/film/1040419/";
                    film1.cover = BitmapFactory.decodeResource(App.getAppResources(), R.drawable.film1);
                    film1.coverPreview = BitmapFactory.decodeResource(App.getAppResources(), R.drawable.film1);
                    film1.genres.add(FilmGenre.comedy.ordinal());
                    film1.genres.add(FilmGenre.series.ordinal());
                    ret.add(film1.ID);
                    addFilm(film1);
                    break;
                case 1:
                    FilmDescription film2 = FilmDescriptionFactory.getNewFilmDescription();
                    film2.name = "Теория большого взрыва";
                    film2.description = App.getAppResources().getString(R.string.film2);
                    film2.url = "https://www.kinopoisk.ru/film/306084/";
                    film2.cover = BitmapFactory.decodeResource(App.getAppResources(), R.drawable.film2);
                    film2.coverPreview = BitmapFactory.decodeResource(App.getAppResources(), R.drawable.film2);
                    film2.genres.add(FilmGenre.comedy.ordinal());
                    film2.genres.add(FilmGenre.series.ordinal());
                    ret.add(film2.ID);
                    addFilm(film2);
                    break;
                case 2:
                    FilmDescription film3 = FilmDescriptionFactory.getNewFilmDescription();
                    film3.name = "Кролик Багз или Дорожный Бегун";
                    film3.description = App.getAppResources().getString(R.string.film3);
                    film3.url = "https://www.kinopoisk.ru/film/33821/";
                    film3.cover = BitmapFactory.decodeResource(App.getAppResources(), R.drawable.film3);
                    film3.coverPreview = BitmapFactory.decodeResource(App.getAppResources(), R.drawable.film3);
                    film3.genres.add(FilmGenre.comedy.ordinal());
                    film3.genres.add(FilmGenre.series.ordinal());
                    film3.genres.add(FilmGenre.cartoon.ordinal());
                    ret.add(film3.ID);
                    addFilm(film3);
                    break;
            }
        }

        callback.accept(ret);
    }

    @NonNull
    @Override
    public Collection<String> getFilmsIDs() {
        return Films.keySet();
    }

    @NonNull
    @Override
    public Collection<String> getFavoriteFilmsIDs(boolean refreshActual) {
        if (!refreshActual)
            return getFavoriteFilmsIDs();

        Collection<String> ret = new ArrayList<>();
        for (String id : getFavoriteFilmsIDs())
            if (containsID(id))
                ret.add(id);

        return ret;
    }

    @NonNull
    @Override
    public Collection<String> getFavoriteFilmsIDs() {
        Collection<String> ret = new ArrayList<>();
        for (FilmDescription film : getFilms())
            if (film.isFavorite())
                ret.add(film.ID);
        return ret;
    }

    @Override
    public int getTotalPages() {
        return 5;
    }

    @Override
    public int getFilmsPerPage() {
        return 20;
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

    @Override
    @Deprecated
    public void addFilm(@NonNull FilmDescription film) { //TODO: подумать о том, что бы максимально вынести генерацию метрик в FilmDescription
        Films.put(film.ID, film);
        MetricsStorage.getMetricNotifier().increment(MetricsStorage.TOTAL_TAG);
        if (film.genres.contains(FilmGenre.cartoon.ordinal()))
            MetricsStorage.getMetricNotifier().increment(MetricsStorage.CARTOON_TAG);
    }

    @NonNull
    @Override
    public String getReadableGenre(int genreID) {
        if (genreID == FilmGenre.comedy.ordinal()) return "Комедия";
        else if (genreID == FilmGenre.series.ordinal()) return "Сериал";
        else if (genreID == FilmGenre.cartoon.ordinal()) return "Мультфильм";
        else return Integer.toString(genreID);
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

    public enum FilmGenre {
        comedy,
        series,
        cartoon,
    }

}

