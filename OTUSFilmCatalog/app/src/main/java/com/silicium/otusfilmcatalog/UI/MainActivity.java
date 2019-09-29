package com.silicium.otusfilmcatalog.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.silicium.otusfilmcatalog.Logic.FilmDescriptionStorage;
import com.silicium.otusfilmcatalog.R;


public class MainActivity extends AppCompatActivity {

    private String selectedFilmTag = ""; // TODO: оптимизировать на доступ по индексу

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final LinearLayout root = findViewById(R.id.film_root_layout);

        FilmDescriptionStorage instance = FilmDescriptionStorage.getInstance();

        for(View v : instance.GetFilmViews(this)) {
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelectedFilmTag(v.getTag().toString());
                }
            });
            root.addView(v);
        }

        if (savedInstanceState != null)
            setSelectedFilmTag(savedInstanceState.getString("selectedFilmTag"));
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("selectedFilmTag",selectedFilmTag);
    }

    String getSelectedFilmTag() {
        return selectedFilmTag;
    }

    void setSelectedFilmTag(String selectedFilmTag) {
        final LinearLayout root = findViewById(R.id.film_root_layout);
        View prev = root.findViewWithTag(getSelectedFilmTag());
        if (prev != null)
            prev.setBackgroundColor(root.getDrawingCacheBackgroundColor());
        this.selectedFilmTag = selectedFilmTag;
        View v = root.findViewWithTag(getSelectedFilmTag());
        if (v != null)
            v.setBackgroundColor(getResources().getColor(R.color.colorSelectedFilm)); // todo: передалать на вариант у учётом темы
    }
}
