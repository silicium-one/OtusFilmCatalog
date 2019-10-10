package com.silicium.otusfilmcatalog.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.silicium.otusfilmcatalog.logic.FilmDescriptionStorage;
import com.silicium.otusfilmcatalog.R;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String selectedFilmTag = ""; // TODO: оптимизировать на доступ по индексу
    private boolean isAboutMode = false;
    private final int DETAIL_ACTIVITY_CODE = 1;
    private final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_main_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState != null) {
            setSelectedFilmTag(savedInstanceState.getString("selectedFilmTag"));
            isAboutMode = savedInstanceState.getBoolean("isAboutMode", false);
        }

        if (isAboutMode)
            aboutMode();
        else
            filmSelectMode();
    }

    private void filmSelectMode() {
        isAboutMode = false;
        final LinearLayout root = findViewById(R.id.film_root_layout);
        root.removeAllViews();

        FilmDescriptionStorage instance = FilmDescriptionStorage.getInstance();

        for(View v : instance.GetFilmViews(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedFilmTag(v.getTag().toString());
                gotoDetailActivity();
            }}))
            root.addView(v);
    }

    private void aboutMode() {
        isAboutMode = true;
        final LinearLayout root = findViewById(R.id.film_root_layout);
        root.removeAllViews();

        ImageView pic = new ImageView(this);
        pic.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        pic.setPadding(10,10,10,10);
        pic.setImageResource(R.drawable.otus);
        root.addView(pic);
    }

    private void gotoDetailActivity() {
        if (FilmDescriptionStorage.getInstance().getFilmIDs().contains(getSelectedFilmTag())) {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("selectedFilmTag", getSelectedFilmTag());
            startActivityForResult(intent, DETAIL_ACTIVITY_CODE);
        }
        else
            Toast.makeText(this, R.string.noFilmByIDErr, Toast.LENGTH_LONG).show();
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("selectedFilmTag",selectedFilmTag);
        outState.putBoolean("isAboutMode",isAboutMode);
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item_home)
            filmSelectMode();
        else if (id == R.id.item_about)
            aboutMode();

        DrawerLayout drawer = findViewById(R.id.drawer_main_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
