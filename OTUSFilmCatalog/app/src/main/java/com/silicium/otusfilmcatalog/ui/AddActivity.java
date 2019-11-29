package com.silicium.otusfilmcatalog.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.controller.FilmDescriptionStorage;
import com.silicium.otusfilmcatalog.logic.model.FilmDescription;
import com.silicium.otusfilmcatalog.logic.model.FilmDescriptionFactory;
import com.silicium.otusfilmcatalog.ui.cuctomcomponents.AddMoreDialogFragment;
import com.silicium.otusfilmcatalog.ui.cuctomcomponents.HideableSnackCircularProgressBar;
import com.silicium.otusfilmcatalog.ui.cuctomcomponents.UiComponets;
import com.tingyik90.snackprogressbar.SnackProgressBar;
import com.tingyik90.snackprogressbar.SnackProgressBarLayout;
import com.tingyik90.snackprogressbar.SnackProgressBarManager;

public class AddActivity extends AppCompatActivity {

    private HideableSnackCircularProgressBar snackProgressBar;
    private ConstraintLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        rootLayout = findViewById(R.id.activity_add);

        snackProgressBar = new HideableSnackCircularProgressBar(rootLayout, this,
                getString(R.string.backPressedToastText),
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

        Button btn = findViewById(R.id.button);
        btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AddMoreDialogFragment addPhotoBottomDialogFragment = new AddMoreDialogFragment();
                addPhotoBottomDialogFragment.show(getSupportFragmentManager(), "add_more_dialog_fragment");
                return false;
            }
        });
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
        } else {
            snackProgressBar.Show();
            this.doubleBackToExitPressedOnce = true;
        }
    }

    public void onSaveClick(View view) {
        EditText film_name = findViewById(R.id.film_name);
        EditText film_description = findViewById(R.id.film_description);

        FilmDescription film = FilmDescriptionFactory.GetNewFilmDescription();
        film.Name = film_name.getText().toString();
        film.Description = film_description.getText().toString();

        FilmDescriptionStorage.getInstance().addFilm(film);
        finish();
    }

    public void onItemClick(View view) {
        Toast.makeText(view.getContext(),R.string.under_construction__hint_string,Toast.LENGTH_SHORT).show();
        UiComponets.showUnderConstructionSnackBar(rootLayout);
    }

}
