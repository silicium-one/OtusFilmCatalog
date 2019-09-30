package com.silicium.otusfilmcatalog.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.silicium.otusfilmcatalog.logic.FilmDescriptionStorage;
import com.silicium.otusfilmcatalog.R;


public class MainActivity extends AppCompatActivity {

    private String selectedFilmTag = ""; // TODO: оптимизировать на доступ по индексу
    private final int DETAIL_ACTIVITY_CODE = 1;
    private final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final LinearLayout root = findViewById(R.id.film_root_layout);

        FilmDescriptionStorage instance = FilmDescriptionStorage.getInstance();

        for(View v : instance.GetFilmViews(this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelectedFilmTag(v.getTag().toString());
                    gotoDetailActivity();
                }}))
            root.addView(v);

        if (savedInstanceState != null)
            setSelectedFilmTag(savedInstanceState.getString("selectedFilmTag"));
    }

    private void gotoDetailActivity() {
        if (FilmDescriptionStorage.getInstance().getFilmIDs().contains(getSelectedFilmTag())) {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("selectedFilmTag", getSelectedFilmTag());
            startActivityForResult(intent, DETAIL_ACTIVITY_CODE);
        }
        else
            Toast.makeText(this, R.string.error_noFilmByID, Toast.LENGTH_LONG).show();
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
            v.setBackgroundColor(getResources().getColor(R.color.colorSelectedFilm)); // todo: передалать на вариант c учётом темы
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == DETAIL_ACTIVITY_CODE && resultCode == RESULT_OK)
        {
            assert data != null;
            String comment = data.getStringExtra("film_comment");
            boolean isLiked = data.getBooleanExtra("film_is_liked", false);
            Log.d(LOG_TAG, String.format("isLiked={%s}, comment={%s}", Boolean.toString(isLiked), comment));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
