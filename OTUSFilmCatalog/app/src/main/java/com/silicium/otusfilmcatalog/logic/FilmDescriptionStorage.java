package com.silicium.otusfilmcatalog.logic;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.silicium.otusfilmcatalog.R;

import java.util.ArrayList;
import java.util.List;

public class FilmDescriptionStorage {
    private List<String> FilmIDs;

    private static volatile FilmDescriptionStorage instance = null;
    public static FilmDescriptionStorage getInstance()
    {
        if (instance == null)
            synchronized (FilmDescriptionStorage.class) {
                if (instance == null)
                    instance = new FilmDescriptionStorage();
            }
        return instance;
    }
    private FilmDescriptionStorage() {
        super();
        FilmIDs = new ArrayList<>();
        FilmIDs.add("film1");
        FilmIDs.add("film2");
        FilmIDs.add("film3");
    }

    /**
     * Список ID фильмов
     * @return список ключей доступных в базе фильмов
     */
    public List<String> getFilmIDs()
    {
        return FilmIDs;
    }

    /**
     * Получение представления с описанием фильма для списка
     * @param ID идентификатор фильма
     * @param parent родитель
     * @param detailClickListener действие по нажатии кнопки "детали"
     * @return представление с картинкой и описанием
     */
    public View GetFilmView(String ID, Context parent, View.OnClickListener detailClickListener)
    {
        LinearLayout ret = new LinearLayout(parent);
        ret.setOrientation(LinearLayout.HORIZONTAL);
        ret.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        ret.setTag(ID);

        final Button btnDetail = new Button(parent);
        btnDetail.setTag(ID);
        btnDetail.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 0.1F));
        btnDetail.setText(R.string.filmDetailBtnText);
        btnDetail.setRotation(-90F);
        btnDetail.setOnClickListener(detailClickListener);

        View pic = GetPicView(ID, parent);
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
        ret.addView(GetDescView(ID, parent));
        ret.addView(btnDetail);
        return ret;
    }

    /**
     * Получение представления с описанием фильма для отдельного экрана
     * @param ID идентификатор фильма
     * @param parent родитель
     * @return представление с картинкой и описанием
     */
    public View GetFilmViewDetails(final String ID, final Context parent)
    {
        LinearLayout ret = new LinearLayout(parent);
        ret.setOrientation(LinearLayout.VERTICAL);
        ret.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        ret.setTag(ID);
        ret.addView(GetPicView(ID, parent)); // TODO: сделать отдельные большие картинки
        ret.addView(GetDescView(ID, parent));
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
        for (String ID: getFilmIDs()){
            views.add(GetFilmView(ID, parent, detailClickListener));
        }
        return views;
    }

    public String GetFilmUrl(String ID)
    {
        switch (ID)
        {
            case "film1":
                return "<a href=\"https://www.kinopoisk.ru/film/1040419/\">Детство Шелдона</a>";
            case "film2":
                return "<a href=\"https://www.kinopoisk.ru/film/306084/\">Теория большого взрыва</a>";

            case "film3":
                return "<a href=\"https://www.kinopoisk.ru/film/33821/\">Кролик Багз или Дорожный Бегун</a>";
            default:
                return "<a href=\"\"https://www.kinopoisk.ru/error\"\">Где наши чемоданы?</a>";
        }
    }

    private ImageView GetPicView(@org.jetbrains.annotations.NotNull String ID, Context parent)
    {
        ImageView pic = new ImageView(parent);
        pic.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 200, 1.0F));
        pic.setPadding(10,10,10,10);
        switch (ID)
        {
            case "film1":
                pic.setImageResource(R.drawable.film1);
                break;
            case "film2":
                pic.setImageResource(R.drawable.film2);
                break;
            case "film3":
                pic.setImageResource(R.drawable.film3);
                break;
        }

        return pic;
    }

    private TextView GetDescView(@org.jetbrains.annotations.NotNull String ID, Context parent)
    {
        TextView description = new TextView(parent);
        description.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 200, 3.0F));
        description.setPadding(10,10,10,10);
        switch (ID)
        {
            case "film1":
                description.setText(R.string.film1);
                break;
            case "film2":
                description.setText(R.string.film2);
                break;
            case "film3":
                description.setText(R.string.film3);
                break;
        }
        return description;
    }

}
