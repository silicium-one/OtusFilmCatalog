package com.silicium.otusfilmcatalog.logic.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.controller.FilmDescriptionStorage;
import com.silicium.otusfilmcatalog.logic.model.FilmDescription;
import com.silicium.otusfilmcatalog.logic.model.FilmDescriptionFactory;

import java.util.ArrayList;
import java.util.List;

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
    @NonNull
    public View getFilmViewDetails(@NonNull FilmDescription film, @NonNull final Context parent) {
        LinearLayout ret = new LinearLayout(parent);
        ret.setOrientation(LinearLayout.VERTICAL);
        ret.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        ret.setTag(film.ID);
        ret.addView(GetGenreChips(film, parent));
        ret.addView(GetPicView(film, parent)); // TODO: сделать отдельные большие картинки, API теперь есть
        ret.addView(GetDescView(film, parent));
        return ret;
    }

    /**
     * Список представлений с фильмами
     *
     * @param parent родительский элемент
     * @return Список View
     */
    @NonNull
    public List<View> getFilmViews(@NonNull Context parent, View.OnClickListener detailClickListener) {
        List<View> views = new ArrayList<>();
        for (FilmDescription film : storageInstance.getFilms()) {
            views.add(getFilmView(film, parent, detailClickListener));
        }
        return views;
    }

    @NonNull
    public String getFilmUrl(@NonNull FilmDescription film) {
        return String.format("<a href=\"%s\">%s</a>", film.Url, film.Name);
    }
    //endregion

    //region wrappers
    /**
     * Получение представления с описанием фильма для списка
     *
     * @param film                Данные из БД о фильме
     * @param parent              родитель
     * @param detailClickListener действие по нажатии кнопки "детали"
     * @return представление с картинкой и описанием
     */
    @NonNull
    private View getFilmView(@NonNull FilmDescription film, @NonNull final Context parent, View.OnClickListener detailClickListener) {
        LinearLayout ret = new LinearLayout(parent);
        ret.setOrientation(LinearLayout.HORIZONTAL);
        ret.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        ret.setTag(film.ID);

        final Button btnDetail = new Button(parent);
        btnDetail.setTag(film.ID);
        btnDetail.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 0.1F));
        btnDetail.setText(R.string.filmDetailBtnText);
        btnDetail.setRotation(-90F);
        btnDetail.setOnClickListener(detailClickListener);

        View pic = GetPicViewPreview(film, parent);
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                ObjectAnimator fadeOut = ObjectAnimator.ofFloat(v, View.ALPHA, 1, 0);
                fadeOut.setDuration(parent.getResources().getInteger(R.integer.smooth_transition_time_ms));
                fadeOut.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        btnDetail.performClick();
                        ObjectAnimator.ofFloat(v, View.ALPHA, 0, 1).start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
                fadeOut.start();
            }
        });

        ret.addView(pic); //TODO: Принимать картинки разного размера и приводить к одному
        ret.addView(getDescView(film, parent));
        ret.addView(btnDetail);
        return ret;
    }

    @NonNull
    private View getGenreChips(@NonNull FilmDescription film, @NonNull Context parent) {
        ChipGroup ret = new ChipGroup(parent); // TODO: добавиить отступы
        ret.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        for (FilmDescription.FilmGenre filmGenre : film.Genre) {
            Chip genre = new Chip(parent);
            genre.setText(FilmDescriptionFactory.getReadableGenre(filmGenre));
            ret.addView(genre);
        }
        return ret;
    }

    private ImageView GetPicViewPreview(FilmDescription film, Context parent)
    {
        ImageView pic = new ImageView(parent);
        pic.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
        pic.setPadding(10,10,10,10);
        pic.setImageBitmap(film.CoverPreview);

        return pic;
    }

    private ImageView GetPicView(FilmDescription film, Context parent)
    {
        ImageView pic = new ImageView(parent);
        pic.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
        pic.setPadding(10, 10, 10, 10);
        pic.setImageBitmap(film.Cover);

        return pic;
    }

    @NonNull
    private TextView getDescView(@NonNull FilmDescription film, Context parent) {
        TextView description = new TextView(parent);
        description.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 200, 3.0F));
        description.setPadding(10, 10, 10, 10);
        description.setText(film.Description);

        return description;
    }
    //endregion
}
