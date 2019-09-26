package com.silicium.otusfilmcatalog.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.silicium.otusfilmcatalog.Logic.FilmDescriptionStorage;
import com.silicium.otusfilmcatalog.R;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout root = findViewById(R.id.film_root_layout);

        FilmDescriptionStorage instance = FilmDescriptionStorage.getInstance();

        for(View v : instance.GetFilmViews(this)) {
            root.addView(v);
        }
    }
}
