package com.silicium.otusfilmcatalog.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.model.FilmDescription;
import com.silicium.otusfilmcatalog.logic.model.FragmentWithCallback;
import com.silicium.otusfilmcatalog.logic.model.IOnBackPressedListener;
import com.silicium.otusfilmcatalog.logic.view.FilmViewWrapper;
import com.silicium.otusfilmcatalog.ui.cuctomcomponents.HideableSnackCircularProgressBar;
import com.tingyik90.snackprogressbar.SnackProgressBar;
import com.tingyik90.snackprogressbar.SnackProgressBarLayout;
import com.tingyik90.snackprogressbar.SnackProgressBarManager;

public class DetailFragment extends FragmentWithCallback implements IOnBackPressedListener {

    private String filmID;
    private FilmDescription film;
    private CheckBox film_is_liked;
    private EditText film_comment;
    public final static String FRAGMENT_TAG = "DetailFragment";

    private View rootView;

    private HideableSnackCircularProgressBar snackProgressBar;

    public static DetailFragment newInstance(String text) {
        DetailFragment fragment = new DetailFragment();

        Bundle bundle = new Bundle();
        bundle.putString("filmID", text);
        fragment.setArguments(bundle);

        return fragment;
    }

    public DetailFragment()
    {
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null)
            filmID = bundle.getString("filmID");
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootView = view;

        final LinearLayout root = rootView.findViewById(R.id.film_root_layout);
        film_is_liked = rootView.findViewById(R.id.film_is_liked);
        film_comment = rootView.findViewById(R.id.film_comment);
        FilmViewWrapper instance = FilmViewWrapper.getInstance();
        film = instance.GetFilmByID(filmID);
        root.addView(instance.GetFilmViewDetails(film, getContext()));

        final View more_header = rootView.findViewById(R.id.more_text_view);
        final int oldHeight = more_header.getLayoutParams().height;


        // get the bottom sheet view
        ConstraintLayout bottomSheet = rootView.findViewById(R.id.detail_bottom_sheet);
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
                            }, getResources().getInteger(R.integer.foolproof_time_ms));
                        } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                            float scaleFactor = 0.1F;
                            more_header.animate().y(-(oldHeight * (1 - scaleFactor) / 2F)).scaleY(scaleFactor).start();
                        } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                            more_header.animate().setDuration(getResources().getInteger(R.integer.smooth_transistion_time_ms)).y(0).scaleY(1F).start();
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View view, float slideValue) {
                    }
                }
        );

        snackProgressBar = new HideableSnackCircularProgressBar(rootView.findViewById(R.id.fragment_detail), this,
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
    }

    private void onShareBtnClick() {
        String textMessage = getString(R.string.shareFilmMsg) + FilmViewWrapper.getInstance().GetFilmUrl(film);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, textMessage);
        sendIntent.setType("text/html");

        String title = getResources().getString(R.string.shareFilmDlgTitle);
        Intent chooser = Intent.createChooser(sendIntent, title);

        if (sendIntent.resolveActivity(rootView.getContext().getPackageManager()) != null) {
            startActivity(chooser);
        }
    }

    private boolean doubleBackToExitPressedOnce = false;
    /**
     * Если вернуть ИСТИНА, то нажатие кнопки "назад" обрабтано. Если вернуть ЛОЖЬ, то требуется обработка выше по стэку.
     * @return ИСТИНА, если нажание кнопки back обработано и ЛОЖЬ в противном случае
     */
    @Override
    public boolean onBackPressed() {
        if (doubleBackToExitPressedOnce) {
//            Intent intent = new Intent();
//            intent.putExtra("film_is_liked", film_is_liked.isChecked());
//            intent.putExtra("film_comment", film_comment.getText().toString());
//            setResult(RESULT_OK, intent);
//            finish();
            // TODO: придумать, как обработать полученные даннве. Возможно, упокавать их в базу
            return false;
        } else {
            snackProgressBar.Show();
            this.doubleBackToExitPressedOnce = true;
            return true;
        }
    }

//    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.fragment_detail_menu, menu);
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
