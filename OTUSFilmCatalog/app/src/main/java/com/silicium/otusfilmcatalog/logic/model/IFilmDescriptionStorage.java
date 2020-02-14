package com.silicium.otusfilmcatalog.logic.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import java.util.Collection;

public interface IFilmDescriptionStorage {

    /**
     * Количество страниц с фильмами
     *
     * @return сколько всего страниц с фильмами в базе
     */
    int getTotalPages();

    /**
     * Количество фильмов на одной странице (больше относится к списками из интернета, чем из базы)
     *
     * @return сколько фильмов в одном запросе
     */
    int getFilmsPerPage();

    /**
     * Список ID фильмов. Может вернуть пустую коллекцию. Также может вернуть количество фильмов,
     * отличное от {@link IFilmDescriptionStorage#getFilmsPerPage()}.
     *
     * @param callback      - будет вызвано, когда будет закончено получение данных с списком полученных ключей в качестве аргумента
     * @param errorResponse - будет вызвано, если возникли ошибки в получении фильмов с {@link ErrorResponse} в качестве аргумента
     * <p>
     * Вызвано будет что-то одно
     */
    void getFilmsIDsNextPageAsync(@NonNull Consumer<Collection<String>> callback, @Nullable Consumer<ErrorResponse> errorResponse);

    /**
     * Список ID фильмов из избранного списка
     * ВАЖНО: список ключей хранится отдельным списком, поэтому он будет актуализироваться только при обращении с флагом ИСТИНА
     *
     * @param refreshActual ИСТИНА, если нужно обновить список сообразно списку имеющихся в базе фильмов (МЕДЛЕННО!)
     * @return список ключей из списка "избранное"
     */
    @NonNull
    Collection<String> getFavoriteFilmsIDs(boolean refreshActual);

    /**
     * Список ID фильмов из избранного списка без обновления актуальности
     *
     * @return список ключей из списка "избранное"
     */
    @NonNull
    Collection<String> getFavoriteFilmsIDs();

    /**
     * Содержится ли данный ID в базе
     *
     * @param ID id фильма
     * @return ИСТИНА, если содержится
     */
    boolean containsID(@NonNull String ID);

    /**
     * Получить описание фильма по ID
     *
     * @param ID id фильма
     * @return данные, достаточные для вывода фильма на экран
     */
    @NonNull
    FilmDescription getFilmByID(@NonNull String ID);

    /**
     * Добавить фильм
     * Устарело, так как фильмы приходят с tmdb.org
     *
     * @param film Фильм
     */
    @Deprecated
    void addFilm(@NonNull FilmDescription film);
}
