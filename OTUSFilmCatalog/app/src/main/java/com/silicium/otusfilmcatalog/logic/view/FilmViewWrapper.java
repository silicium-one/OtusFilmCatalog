package com.silicium.otusfilmcatalog.logic.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.controller.FilmDescriptionStorage;
import com.silicium.otusfilmcatalog.logic.model.FilmDescription;

import java.util.ArrayList;
import java.util.List;

public class FilmViewWrapper {

    private FilmDescriptionStorage storageInstance = FilmDescriptionStorage.getInstance();

    //region  singleton
    private static volatile FilmViewWrapper instance = null;
    public static FilmViewWrapper getInstance()
    {
        if (instance == null)
            synchronized (FilmViewWrapper.class) {
                if (instance == null)
                    instance = new FilmViewWrapper();
            }
        return instance;
    }

    private FilmViewWrapper() {
        super();
    }
    //endregion

    ///region API
    public FilmDescription GetFilmByID(String ID)
    {
        return storageInstance.GetFilmByID(ID);
    }
    /**
     * Присутсвует ли заданный ID в списке фильмов ?
     * @param ID идентификатор фильма
     * @return "истина", если пристусвует и "ложь", если нет
     */
    public boolean containsID(String ID)
    {
        return storageInstance.containsID(ID);
    }

    /**
     * Получение представления с описанием фильма для отдельного экрана
     * @param film Данные из БД о фильме
     * @param parent родитель
     * @return представление с картинкой и описанием
     */
    public View GetFilmViewDetails(FilmDescription film, final Context parent)
    {
        LinearLayout ret = new LinearLayout(parent);
        ret.setOrientation(LinearLayout.VERTICAL);
        ret.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        ret.setTag(film.ID);
        ret.addView(GetPicView(film, parent)); // TODO: сделать отдельные большие картинки
        ret.addView(GetDescView(film, parent));
        return ret;
    }

    /**
     * Список представлений с фильмами
     * @param parent родительский элемент
     * @return Список View
     */
    public List<View> GetFilmViews(Context parent, View.OnClickListener detailClickListener)
    {
        List<View> views = new ArrayList<>();
        for (FilmDescription film: storageInstance.getFilms()){
            views.add(GetFilmView(film, parent, detailClickListener));
        }
        return views;
    }

    public String GetFilmUrl(FilmDescription film)
    {
        return String.format("<a href=\"%s\">%s</a>",film.Url, film.Name);
//        switch (ID)
//        {
//            case "film1":
//                return "<a href=\"https://www.kinopoisk.ru/film/1040419/\">Детство Шелдона</a>";
//            case "film2":
//                return "<a href=\"https://www.kinopoisk.ru/film/306084/\">Теория большого взрыва</a>";
//            case "film3":
//                return "<a href=\"https://www.kinopoisk.ru/film/33821/\">Кролик Багз или Дорожный Бегун</a>";
//            default:
//                return "<a href=\"\"https://www.kinopoisk.ru/error\"\">Где наши чемоданы?</a>";
//        }
    }

    /**
     * Получение представления с описанием фильма для списка
     * @param film Данные из БД о фильме
     * @param parent родитель
     * @param detailClickListener действие по нажатии кнопки "детали"
     * @return представление с картинкой и описанием
     */
    private View GetFilmView(FilmDescription film, Context parent, View.OnClickListener detailClickListener)
    {
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

        View pic = GetPicView(film, parent);
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                ObjectAnimator fadeOut = ObjectAnimator.ofFloat(v, View.ALPHA,1, 0);
                fadeOut.setDuration(2000);
                fadeOut.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        btnDetail.performClick();
                        ObjectAnimator.ofFloat(v, View.ALPHA,0,1).start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
                fadeOut.start();
            }});

        ret.addView(pic);
        ret.addView(GetDescView(film, parent));
        ret.addView(btnDetail);
        return ret;
    }
    //endregion

    //region wrappers
    private ImageView GetPicView(@org.jetbrains.annotations.NotNull FilmDescription film, Context parent)
    {
        ImageView pic = new ImageView(parent);
        pic.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 200, 1.0F));
        pic.setPadding(10,10,10,10);
        pic.setImageBitmap(film.Cover);
//        switch (ID) {
//            case "film1":
//                pic.setImageResource(R.drawable.film1);
//                break;
//            case "film2":
//                pic.setImageResource(R.drawable.film2);
//                break;
//            case "film3":
//                pic.setImageResource(R.drawable.film3);
//                break;
//            default:
//                pic.setImageResource(R.drawable.film_no_image);
//        }

        return pic;
    }

    private TextView GetDescView(@org.jetbrains.annotations.NotNull FilmDescription film, Context parent)
    {
        TextView description = new TextView(parent);
        description.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 200, 3.0F));
        description.setPadding(10,10,10,10);
        description.setText(film.Description);
//        switch (ID)
//        {
//            case "film1":
//                description.setText(R.string.film1);
//                break;
//            case "film2":
//                description.setText(R.string.film2);
//                break;
//            case "film3":
//                description.setText(R.string.film3);
//                break;
//        }
        return description;
    }
    //endregion
}
