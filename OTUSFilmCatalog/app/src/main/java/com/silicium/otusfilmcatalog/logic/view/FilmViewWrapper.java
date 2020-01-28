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
import com.silicium.otusfilmcatalog.logic.controller.FilmDescriptionStorage;
import com.silicium.otusfilmcatalog.logic.model.FilmDescription;
import com.silicium.otusfilmcatalog.logic.model.FilmDescriptionFactory;

import org.jetbrains.annotations.NotNull;

public class FilmViewWrapper {

    //region  singleton
    @Nullable
    private static volatile FilmViewWrapper instance = null;
    @Nullable
    private final FilmDescriptionStorage storageInstance = FilmDescriptionStorage.getInstance();

    private FilmViewWrapper() {
        super();
    }

    @Nullable
    public static FilmViewWrapper getInstance() {
        if (instance == null)
            synchronized (FilmViewWrapper.class) {
                if (instance == null)
                    instance = new FilmViewWrapper();
            }
        return instance;
    }
    //endregion

    ///region API
    @Nullable
    public FilmDescription getFilmByID(String ID) {
        return storageInstance.getFilmByID(ID);
    }

    /**
     * Присутсвует ли заданный ID в списке фильмов ?
     *
     * @param ID идентификатор фильма
     * @return "истина", если пристусвует и "ложь", если нет
     */
    public boolean containsID(String ID) {
        return storageInstance.containsID(ID);
    }

    /**
     * Получение представления с описанием фильма для отдельного экрана
     *
     * @param film   Данные из БД о фильме
     * @param parent родитель
     * @return представление с картинкой и описанием
     */
    public View GetFilmViewDetails(@NotNull FilmDescription film, final Context parent)
    {
        LinearLayout ret = new LinearLayout(parent);
        ret.setOrientation(LinearLayout.VERTICAL);
        ret.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        ret.setTag(film.ID);
        ret.addView(GetGenreChips(film, parent));
        ret.addView(GetPicView(film, parent)); // TODO: сделать отдельные большие картинки, API теперь есть
        ret.addView(GetDescView(film, parent));
        return ret;
    }

    @NonNull
    public String GetFilmUrl(@NotNull FilmDescription film)
    {
        return String.format("<a href=\"%s\">%s</a>",film.Url, film.Name);
    }
    //endregion


    //region wrappers
    private View GetGenreChips(@NotNull FilmDescription film, Context parent) {
        ChipGroup ret = new ChipGroup(parent); // TODO: добавиить отступы
        ret.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        for (FilmDescription.FilmGenre filmGenre : film.Genre) {
            Chip genre = new Chip(parent);
            genre.setText(FilmDescriptionFactory.getReadableGenre(filmGenre));
            ret.addView(genre);
        }
        return ret;
    }

    private ImageView GetPicView(@NotNull FilmDescription film, Context parent)
    {
        ImageView pic = new ImageView(parent);
        pic.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
        pic.setPadding(10,10,10,10);
        pic.setImageBitmap(film.Cover);

        return pic;
    }

    private TextView GetDescView(@NotNull FilmDescription film, Context parent)
    {
        TextView description = new TextView(parent);
        description.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 200, 3.0F));
        description.setPadding(10,10,10,10);
        description.setText(film.Description);

        return description;
    }
    //endregion
}
