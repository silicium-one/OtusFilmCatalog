package com.silicium.otusfilmcatalog.logic.controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.model.DiscoverMoviesResultJson;
import com.silicium.otusfilmcatalog.logic.model.ErrorResponse;
import com.silicium.otusfilmcatalog.logic.model.FilmDescription;
import com.silicium.otusfilmcatalog.logic.model.FilmDescriptionFactory;
import com.silicium.otusfilmcatalog.logic.model.IFilmDescriptionStorage;
import com.silicium.otusfilmcatalog.logic.model.ITMDBDiscoverMoviesService;
import com.silicium.otusfilmcatalog.logic.model.MovieDescriptionJson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ТОП 100 с themoviedb.org
 */
public class FilmDescriptionFromTMDBFetcher implements IFilmDescriptionStorage {
    @Nullable
    private static volatile IFilmDescriptionStorage instance = null;
    @NonNull
    private final Map<String, FilmDescription> Films = new HashMap<>();
    @NonNull
    private final ITMDBDiscoverMoviesService service;

    private FilmDescriptionFromTMDBFetcher() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @NonNull
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .addHeader("Content-Type", "application/json")
                                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI1ZGVhYTJlM2FjOGUxNGIwMjAxOTk4YmU3ZTVmY2ZiNSIsInN1YiI6IjVlM2IwZDdjMGMyNzEwMDAxYTc2YmZlMCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.j1T-lSL41AXfxUn2OWVxyjB88vTtuR9w0PJ3JgNtoxw")
                                .build();
                        return chain.proceed(request);
                    }
                })
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://api.themoviedb.org/4/discover/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(ITMDBDiscoverMoviesService.class);
    }

    @NonNull
    public static IFilmDescriptionStorage getInstance() {
        if (instance == null)
            synchronized (FilmDescriptionFromTMDBFetcher.class) {
                if (instance == null)
                    instance = new FilmDescriptionFromTMDBFetcher();
            }
        //noinspection ConstantConditions
        return instance;
    }

    /**
     * Количество страниц с фильмами
     *
     * @return сколько всего страниц с фильмами в базе
     */
    @Override
    public int getTotalPages() {
        return 5;
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
     * Список ID фильмов. Может вернуть пустую коллекцию. Также может вернуть количество фильмов,
     * отличное от {@link IFilmDescriptionStorage#getFilmsPerPage()}.
     *
     * @param callback      - будет вызвано, когда будет закончено получение данных с списком полученных ключей в качестве аргумента
     * @param errorResponse - будет вызвано, если возникли ошибки в получении фильмов с {@link ErrorResponse} в качестве аргумента
     *
     * Вызвано будет что-то одно
     */
    @Override
    public void getFilmsIDsNextPageAsync(@NonNull final Consumer<Collection<String>> callback, @Nullable final Consumer<ErrorResponse> errorResponse) {
        int total = Films.size();
        if (total >= getFilmsPerPage() * getTotalPages()) {
            if (errorResponse != null)
                errorResponse.accept(new ErrorResponse(R.string.limitExceed));
            return;
        }

        int page = total / getFilmsPerPage() + 1;

        service.getMovies(page).enqueue(new Callback<DiscoverMoviesResultJson>() {
            @Override
            public void onResponse(@NonNull Call<DiscoverMoviesResultJson> call, @NonNull retrofit2.Response<DiscoverMoviesResultJson> response) {
                if (!response.isSuccessful()) {
                    if (errorResponse != null)
                        errorResponse.accept(new ErrorResponse(R.string.httpRequestFail, response.code()));
                    return;
                }

                if (response.body() == null)  {
                    if (errorResponse != null)
                        errorResponse.accept(new ErrorResponse(R.string.httpResponseEmptyBody));
                    return;
                }

                Collection<String> ret = new ArrayList<>();
                for (MovieDescriptionJson movie : response.body().results) {
                    FilmDescription filmDescription = FilmDescriptionFactory.getFilmDescriptionFromTMDBJson(movie);
                    ret.add(filmDescription.ID);
                    addFilm(filmDescription);
                }

                callback.accept(ret);
            }

            @Override
            public void onFailure(@NonNull Call<DiscoverMoviesResultJson> call, @NonNull Throwable t) {
                if (errorResponse != null)
                    errorResponse.accept(new ErrorResponse(R.string.connectFailure, t));
            }
        });
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
        return new ArrayList<>();
    }

    /**
     * Список ID фильмов из избранного списка без обновления актуальности
     *
     * @return список ключей из списка "избранное"
     */
    @NonNull
    @Override
    public Collection<String> getFavoriteFilmsIDs() {
        return new ArrayList<>();
    }

    /**
     * Содержится ли данный ID в базе
     *
     * @param ID id фильма
     * @return ИСТИНА, если содержится
     */
    @Override
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
    @Override
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
    @Override
    public void addFilm(@NonNull FilmDescription film) {
        Films.put(film.ID, film);
        MetricsStorage.getMetricNotifier().increment(MetricsStorage.TOTAL_TAG);
        if (film.Genre.contains(FilmDescription.FilmGenre.cartoon))
            MetricsStorage.getMetricNotifier().increment(MetricsStorage.CARTOON_TAG);
    }
}