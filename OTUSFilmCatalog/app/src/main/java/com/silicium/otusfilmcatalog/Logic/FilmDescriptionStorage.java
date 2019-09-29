package com.silicium.otusfilmcatalog.Logic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.silicium.otusfilmcatalog.R;

import java.util.ArrayList;
import java.util.List;
import static java.util.Collections.emptyList;

public class FilmDescriptionStorage {
    private String[] FilmIDs;

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
        FilmIDs = new String[]{"film1", "film2", "film3"};
    }

    /**
     * Список ID фильмов
     * @return список ключей доступных в базе фильмов
     */
    public String[] getFilmIDs()
    {
        return FilmIDs;
    }

    /**
     * Получение представления с описанием фильма
     * @param ID идентификатор фильма
     * @param parent родитель
     * @return представление с картинкой и описанием
     */
    public View GetFilmView(String ID, Context parent)
    {
        LinearLayout ret = new LinearLayout(parent);
        ret.setOrientation(LinearLayout.HORIZONTAL);
        ret.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        ret.setTag(ID);
        ret.addView(GetPicView(ID, parent));
        ret.addView(GetDescView(ID, parent));

        return ret;
    }


    public List<View> GetFilmViews(Context parent)
    {
        List<View> views = new ArrayList<View>();
        for (String ID: getFilmIDs()){
            views.add(GetFilmView(ID, parent));
        }
        return views;
    }

    private ImageView GetPicView(@org.jetbrains.annotations.NotNull String ID, Context parent)
    {
        ImageView pic = new ImageView(parent);
        pic.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 200, 1.0F));
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
        description.setLayoutParams(new LinearLayout.LayoutParams(0, 200, 3.0F));
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
