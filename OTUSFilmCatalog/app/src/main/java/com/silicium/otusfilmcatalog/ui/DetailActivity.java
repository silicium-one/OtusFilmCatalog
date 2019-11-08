package com.silicium.otusfilmcatalog.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.model.FilmDescription;
import com.silicium.otusfilmcatalog.logic.view.FilmViewWrapper;

public class DetailActivity extends AppCompatActivity {

    FilmDescription film;
    CheckBox film_is_liked;
    EditText film_comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String filmID = getIntent().getStringExtra("selectedFilmTag");

        final LinearLayout root = findViewById(R.id.film_root_layout);
        film_is_liked = findViewById(R.id.film_is_liked);
        film_comment = findViewById(R.id.film_comment);
        FilmViewWrapper instance = FilmViewWrapper.getInstance();
        film = instance.GetFilmByID(filmID);
        root.addView(instance.GetFilmViewDetails(film, this));
    }

    public void onShareBtnClick() {
        String textMessage = getString(R.string.shareFilmMsg) + FilmViewWrapper.getInstance().GetFilmUrl(film);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, textMessage);
        sendIntent.setType("text/html");

        String title = getResources().getString(R.string.shareFilmDlgTitle);
        Intent chooser = Intent.createChooser(sendIntent, title);

        if (sendIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        }
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent();
            intent.putExtra("film_is_liked", film_is_liked.isChecked());
            intent.putExtra("film_comment", film_comment.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        } else {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.backPressedToastText, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.detail_share)
        {
            onShareBtnClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
