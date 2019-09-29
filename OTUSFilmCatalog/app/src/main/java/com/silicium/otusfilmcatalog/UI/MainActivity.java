package com.silicium.otusfilmcatalog.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.silicium.otusfilmcatalog.Logic.FilmDescriptionStorage;
import com.silicium.otusfilmcatalog.R;


public class MainActivity extends AppCompatActivity {

    String selectedFilmTag = ""; // TODO: оптимизировать на доступ по индексу

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final LinearLayout root = findViewById(R.id.film_root_layout);

        FilmDescriptionStorage instance = FilmDescriptionStorage.getInstance();

        for(View v : instance.GetFilmViews(this)) {
            v.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View v) {
                    View prev = root.findViewWithTag(selectedFilmTag);
                    if (prev != null)
                        prev.setBackgroundColor(root.getDrawingCacheBackgroundColor());

                    selectedFilmTag = v.getTag().toString();
                    v.setBackgroundColor(getResources().getColor(R.color.colorSelectedFilm)); // todo: передалать на вариант у учётом темы
                }
            });
            root.addView(v);
        }
    }
}
