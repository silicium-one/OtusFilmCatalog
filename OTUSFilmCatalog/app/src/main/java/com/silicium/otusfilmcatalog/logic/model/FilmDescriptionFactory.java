package com.silicium.otusfilmcatalog.logic.model;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.silicium.otusfilmcatalog.App;
import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.controller.MetricsStorage;

import org.jetbrains.annotations.Contract;

import java.util.Locale;
import java.util.UUID;

public class FilmDescriptionFactory {
    @NonNull
    @Deprecated
    public static FilmDescription getFilmDescriptionFromTMDBJson(@NonNull MovieDescriptionJson movieDescriptionJson) {
        final FilmDescription ret = getFilmDescription(Integer.toString(movieDescriptionJson.id));

        ret.name = movieDescriptionJson.title;
        ret.description = movieDescriptionJson.overview;
        ret.url = String.format(Locale.getDefault(), "https://www.themoviedb.org/movie/%d?language=ru-RU", movieDescriptionJson.id);
        ret.genres.addAll(movieDescriptionJson.genreIds);

        String coverUrl = "http://image.tmdb.org/t/p/w300" + movieDescriptionJson.posterPath;
        String previewUrl = "http://image.tmdb.org/t/p/w45" + movieDescriptionJson.posterPath;

        Glide.with(App.getApplication().getApplicationContext()).asBitmap()
                .load(coverUrl)
                .centerCrop()
                .error(R.drawable.film_no_image)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        ret.cover = resource;
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

        Glide.with(App.getApplication().getApplicationContext()).asBitmap()
                .load(previewUrl)
                .centerCrop()
                .error(R.drawable.film_no_image)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        ret.coverPreview = resource;
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });


        return ret;
    }

    public static void getFilmDescriptionFromTMDBJsonAsync(@NonNull MovieDescriptionJson movieDescriptionJson, @NonNull final Consumer<FilmDescription> readyCallback) {
        final FilmDescription ret = getFilmDescription(Integer.toString(movieDescriptionJson.id));

        ret.name = movieDescriptionJson.title;
        ret.description = movieDescriptionJson.overview;
        ret.url = String.format(Locale.getDefault(), "https://www.themoviedb.org/movie/%d?language=ru-RU", movieDescriptionJson.id);
        ret.genres.addAll(movieDescriptionJson.genreIds);

        final String coverUrl = "https://image.tmdb.org/t/p/w780" + movieDescriptionJson.posterPath;
        final String previewUrl = "https://image.tmdb.org/t/p/w45" + movieDescriptionJson.posterPath;

        Glide.with(App.getApplication().getApplicationContext()).asBitmap()
                .load(previewUrl)
                .centerCrop()
                .error(R.drawable.film_no_image)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        ret.coverPreview = resource;
                        Glide.with(App.getApplication().getApplicationContext()).asBitmap()
                                .load(coverUrl)
                                .centerCrop()
                                .error(R.drawable.film_no_image)
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        ret.cover = resource;
                                        readyCallback.accept(ret);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                        readyCallback.accept(ret);
                                    }
                                });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        readyCallback.accept(ret);
                    }
                });
    }

    @NonNull
    public static FilmDescription getNewFilmDescription() {
        return getFilmDescription(UUID.randomUUID().toString());
    }

    @Contract("_ -> new")
    @NonNull
    private static FilmDescription getFilmDescription(@NonNull String ID) {
        return new FilmDescription(ID, MetricsStorage.getMetricNotifier());
    }

    @Contract(" -> new")
    @NonNull
    public static FilmDescription getStubFilmDescription() {
        return getFilmDescription("");
    }
}
