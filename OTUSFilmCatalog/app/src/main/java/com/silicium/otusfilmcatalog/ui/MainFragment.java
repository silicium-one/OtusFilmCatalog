package com.silicium.otusfilmcatalog.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.controller.FilmDescriptionStorage;
import com.silicium.otusfilmcatalog.logic.model.FragmentWithCallback;
import com.silicium.otusfilmcatalog.logic.model.IItemTouchHelperAdapter;
import com.silicium.otusfilmcatalog.logic.model.IOnBackPressedListener;
import com.silicium.otusfilmcatalog.logic.view.FilmItemAdapter;
import com.silicium.otusfilmcatalog.logic.view.FilmViewWrapper;
import com.silicium.otusfilmcatalog.ui.cuctomcomponents.DisappearingSnackCircularProgressBar;
import com.silicium.otusfilmcatalog.ui.cuctomcomponents.SwipeProcessor;
import com.silicium.otusfilmcatalog.ui.cuctomcomponents.UiComponents;
import com.tingyik90.snackprogressbar.SnackProgressBar;
import com.tingyik90.snackprogressbar.SnackProgressBarLayout;
import com.tingyik90.snackprogressbar.SnackProgressBarManager;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class MainFragment extends FragmentWithCallback implements IOnBackPressedListener {
    public final static String FRAGMENT_TAG = MainFragment.class.getSimpleName();
    @NonNull
    private String selectedFilmTag = "";
    private FilmItemAdapter filmItemAdapter;
    private DisappearingSnackCircularProgressBar snackProgressBar;
    private boolean isFavoritesOnly;
    private final SwipeProcessor swipeCallback = new SwipeProcessor(new IItemTouchHelperAdapter() {
        @SuppressWarnings("unused")
        @SuppressLint("SyntheticAccessor")
        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            filmItemAdapter.onItemMove(fromPosition, toPosition);
        }

        @SuppressLint("SyntheticAccessor")
        @Override
        public void onItemDismiss(int position) {
            if (isFavoritesOnly())
                FilmDescriptionStorage.getInstance().getFilmByID(filmItemAdapter.getFilmIDByPos(position)).setFavorite(false);
            filmItemAdapter.onItemDismiss(position);
        }
    });
    private final ItemTouchHelper swipeProcessingHelper = new ItemTouchHelper(swipeCallback);
    private RecyclerView film_recycler_view;
    private FloatingActionButton fab_del;
    private FloatingActionButton fab_manipulate_favorites;
    private BottomNavigationView nav_view;
    private boolean isMultiselectMode;
    private boolean doubleBackToExitPressedOnce = false;

    public MainFragment() {
        setRetainInstance(true);
    }

    @SuppressLint("RestrictedApi")
    private void setMultiselectProcessingMode(boolean isMultiselect) {
        isMultiselectMode = isMultiselect;
        filmItemAdapter.setMultiselectMode(isMultiselect);
        if (isMultiselect) { //TODO: поискать возможност анимации одной цепочкой
            ObjectAnimator del_fadeIn = ObjectAnimator.ofFloat(fab_del, View.ALPHA, 0, 1).setDuration(getResources().getInteger(R.integer.smooth_transition_time_ms));
            final ObjectAnimator fav_fadeIn = ObjectAnimator.ofFloat(fab_manipulate_favorites, View.ALPHA, 0, 1).setDuration(getResources().getInteger(R.integer.smooth_transition_time_ms));

            del_fadeIn.addListener(new AnimatorListenerAdapter() {

                @SuppressLint("SyntheticAccessor")
                @Override
                public void onAnimationStart(Animator animation) {
                    fab_del.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    fav_fadeIn.start();
                }
            });

            fav_fadeIn.addListener(new AnimatorListenerAdapter() {

                @SuppressLint("SyntheticAccessor")
                @Override
                public void onAnimationStart(Animator animation) {
                    fab_manipulate_favorites.setVisibility(View.VISIBLE);
                }
            });

            del_fadeIn.start();
        } else { //TODO: поискать возможност анимации одной цепочкой
            final ObjectAnimator del_fadeOut = ObjectAnimator.ofFloat(fab_del, View.ALPHA, 1, 0).setDuration(getResources().getInteger(R.integer.smooth_transition_time_ms));
            ObjectAnimator fav_fadeOut = ObjectAnimator.ofFloat(fab_manipulate_favorites, View.ALPHA, 1, 0).setDuration(getResources().getInteger(R.integer.smooth_transition_time_ms));

            fav_fadeOut.addListener(new AnimatorListenerAdapter() {

                @SuppressLint("SyntheticAccessor")
                @Override
                public void onAnimationEnd(Animator animation) {
                    fab_manipulate_favorites.setVisibility(View.GONE);
                    del_fadeOut.start();
                }
            });

            del_fadeOut.addListener(new AnimatorListenerAdapter() {

                @SuppressLint("SyntheticAccessor")
                @Override
                public void onAnimationEnd(Animator animation) {
                    fab_del.setVisibility(View.GONE);
                }
            });

            fav_fadeOut.start();
        }
    }

    @Contract(pure = true)
    private boolean isFavoritesOnly() {
        return isFavoritesOnly;
    }

    private void setFavoritesOnly(boolean favoritesOnly) {
        isFavoritesOnly = favoritesOnly;
        setMultiselectProcessingMode(false);
        if (isFavoritesOnly)
            favoritesListMode();
        else
            fullListMode();
    }

    private void favoritesListMode() {
        swipeCallback.setSwipeDeletionPossible(true);
        nav_view.getMenu().getItem(0).setChecked(true); //nav_view.setSelectedItemId(R.id.favorites_list); - бесконечная рекурсия
        film_recycler_view.post(new Runnable() {
            @SuppressLint("SyntheticAccessor")
            @Override
            public void run() {
                filmItemAdapter.removeAllItems();

                for (String item : FilmDescriptionStorage.getInstance().getFavoriteFilmsIDs(true))
                    filmItemAdapter.addItem(item);

                setSelectedFilmTag(getSelectedFilmTag());
            }
        });
    }

    private void fullListMode() {
        swipeCallback.setSwipeDeletionPossible(false);
        nav_view.getMenu().getItem(1).setChecked(true); //nav_view.setSelectedItemId(R.id.full_list); - бесконечная рекурсия
        FilmDescriptionStorage.getInstance().getFilmsIDsNextPageAsync(new Consumer<Collection<String>>() {
            @SuppressLint("SyntheticAccessor")
            @Override
            public void accept(Collection<String> filmIDs) {
                filmItemAdapter.removeAllItems();

                for (String item : filmIDs)
                    filmItemAdapter.addItem(item);

                setSelectedFilmTag(getSelectedFilmTag());
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nav_view = view.findViewById(R.id.bottom_main_navigation);
        nav_view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("SyntheticAccessor")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.favorites_list)
                    setFavoritesOnly(true);
                else if (id == R.id.full_list)
                    setFavoritesOnly(false);

                return false;
            }
        });

        FloatingActionButton fab_add = view.findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    gotoFragmentCallback.gotoAddFragment();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

        fab_del = view.findViewById(R.id.fab_del);
        fab_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                UiComponents.showUnderConstructionSnackBar(v);
            }
        });

        fab_manipulate_favorites = view.findViewById(R.id.fab_manipulate_favorites);
        fab_manipulate_favorites.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SyntheticAccessor")
            @Override
            public void onClick(View v) {
                @SuppressLint("SyntheticAccessor") List<String> checkedIDs = filmItemAdapter.getCheckedIDs();
                if (isFavoritesOnly()) {
                    for (String filmID : checkedIDs) {
                        FilmDescriptionStorage.getInstance().getFilmByID(filmID).setFavorite(false);
                        filmItemAdapter.removeItemByID(filmID);
                    }

                    if (filmItemAdapter.getItemCount() == 0)
                        setMultiselectProcessingMode(false);
                } else {
                    List<String> favoriteIDs = new ArrayList<>();
                    for (String filmID : checkedIDs) {
                        if (FilmDescriptionStorage.getInstance().getFilmByID(filmID).isFavorite())
                            favoriteIDs.add(filmID);
                    }

                    if (favoriteIDs.size() == 0) { // в выбранных элементах избранных нет, надо добавить выбранные элементы в избранное
                        for (String filmID : checkedIDs) {
                            FilmDescriptionStorage.getInstance().getFilmByID(filmID).setFavorite(true);
                            filmItemAdapter.notifyItemChanged(filmItemAdapter.getPosByID(filmID));
                        }
                    } else { // надо удалить элементы из избранного
                        for (String filmID : favoriteIDs) {
                            FilmDescriptionStorage.getInstance().getFilmByID(filmID).setFavorite(false);
                            filmItemAdapter.notifyItemChanged(filmItemAdapter.getPosByID(filmID));
                        }
                    }
                }
            }
        });

        snackProgressBar = new DisappearingSnackCircularProgressBar(view,
                getString(R.string.backPressedCancelSelectionToastText),
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

        film_recycler_view = view.findViewById(R.id.film_recycler_view);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false);
        film_recycler_view.setLayoutManager(linearLayoutManager);
        film_recycler_view.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        RecyclerView.ItemAnimator itemAnimator = new SlideInUpAnimator(new OvershootInterpolator(1f));
        itemAnimator.setAddDuration(view.getResources().getInteger(R.integer.element_adding_animation_time_ms));
        film_recycler_view.setItemAnimator(itemAnimator);
        swipeProcessingHelper.attachToRecyclerView(film_recycler_view);


        boolean adapterCreationRequired = filmItemAdapter == null;

        if (adapterCreationRequired) {
            filmItemAdapter = new FilmItemAdapter(LayoutInflater.from(getContext()),
                    new CompoundButton.OnCheckedChangeListener() {
                        @SuppressLint("SyntheticAccessor")
                        @Override
                        public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                            String filmID = buttonView.getTag().toString();
                            FilmDescriptionStorage.getInstance().getFilmByID(filmID).setFavorite(isChecked);
                            if (!isChecked && isFavoritesOnly())
                                filmItemAdapter.removeItemByID(filmID);
                        }
                    },
                    new View.OnClickListener() {
                        @SuppressLint("SyntheticAccessor")
                        @Override
                        public void onClick(@NonNull View v) {
                            setSelectedFilmTag(v.getTag().toString());
                            gotoDetailFragment();
                        }
                    },
                    new View.OnLongClickListener() {
                        @SuppressLint("SyntheticAccessor")
                        @Override
                        public boolean onLongClick(View v) {
                            setMultiselectProcessingMode(true);
                            return false;
                        }
                    });
        }

        film_recycler_view.setAdapter(filmItemAdapter);

        if (adapterCreationRequired) {
            if (isFavoritesOnly())
                favoritesListMode();
            else
                fullListMode();
        }

        setMultiselectProcessingMode(isMultiselectMode);

        final SwipeRefreshLayout srl = view.findViewById(R.id.srl);

        film_recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            /**
             * Callback method to be invoked when RecyclerView's scroll state changes.
             *
             * @param recyclerView The RecyclerView whose scroll state has changed.
             * @param newState     The updated scroll state. One of {@link RecyclerView#SCROLL_STATE_IDLE},
             *                     {@link RecyclerView#SCROLL_STATE_DRAGGING} or {@link RecyclerView#SCROLL_STATE_SETTLING}.
             */
            @SuppressLint("SyntheticAccessor")
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (isFavoritesOnly())
                    return;

                if (linearLayoutManager.findLastVisibleItemPosition() == filmItemAdapter.getItemCount() - 1) {
                    if (srl.isRefreshing())
                        return;

                    srl.setRefreshing(true);
                    FilmDescriptionStorage.getInstance().getFilmsIDsNextPageAsync(new Consumer<Collection<String>>() {
                        @Override
                        public void accept(final Collection<String> filmIDs) {
                            view.postDelayed(new Runnable() { // ИБД
                                @Override
                                public void run() {
                                    filmItemAdapter.addRange(filmIDs);
                                    srl.setRefreshing(false);
                                }
                            }, 333);
                        }
                    });
                }
            }
        });
    }

    private void gotoDetailFragment() {
        if (FilmViewWrapper.getInstance().containsID(getSelectedFilmTag())) {
            try {
                gotoFragmentCallback.gotoDetailFragment(getSelectedFilmTag());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else
            Toast.makeText(getContext(), R.string.noFilmByIDErr, Toast.LENGTH_LONG).show();
    }

    @NonNull
    @SuppressLint("KotlinPropertyAccess")
    @Contract(pure = true)
    private String getSelectedFilmTag() {
        return selectedFilmTag;
    }

    public void setSelectedFilmTag(@NonNull String selectedFilmTag) {
        filmItemAdapter.setSelectedFilmTag(selectedFilmTag);
        this.selectedFilmTag = selectedFilmTag;
    }

    /**
     * Если вернуть ИСТИНА, то нажатие кнопки "назад" обрабтано. Если вернуть ЛОЖЬ, то требуется обработка выше по стэку.
     *
     * @return ИСТИНА, если нажание кнопки back обработано и ЛОЖЬ в противном случае
     */
    @Override
    public boolean onBackPressed() {
        if (filmItemAdapter.isMultiselectMode()) {
            if (doubleBackToExitPressedOnce) {
                setMultiselectProcessingMode(false);
                snackProgressBar.dismiss();
            } else {
                snackProgressBar.Show();
                this.doubleBackToExitPressedOnce = true;
            }
            return true;
        }
        return false;
    }
}
