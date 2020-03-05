package com.silicium.otusfilmcatalog.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.silicium.otusfilmcatalog.App;
import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.model.FilmDescription;
import com.silicium.otusfilmcatalog.logic.model.FilmDescriptionFactory;
import com.silicium.otusfilmcatalog.logic.model.FragmentWithCallback;
import com.silicium.otusfilmcatalog.logic.model.IOnBackPressedListener;
import com.silicium.otusfilmcatalog.ui.cuctomcomponents.AddMoreDialogFragment;
import com.silicium.otusfilmcatalog.ui.cuctomcomponents.DisappearingSnackCircularProgressBar;
import com.silicium.otusfilmcatalog.ui.cuctomcomponents.UiComponents;
import com.tingyik90.snackprogressbar.SnackProgressBar;
import com.tingyik90.snackprogressbar.SnackProgressBarLayout;
import com.tingyik90.snackprogressbar.SnackProgressBarManager;

public class AddFragment extends FragmentWithCallback implements IOnBackPressedListener {

    public final static String FRAGMENT_TAG = AddFragment.class.getSimpleName();
    private DisappearingSnackCircularProgressBar snackProgressBar;
    private ConstraintLayout rootLayout;
    private boolean doubleBackToExitPressedOnce = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootLayout = view.findViewById(R.id.fragment_add);

        snackProgressBar = new DisappearingSnackCircularProgressBar(rootLayout,
                getString(R.string.backPressedToastText),
                new SnackProgressBarManager.OnDisplayListener() {
                    @SuppressLint("SyntheticAccessor")
                    @Override
                    public void onDismissed(@NonNull SnackProgressBar snackProgressBar, int onDisplayId) {
                        doubleBackToExitPressedOnce = false;
                    }

                    @Override
                    public void onShown(@NonNull SnackProgressBar snackProgressBar, int onDisplayId) {
                    }

                    @Override
                    public void onLayoutInflated(@NonNull SnackProgressBarLayout snackProgressBarLayout, @NonNull FrameLayout overlayLayout, @NonNull SnackProgressBar snackProgressBar, int onDisplayId) {
                    }
                }, this);

        Button btn = view.findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SyntheticAccessor")
            @Override
            public void onClick(View view) {
                onSaveClick();
            }
        });

        btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AddMoreDialogFragment addPhotoBottomDialogFragment = new AddMoreDialogFragment(new View.OnClickListener() {
                    @SuppressLint("SyntheticAccessor")
                    @Override
                    public void onClick(@NonNull View view) {
                        onItemClick(view);
                    }
                });
                assert getFragmentManager() != null;
                addPhotoBottomDialogFragment.show(getFragmentManager(), "add_more_dialog_fragment");
                return false;
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        if (doubleBackToExitPressedOnce)
            return false;

        snackProgressBar.Show();
        this.doubleBackToExitPressedOnce = true;

        return true;
    }

    private void onSaveClick() {
        EditText film_name = rootLayout.findViewById(R.id.film_name);
        EditText film_description = rootLayout.findViewById(R.id.film_description);

        FilmDescription film = FilmDescriptionFactory.getNewFilmDescription();
        film.name = film_name.getText().toString();
        film.description = film_description.getText().toString();

        App.getFilmDescriptionStorage().addFilm(film);
    }

    private void onItemClick(@NonNull View view) {
        Toast.makeText(view.getContext(), R.string.under_construction__hint_string, Toast.LENGTH_SHORT).show();
        UiComponents.showUnderConstructionSnackBar(rootLayout);
    }

}
