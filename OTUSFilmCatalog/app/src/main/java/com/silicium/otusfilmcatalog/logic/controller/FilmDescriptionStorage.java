package com.silicium.otusfilmcatalog.logic.controller;

import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.silicium.otusfilmcatalog.App;
import com.silicium.otusfilmcatalog.R;
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

    /**
     * Список ID фильмов. Может вернуть пустую коллекцию, если страница задана неверно или нет связи с интернетом
     * Также может вернуть количество фильмов, отличное от {@link IFilmDescriptionStorage#getFilmsPerPage()}
     *
     * @param callback - вызвать, когда будет закончено получение данных с списком полученных ключей в качестве аргумента
     */
    @Override
    public void getFilmsIDsNextPageAsync(@NonNull Consumer<Collection<String>> callback) {
        Collection<String> ret = new ArrayList<>(getFilmsPerPage());
        for (int i = 0; i < getFilmsPerPage(); i++) {
            switch (i % 3) {
                case 0:
                    FilmDescription film1 = FilmDescriptionFactory.getNewFilmDescription();
                    film1.Name = "Детство Шелдона";
                    film1.Description = App.getAppResources().getString(R.string.film1);
                    film1.Url = "https://www.kinopoisk.ru/film/1040419/";
                    film1.Cover = BitmapFactory.decodeResource(App.getAppResources(), R.drawable.film1);
                    film1.CoverPreview = BitmapFactory.decodeResource(App.getAppResources(), R.drawable.film1);
                    film1.Genre.add(FilmDescription.FilmGenre.comedy);
                    film1.Genre.add(FilmDescription.FilmGenre.series);
                    ret.add(film1.ID);
                    addFilm(film1);
                    break;
                case 1:
                    FilmDescription film2 = FilmDescriptionFactory.getNewFilmDescription();
                    film2.Name = "Теория большого взрыва";
                    film2.Description = App.getAppResources().getString(R.string.film2);
                    film2.Url = "https://www.kinopoisk.ru/film/306084/";
                    film2.Cover = BitmapFactory.decodeResource(App.getAppResources(), R.drawable.film2);
                    film2.CoverPreview = BitmapFactory.decodeResource(App.getAppResources(), R.drawable.film2);
                    film2.Genre.add(FilmDescription.FilmGenre.comedy);
                    film2.Genre.add(FilmDescription.FilmGenre.series);
                    ret.add(film2.ID);
                    addFilm(film2);
                    break;
                case 2:
                    FilmDescription film3 = FilmDescriptionFactory.getNewFilmDescription();
                    film3.Name = "Кролик Багз или Дорожный Бегун";
                    film3.Description = App.getAppResources().getString(R.string.film3);
                    film3.Url = "https://www.kinopoisk.ru/film/33821/";
                    film3.Cover = BitmapFactory.decodeResource(App.getAppResources(), R.drawable.film3);
                    film3.CoverPreview = BitmapFactory.decodeResource(App.getAppResources(), R.drawable.film3);
                    film3.Genre.add(FilmDescription.FilmGenre.comedy);
                    film3.Genre.add(FilmDescription.FilmGenre.series);
                    film3.Genre.add(FilmDescription.FilmGenre.cartoon);
                    ret.add(film3.ID);
                    addFilm(film3);
                    break;
            }
        }

        callback.accept(ret);
    }

    /**
     * Список ID фильмов из избранного списка
     * ВАЖНО: список ключей хранится отдельным списком, поэтому он будет актуализироваться только при обращении с флагом ИСТИНА
     *
     * @param refreshActual ИСТИНА, если нужно обновить список сообразно списку имеющихся в базе фильмов (МЕДЛЕННО!)
     * @return список ключей из списка "избранное"
     */
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

    /**
     * Список ID фильмов из избранного списка без обновления актуальности
     *
     * @return список ключей из списка "избранное"
     */
    @NonNull
    @Override
    public Collection<String> getFavoriteFilmsIDs() {
        Collection<String> ret = new ArrayList<>();
        for (FilmDescription film : getFilms())
            if (film.isFavorite())
                ret.add(film.ID);
        return ret;
    }

    /**
     * Количество страниц с фильмами
     *
     * @return сколько всего страниц с фильмами в базе
     */
    @Override
    public int getTotalPages() {
        float ret = Films.size() / getFilmsPerPage();
        if (ret == (int) ret)
            return (int) ret;
        else
            return (int) ret + 1;
    }

    /**
     * Количество фильмов на одной странице (больше относится к списками из интернета, чем из базы)
     *
     * @return сколько фильмов в одном запросе
     */
    @Override
    public int getFilmsPerPage() {
        return 20;
    }

    /**
     * Содержится ли данный ID в базе
     *
     * @param ID id фильма
     * @return ИСТИНА, если содержится
     */
    public boolean containsID(@NonNull String ID) {
        return Films.containsKey(ID);
    }

    /**
     * Получить описание фильма по ID
     *
     * @param ID id фильма
     * @return данные, достаточные для вывода фильма на экран
     */
    @NonNull
    public FilmDescription getFilmByID(@NonNull String ID) {
        try {
            FilmDescription ret = Films.get(ID);
            return ret == null ? FilmDescriptionFactory.getStubFilmDescription() : ret;
        } catch (Exception ex) {
            return FilmDescriptionFactory.getStubFilmDescription();
        }
    }

    /**
     * Добавить фильм
     * Устарело, так как фильмы приходят с tmdb.org
     *
     * @param film Фильм
     */
    @Deprecated
    public void addFilm(@NonNull FilmDescription film) { //TODO: подумать о том, что бы максимально вынести генерацию метрик в FilmDescription
        Films.put(film.ID, film);
        MetricsStorage.getMetricNotifier().increment(MetricsStorage.TOTAL_TAG);
        if (film.Genre.contains(FilmDescription.FilmGenre.cartoon))
            MetricsStorage.getMetricNotifier().increment(MetricsStorage.CARTOON_TAG);
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

}

