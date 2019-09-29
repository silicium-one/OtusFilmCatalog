package com.silicium.otusfilmcatalog.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.silicium.otusfilmcatalog.Logic.FilmDescriptionStorage;
import com.silicium.otusfilmcatalog.R;

public class DetailActivity extends AppCompatActivity {

    String filmID;
    CheckBox film_is_liked;
    EditText film_comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        filmID = getIntent().getStringExtra("selectedFilmTag");

        final LinearLayout root = findViewById(R.id.film_root_layout);
        film_is_liked = findViewById(R.id.film_is_liked);
        film_comment = findViewById(R.id.film_comment);
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
            Toast.makeText(this, R.string.backPressedText, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }
}
