package com.silicium.otusfilmcatalog.logic.view;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.silicium.otusfilmcatalog.App;
import com.silicium.otusfilmcatalog.logic.model.FilmDescription;
import com.silicium.otusfilmcatalog.logic.model.IFilmDescriptionStorage;

public class FilmViewWrapper {

    //region  singleton
    @Nullable
    private static volatile FilmViewWrapper instance = null;
    @NonNull
    private final IFilmDescriptionStorage storageInstance = App.getFilmDescriptionStorage();

    private FilmViewWrapper() {
        super();
    }

    @NonNull
    public static FilmViewWrapper getInstance() {
        if (instance == null)
            synchronized (FilmViewWrapper.class) {
                if (instance == null)
                    instance = new FilmViewWrapper();
            }
        //noinspection ConstantConditions
        return instance;
    }
    //endregion

    ///region API
    @NonNull
    public FilmDescription getFilmByID(@NonNull String ID) {
        return storageInstance.getFilmByID(ID);
    }

    /**
     * Присутсвует ли заданный ID в списке фильмов ?
     *
     * @param ID идентификатор фильма
     * @return "истина", если пристусвует и "ложь", если нет
     */
    public boolean containsID(@NonNull String ID) {
        return storageInstance.containsID(ID);
    }

    /**
     * Получение представления с описанием фильма для отдельного экрана
     *
     * @param film   Данные из БД о фильме
     * @param parent родитель
     * @return представление с картинкой и описанием
     */
    @NonNull
    public View getFilmViewDetails(@NonNull FilmDescription film, final @Nullable Context parent) {
        LinearLayout ret = new LinearLayout(parent);
        ret.setOrientation(LinearLayout.VERTICAL);
        ret.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        ret.setTag(film.ID);
        ret.addView(getGenreChips(film, parent));
        ret.addView(getPicView(film, parent)); // TODO: сделать отдельные большие картинки, API теперь есть
        ret.addView(getDescView(film, parent));
        return ret;
    }

    @NonNull
    public String getFilmUrl(@NonNull FilmDescription film) {
        return String.format("<a href=\"%s\">%s</a>", film.url, film.name);
    }
    //endregion

    //region wrappers
    @NonNull
    private View getGenreChips(@NonNull FilmDescription film, @Nullable Context parent) {
        //noinspection ConstantConditions
        ChipGroup ret = new ChipGroup(parent); // TODO: добавиить отступы
        ret.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        for (int filmGenreID : film.genres) {
            Chip genre = new Chip(parent);
            genre.setText(App.getFilmDescriptionStorage().getReadableGenre(filmGenreID));
            ret.addView(genre);
        }
        return ret;
    }

    @NonNull
    private ImageView getPicView(@NonNull FilmDescription film, @Nullable Context parent) {
        ImageView pic = new ImageView(parent);
        pic.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
        pic.setPadding(10, 10, 10, 10);
        pic.setImageBitmap(film.cover);

        return pic;
    }

    @NonNull
    private TextView getDescView(@NonNull FilmDescription film, @Nullable Context parent) {
        TextView description = new TextView(parent);
        description.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 200, 3.0F));
        description.setPadding(10, 10, 10, 10);
        description.setText(film.description);

        return description;
    }
    //endregion
}
