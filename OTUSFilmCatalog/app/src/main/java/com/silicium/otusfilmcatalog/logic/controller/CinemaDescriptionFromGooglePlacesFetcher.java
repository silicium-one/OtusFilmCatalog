package com.silicium.otusfilmcatalog.logic.controller;

import android.annotation.SuppressLint;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.silicium.otusfilmcatalog.App;
import com.silicium.otusfilmcatalog.BuildConfig;
import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.model.CinemaDescription;
import com.silicium.otusfilmcatalog.logic.model.CinemaDescriptionFactory;
import com.silicium.otusfilmcatalog.logic.model.DiscoverPlacesResultJson;
import com.silicium.otusfilmcatalog.logic.model.ErrorResponse;
import com.silicium.otusfilmcatalog.logic.model.IGooglePlacesDiscoverMovieTheatersService;
import com.silicium.otusfilmcatalog.logic.model.PlaceDescriptionJson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CinemaDescriptionFromGooglePlacesFetcher {
    /**
     * Результат поиска отсортирован по удалению от заданного места,
     * поэтому введено ограничение по количеству кинотетаров. Ограничение
     * действует по принципу "не менее чем". Исключение составляют ситуации,
     * когда сервис не возвращает признак следующей страницы
     */
    private static final int MAX_CINEMAS_COUNT = 10;

    @Nullable
    private static volatile CinemaDescriptionFromGooglePlacesFetcher instance = null;

    @NonNull
    private final IGooglePlacesDiscoverMovieTheatersService service;

    private CinemaDescriptionFromGooglePlacesFetcher() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @NonNull
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {

                        HttpUrl urlWithApiKey = chain.request().url().newBuilder()
                                .addQueryParameter("key", BuildConfig.GOOGLE_PLACES_API_KEY)
                                .build();

                        Request request = chain.request().newBuilder()
                                .url(urlWithApiKey)
                                .addHeader("Content-Type", "application/json")
                                .build();
                        return chain.proceed(request);
                    }
                })
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(IGooglePlacesDiscoverMovieTheatersService.class);
    }

    @NonNull
    public static CinemaDescriptionFromGooglePlacesFetcher getInstance() {
        if (instance == null)
            synchronized (FilmDescriptionFromTMDBFetcher.class) {
                if (instance == null)
                    instance = new CinemaDescriptionFromGooglePlacesFetcher();
            }
        //noinspection ConstantConditions
        return instance;
    }

    public void getCinemasAsync(double latitude, double longitude, @NonNull final Consumer<Collection<CinemaDescription>> callback, @Nullable final Consumer<ErrorResponse> errorResponse) {
        Collection<CinemaDescription> ret = new ArrayList<>();
        String location = String.format(Locale.ENGLISH, "%.3f,%.3f", latitude, longitude);
        getCinemasAsync(location, callback, errorResponse, null, ret);
    }

    private void getCinemasAsync(@NonNull final String location, @NonNull final Consumer<Collection<CinemaDescription>> callback, @Nullable final Consumer<ErrorResponse> errorResponse,
                                 @Nullable final String nextPageToken, @NonNull final Collection<CinemaDescription> alreadyFetchedCinemas) {
        final Callback<DiscoverPlacesResultJson> serviceCallback = new Callback<DiscoverPlacesResultJson>() {
            @SuppressLint("SyntheticAccessor")
            @Override
            public void onResponse(@NonNull Call<DiscoverPlacesResultJson> call, @NonNull final retrofit2.Response<DiscoverPlacesResultJson> response) {
                if (!response.isSuccessful()) {
                    if (alreadyFetchedCinemas.size() > 0)
                        callback.accept(alreadyFetchedCinemas);
                    if (errorResponse != null)
                        errorResponse.accept(new ErrorResponse(R.string.httpRequestFail, response.code()));
                    return;
                }

                if (response.body() == null) {
                    if (alreadyFetchedCinemas.size() > 0)
                        callback.accept(alreadyFetchedCinemas);
                    if (errorResponse != null)
                        errorResponse.accept(new ErrorResponse(R.string.httpResponseEmptyBody));
                    return;
                }

                if (!response.body().status.equals("OK")) {
                    if (alreadyFetchedCinemas.size() > 0)
                        callback.accept(alreadyFetchedCinemas);
                    if (errorResponse != null) {
                        errorResponse.accept(
                                new ErrorResponse(response.body().errorMessage == null ? response.body().status : response.body().status + " " + response.body().errorMessage));
                    }
                    return;
                }

                for (final PlaceDescriptionJson movieTheater : response.body().results) {
                    CinemaDescription cinemaDescription = CinemaDescriptionFactory.getCinemaDescriptionFromGooglePlacesJson(movieTheater);
                    alreadyFetchedCinemas.add(cinemaDescription);
                }

                if (response.body().nextPageToken == null || alreadyFetchedCinemas.size() >= MAX_CINEMAS_COUNT)
                    callback.accept(alreadyFetchedCinemas);
                else
                    getCinemasAsync(location, callback, errorResponse, response.body().nextPageToken, alreadyFetchedCinemas);
            }

            @Override
            public void onFailure(@NonNull Call<DiscoverPlacesResultJson> call, @NonNull Throwable t) {
                if (errorResponse != null)
                    errorResponse.accept(new ErrorResponse(R.string.connectGooglePlacesFailure, t));
            }
        };

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @SuppressLint("SyntheticAccessor")
            @Override
            public void run() {
                if (nextPageToken == null)
                    service.getCinemas(location).enqueue(serviceCallback);
                else
                    service.getCinemasNextPage(nextPageToken).enqueue(serviceCallback);
            }
        }, App.getAppResources().getInteger(R.integer.google_places_api_fetch_delay_ms));
    }
}
