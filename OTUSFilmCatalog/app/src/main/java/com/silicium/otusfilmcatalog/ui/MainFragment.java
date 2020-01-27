package com.silicium.otusfilmcatalog.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
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
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
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

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class MainFragment extends FragmentWithCallback implements NavigationView.OnNavigationItemSelectedListener, IOnBackPressedListener {
    private String selectedFilmTag = "";
    public final static String FRAGMENT_TAG = MainFragment.class.getSimpleName();
    private View rootView;
    private FilmItemAdapter filmItemAdapter;
    private DisappearingSnackCircularProgressBar snackProgressBar;
    private boolean isFavoritesOnly;
    private RecyclerView film_RecyclerView;
    private FloatingActionButton fab_del;
    private FloatingActionButton fab_del_favorites;

    @SuppressLint("RestrictedApi")
    private void setMultiselectProcessingMode(boolean isMultiselect) {
        filmItemAdapter.setMultiselectMode(isMultiselect);
        int visibility = isMultiselect ? View.VISIBLE : View.GONE;
        fab_del.setVisibility(visibility);
        fab_del_favorites.setVisibility(visibility);
    }
    private SwipeProcessor swipeCallback = new SwipeProcessor(new IItemTouchHelperAdapter() {
        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            filmItemAdapter.onItemMove(fromPosition, toPosition);
        }

        @Override
        public void onItemDismiss(int position) {
            if (isFavoritesOnly())
                FilmDescriptionStorage.getInstance().GetFilmByID(filmItemAdapter.getTagByPos(position)).setFavorite(false);
            filmItemAdapter.onItemDismiss(position);
        }
    });

    private ItemTouchHelper swipeProcessingHelper = new ItemTouchHelper(swipeCallback);

    public MainFragment() {
        setRetainInstance(true);
    }

    @Contract(pure = true)
    private boolean isFavoritesOnly() {
        return isFavoritesOnly;
    }

    private void setFavoritesOnly(boolean favoritesOnly) {
        isFavoritesOnly = favoritesOnly;
        if (isFavoritesOnly)
            favoritesListMode();
        else
            fullListMode();
    }

    private void favoritesListMode() {
        setMultiselectProcessingMode(false);
        swipeCallback.setSwipeDeletionPossible(true);
        film_RecyclerView.post(new Runnable() {
            @Override
            public void run() {
                filmItemAdapter.removeAllItems();

                for (String item : FilmDescriptionStorage.getInstance().getFavoriteFilmsIDs())
                    filmItemAdapter.addItem(item);

                setSelectedFilmTag(getSelectedFilmTag());
            }
        });
    }

    private void fullListMode() {
        setMultiselectProcessingMode(false);
        swipeCallback.setSwipeDeletionPossible(false);
        film_RecyclerView.post(new Runnable() {
            @Override
            public void run() {
                filmItemAdapter.removeAllItems();

                for (String item : FilmDescriptionStorage.getInstance().getFilmsIDs())
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

        rootView = view;

        final BottomNavigationView bnv = view.findViewById(R.id.bottom_main_navigation);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
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

        Toolbar toolbar = view.findViewById(R.id.fragment_main_toolbar);

        DrawerLayout drawer = view.findViewById(R.id.drawer_main_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = view.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
            public void onClick(View v) {
                UiComponents.showUnderConstructionSnackBar(v);
            }
        });

        fab_del_favorites = view.findViewById(R.id.fab_del_favorites);
        fab_del_favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiComponents.showUnderConstructionSnackBar(v);
            }
        });

        snackProgressBar = new DisappearingSnackCircularProgressBar(rootView, this,
                getString(R.string.backPressedCancelSelectionToastText),
                new SnackProgressBarManager.OnDisplayListener() {
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
                });

        film_RecyclerView = rootView.findViewById(R.id.film_RecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(rootView.getContext(), RecyclerView.VERTICAL, false);
        film_RecyclerView.setLayoutManager(linearLayoutManager);

        filmItemAdapter = new FilmItemAdapter(LayoutInflater.from(getContext()),
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        String filmID = buttonView.getTag().toString();
                        FilmDescriptionStorage.getInstance().GetFilmByID(filmID).setFavorite(isChecked);
                        if (!isChecked && isFavoritesOnly())
                            filmItemAdapter.removeItemByID(filmID);
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setSelectedFilmTag(v.getTag().toString());
                        gotoDetailFragment();
                    }
                },
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setMultiselectProcessingMode(true);
                        return false;
                    }
                });

        film_RecyclerView.setAdapter(filmItemAdapter);
        film_RecyclerView.addItemDecoration(new DividerItemDecoration(rootView.getContext(), DividerItemDecoration.VERTICAL));

        RecyclerView.ItemAnimator itemAnimator = new SlideInUpAnimator(new OvershootInterpolator(1f));
        itemAnimator.setAddDuration(rootView.getResources().getInteger(R.integer.element_adding_animation_time_ms));
        film_RecyclerView.setItemAnimator(itemAnimator);

        swipeProcessingHelper.attachToRecyclerView(film_RecyclerView);

        if (isFavoritesOnly())
            favoritesListMode();
        else
            fullListMode();
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

    @Contract(pure = true)
    private String getSelectedFilmTag() {
        return selectedFilmTag;
    }

    public void setSelectedFilmTag(String selectedFilmTag) {
        filmItemAdapter.setSelectedFilmTag(selectedFilmTag);
        this.selectedFilmTag = selectedFilmTag;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item_about)
            gotoFragmentCallback.gotoAboutFragment();
        else if (id == R.id.item_exit) {
            AlertDialog.Builder bld = new AlertDialog.Builder(rootView.getContext());
            DialogInterface.OnClickListener exitDo =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    };

            bld.setMessage(R.string.exitDialogMessage);
            bld.setTitle(R.string.exitDialogTitle);
            bld.setNegativeButton(R.string.exitDialogNegativeAnswer, null);
            bld.setPositiveButton(R.string.exitDialogPositiveAnswer, exitDo);
            bld.setCancelable(false);
            AlertDialog dialog = bld.create();
            dialog.show();
        }

        DrawerLayout drawer = rootView.findViewById(R.id.drawer_main_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean doubleBackToExitPressedOnce = false;

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
