package com.silicium.otusfilmcatalog.logic.model;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Обработчик запросов вида https://api.themoviedb.org/4/discover/movie?sort_by=popularity.desc&language=ru
 */
public interface ITMDBDiscoverMoviesService {
    @NonNull
    @GET("movie?sort_by=popularity.desc&language=ru")
    Call<DiscoverMoviesResultJson> getMovies(@Query("page") int page);
}
