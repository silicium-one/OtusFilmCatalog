package com.silicium.otusfilmcatalog.ui;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.silicium.otusfilmcatalog.App;
import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.SharedQuantityCounter;
import com.silicium.otusfilmcatalog.logic.controller.MetricsStorage;
import com.silicium.otusfilmcatalog.logic.model.FilmDescription;
import com.silicium.otusfilmcatalog.logic.model.FilmDescriptionFactory;
import com.silicium.otusfilmcatalog.logic.model.FragmentWithCallback;
import com.silicium.otusfilmcatalog.logic.model.IMetricNotifier;
import com.silicium.otusfilmcatalog.logic.model.IOnBackPressedListener;
import com.silicium.otusfilmcatalog.ui.cuctomcomponents.DisappearingSnackCircularProgressBar;
import com.tingyik90.snackprogressbar.SnackProgressBar;
import com.tingyik90.snackprogressbar.SnackProgressBarLayout;
import com.tingyik90.snackprogressbar.SnackProgressBarManager;

import java.util.Locale;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class DetailFragment extends FragmentWithCallback implements IOnBackPressedListener {

    public final static String FRAGMENT_TAG = DetailFragment.class.getSimpleName();
    @NonNull
    private String filmID = "";
    @NonNull
    private FilmDescription film = FilmDescriptionFactory.getStubFilmDescription();
    private View rootView;
    private Toolbar toolbar;
    private View more_header;
    private BottomSheetBehavior bShBehavior;
    private DisappearingSnackCircularProgressBar snackProgressBar;
    private boolean doubleBackToExitPressedOnce = false;

    public DetailFragment() {
        setRetainInstance(true);
    }

    @NonNull
    public static DetailFragment newInstance(@NonNull String text) {
        DetailFragment fragment = new DetailFragment();

        Bundle bundle = new Bundle();
        bundle.putString("filmID", text);
        fragment.setArguments(bundle);

        return fragment;
    }

    @NonNull
    public String getFilmID() {
        return filmID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null)
            filmID = bundle.getString("filmID", "");
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootView = view;

        film = App.getFilmDescriptionStorage().getFilmByID(filmID);

        ChipGroup genre_chips_chip_group = view.findViewById(R.id.genre_chips_chip_group);
        for (int filmGenreID : film.genres) {
            Chip genre = new Chip(view.getContext());
            genre.setText(App.getFilmDescriptionStorage().getReadableGenre(filmGenreID));
            genre_chips_chip_group.addView(genre);
        }

        ImageView cover_image_view = view.findViewById(R.id.cover_image_view);
        cover_image_view.setImageBitmap(film.cover);

        TextView film_description_text_view = view.findViewById(R.id.film_description_text_view);
        film_description_text_view.setText(film.description);

        if (film.coverUrl.isEmpty()) {
            cover_image_view.setImageBitmap(film.cover);
        } else {
            //todo: добавить поддержку темы, размеры получать из cover_image_view
            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(view.getContext());
            circularProgressDrawable.setStrokeWidth(5f);
            circularProgressDrawable.setCenterRadius(30f);
            circularProgressDrawable.start();

            Glide.with(App.getApplication().getApplicationContext())
                    .load(film.coverUrl)
                    .fitCenter()
                    .placeholder(circularProgressDrawable)
                    .error(R.drawable.ic_error_outline)
                    .into(cover_image_view);
        }

        ((TextView) view.findViewById(R.id.name_text_view)).setText(film.name);
        ((TextView) view.findViewById(R.id.popularity_value_text_view)).setText(String.format(Locale.getDefault(), "%.3f", film.popularity));
        ((TextView) view.findViewById(R.id.vote_average_value_text_view)).setText(String.format(Locale.getDefault(), "%.1f", film.voteAverage));
        ((TextView) view.findViewById(R.id.vote_count_value_text_view)).setText(String.format(Locale.getDefault(), "%d", film.voteCount));
        ((TextView) view.findViewById(R.id.release_date_value_text_view)).setText(film.releaseDate);

        more_header = rootView.findViewById(R.id.more_text_view);
        final int oldHeight = more_header.getLayoutParams().height;

        // get the bottom sheet view
        ConstraintLayout bottomSheet = rootView.findViewById(R.id.detail_bottom_sheet);
        // init the bottom sheet behavior
        bShBehavior = BottomSheetBehavior.from(bottomSheet);

        bShBehavior.setBottomSheetCallback(
                new BottomSheetBehavior.BottomSheetCallback() {
                    @SuppressLint("SyntheticAccessor")
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
                            more_header.animate().y(-(oldHeight * (1 - scaleFactor) / 2F)).scaleY(scaleFactor).setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    more_header.setVisibility(View.GONE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            }).start();
                        } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                            more_header.animate().setDuration(getResources().getInteger(R.integer.smooth_transition_time_ms)).y(0).scaleY(1F).setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    more_header.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            }).start();
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View view, float slideValue) {
                    }
                }
        );

        more_header.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SyntheticAccessor")
            @Override
            public void onClick(View v) {
                if (bShBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                    bShBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        snackProgressBar = new DisappearingSnackCircularProgressBar(rootView.findViewById(R.id.fragment_detail),
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

        toolbar = requireActivity().findViewById(R.id.toolbar);
    }

    @Override
    public void onStart() {
        super.onStart();

        toolbar.inflateMenu(R.menu.fragment_detail_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @SuppressLint("SyntheticAccessor")
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.detail_share) {
                    onShareBtnClick();
                    return true;
                }
                return false;
            }
        });

        if (bShBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            more_header.setVisibility(View.GONE);
        else if (bShBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
            more_header.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();

        toolbar.getMenu().clear(); // убираем кнопку "поделиться" из панели инструментов
    }

    private void onShareBtnClick() {
        String textMessage = getString(R.string.shareFilmMsg) + String.format("<a href=\"%s\">%s</a>", film.url, film.name);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, textMessage);
        sendIntent.setType("text/html");

        String title = getResources().getString(R.string.shareFilmDlgTitle);

        Intent chooser;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            Intent receiver = new Intent(getContext(), SharedQuantityCounter.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, receiver, PendingIntent.FLAG_UPDATE_CURRENT);
            chooser = Intent.createChooser(sendIntent, title, pendingIntent.getIntentSender());
        } else {
            chooser = Intent.createChooser(sendIntent, title);
            MetricsStorage.getMetricNotifier().increment(IMetricNotifier.SHARED_TAG);
        }

        if (sendIntent.resolveActivity(rootView.getContext().getPackageManager()) != null) {
            startActivity(chooser);
        }
    }

    @Override
    public boolean onBackPressed() {
        if (isLandscape())
            return false;

        if (doubleBackToExitPressedOnce) {
            return false;
        } else {
            snackProgressBar.Show();
            this.doubleBackToExitPressedOnce = true;
            return true;
        }
    }

    private boolean isLandscape() {
        return getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE;
    }
}
