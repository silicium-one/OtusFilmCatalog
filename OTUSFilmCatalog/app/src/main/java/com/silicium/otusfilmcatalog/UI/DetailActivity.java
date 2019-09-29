package com.silicium.otusfilmcatalog.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.silicium.otusfilmcatalog.Logic.FilmDescriptionStorage;
import com.silicium.otusfilmcatalog.R;

public class DetailActivity extends AppCompatActivity {

    String filmID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        filmID = getIntent().getStringExtra("selectedFilmTag");

        final LinearLayout root = findViewById(R.id.film_root_layout);
        root.addView(FilmDescriptionStorage.getInstance().GetFilmViewDetails(filmID, this));
    }

    public void onShareBtnClick(View v) {
        String textMessage = getString(R.string.info_shareFilmMsg) + FilmDescriptionStorage.getInstance().GetFilmUrl(filmID);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, textMessage);
        sendIntent.setType("text/html");

        String title = getResources().getString(R.string.info_shareFilmTitle);
        Intent chooser = Intent.createChooser(sendIntent, title);

        if (sendIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        }
    }
}
