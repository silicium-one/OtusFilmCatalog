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
import com.silicium.otusfilmcatalog.logic.utils.SharedPreferencesAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

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
    private Map<Integer, String> readableGenres = new Hashtable<>();

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

        // Обновляется редко, незачем каждый раз дёргать https://api.themoviedb.org/3/genre/movie/list?&language=ru
        readableGenres.put(28, "боевик");
        readableGenres.put(12, "приключения");
        readableGenres.put(16, "мультфильм");
        readableGenres.put(35, "комедия");
        readableGenres.put(80, "криминал");
        readableGenres.put(99, "документальный");
        readableGenres.put(18, "драма");
        readableGenres.put(10751, "семейный");
        readableGenres.put(14, "фэнтези");
        readableGenres.put(36, "история");
        readableGenres.put(27, "ужасы");
        readableGenres.put(10402, "музыка");
        readableGenres.put(9648, "детектив");
        readableGenres.put(10749, "мелодрама");
        readableGenres.put(878, "фантастика");
        readableGenres.put(10770, "телевизионный фильм");
        readableGenres.put(53, "триллер");
        readableGenres.put(10752, "военный");
        readableGenres.put(37, "вестерн");
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

    @Override
    public int getTotalPages() {
        return 5;
    }

    @Override
    public int getFilmsPerPage() {
        return 20;
    }

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

                if (response.body() == null) {
                    if (errorResponse != null)
                        errorResponse.accept(new ErrorResponse(R.string.httpResponseEmptyBody));
                    return;
                }

                Collection<String> favoriteCandidates = getFavoriteFilmsIDs();
                Collection<String> ret = new ArrayList<>();
                for (MovieDescriptionJson movie : response.body().results) {
                    FilmDescription filmDescription = FilmDescriptionFactory.getFilmDescriptionFromTMDBJson(movie);
                    if (favoriteCandidates.contains(filmDescription.ID))
                        filmDescription.setFavorite(true);

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

        Set<String> ret = new HashSet<>();
        for (FilmDescription film : Films.values())
            if (film.isFavorite())
                ret.add(film.ID);

        SharedPreferencesAPI.saveStringsSet("filmFavorites", ret);
        return ret;
    }

    @NonNull
    @Override
    public Collection<String> getFavoriteFilmsIDs() {
        return SharedPreferencesAPI.loadStringsSet("filmFavorites");
    }

    @Override
    public boolean containsID(@NonNull String ID) {
        return Films.containsKey(ID);
    }

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

    @Override
    @Deprecated
    public void addFilm(@NonNull FilmDescription film) {
        Films.put(film.ID, film);
        MetricsStorage.getMetricNotifier().increment(MetricsStorage.TOTAL_TAG);
        if (film.genres.contains(16))
            MetricsStorage.getMetricNotifier().increment(MetricsStorage.CARTOON_TAG);
    }

    @NonNull
    @Override
    public String getReadableGenre(int genreID) {
        String ret = readableGenres.containsKey(genreID) ? readableGenres.get(genreID) : Integer.toString(genreID);
        return ret == null ? "" : ret;
    }
}
