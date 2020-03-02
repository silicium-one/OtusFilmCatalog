package com.silicium.otusfilmcatalog.logic.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Обработчик запросов вида https://maps.googleapis.com/maps/api/place/nearbysearch/json?language=ru&location=55.881951,37.5885577&rankby=distance&type=movie_theater
 */

public interface IGooglePlacesDiscoverMovieTheatersService {
    @NonNull
    @GET("json?language=ru&rankby=distance&type=movie_theater")
    Call<DiscoverPlacesResultJson> getCinemas(@Query("location") @NonNull String location, @Query("pagetoken") @Nullable String nextPageToken);
}
