package com.silicium.otusfilmcatalog.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.model.FilmDescription;
import com.silicium.otusfilmcatalog.logic.view.FilmViewWrapper;
import com.silicium.otusfilmcatalog.ui.cuctomcomponents.HideableSnackCircularProgressBar;
import com.tingyik90.snackprogressbar.SnackProgressBar;
import com.tingyik90.snackprogressbar.SnackProgressBarLayout;
import com.tingyik90.snackprogressbar.SnackProgressBarManager;

public class DetailActivity extends AppCompatActivity {

    FilmDescription film;
    CheckBox film_is_liked;
    EditText film_comment;
    private HideableSnackCircularProgressBar snackProgressBar;

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

        final int oldHeight = findViewById(R.id.more_text_view).getLayoutParams().height;
        final View more_layout = findViewById(R.id.more_layout);

        // get the bottom sheet view
        LinearLayout bottomSheet = findViewById(R.id.detail_bottom_sheet);
        // init the bottom sheet behavior
        final BottomSheetBehavior bShBehavior = BottomSheetBehavior.from(bottomSheet);

        bShBehavior.setBottomSheetCallback(
                new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull final View view, int newState) {
                        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                            view.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    bShBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                }
                            }, 3000);
                        } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                            float scaleFactor = 0.1F;
                            more_layout.animate().y(-(oldHeight * (1 - scaleFactor) / 2F)).scaleY(scaleFactor).start();
                        } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                            more_layout.animate().y(0).scaleY(1F).start();
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View view, float slideValue) {
                    }
                }
        );

        snackProgressBar = new HideableSnackCircularProgressBar(findViewById(R.id.activity_detail), this,
                getString(R.string.backPressedToastText), 2000,
                new SnackProgressBarManager.OnDisplayListener()
        {
            @Override
            public void onDismissed(@NonNull SnackProgressBar snackProgressBar, int onDisplayId) {
                doubleBackToExitPressedOnce = false;
            }

            @Override
            public void onShown(@NonNull SnackProgressBar snackProgressBar, int onDisplayId) {}

            @Override
            public void onLayoutInflated(@NonNull SnackProgressBarLayout snackProgressBarLayout, @NonNull FrameLayout overlayLayout, @NonNull SnackProgressBar snackProgressBar, int onDisplayId) {}
        });
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
            snackProgressBar.Show();
            this.doubleBackToExitPressedOnce = true;
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
