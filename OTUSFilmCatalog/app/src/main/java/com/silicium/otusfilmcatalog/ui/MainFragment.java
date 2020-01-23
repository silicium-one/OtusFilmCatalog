package com.silicium.otusfilmcatalog.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.controller.FilmDescriptionStorage;
import com.silicium.otusfilmcatalog.logic.model.FragmentWithCallback;
import com.silicium.otusfilmcatalog.logic.model.IOnBackPressedListener;
import com.silicium.otusfilmcatalog.logic.view.FilmItemAdapter;
import com.silicium.otusfilmcatalog.logic.view.FilmViewWrapper;
import com.silicium.otusfilmcatalog.ui.cuctomcomponents.DisappearingSnackCircularProgressBar;
import com.silicium.otusfilmcatalog.ui.cuctomcomponents.SwipeProcessor;
import com.tingyik90.snackprogressbar.SnackProgressBar;
import com.tingyik90.snackprogressbar.SnackProgressBarLayout;
import com.tingyik90.snackprogressbar.SnackProgressBarManager;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends FragmentWithCallback implements NavigationView.OnNavigationItemSelectedListener, IOnBackPressedListener {
    private String selectedFilmTag = "";
    public final static String FRAGMENT_TAG = MainFragment.class.getSimpleName();
    private View rootView;
    private FilmItemAdapter filmItemAdapter;
    private DisappearingSnackCircularProgressBar snackProgressBar;

    public MainFragment()
    {
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootView = view;

        RecyclerView film_RecyclerView = view.findViewById(R.id.film_RecyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false);
        film_RecyclerView.setLayoutManager(linearLayoutManager);
        List<String> items = new ArrayList<>(FilmDescriptionStorage.getInstance().getFilmsIDs());
        filmItemAdapter = new FilmItemAdapter(LayoutInflater.from(getContext()), items,
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        FilmDescriptionStorage.getInstance().GetFilmByID(buttonView.getTag().toString()).setFavorite(isChecked);
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
                        filmItemAdapter.setMultiselectMode(true);
                        return false;
                    }
                });

        film_RecyclerView.setAdapter(filmItemAdapter);
        film_RecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));

        SwipeProcessor swipeCallback = new SwipeProcessor(filmItemAdapter, false);
        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeCallback);
        touchHelper.attachToRecyclerView(film_RecyclerView);

        setSelectedFilmTag(selectedFilmTag);

        Toolbar toolbar = view.findViewById(R.id.fragment_main_toolbar);

        DrawerLayout drawer = view.findViewById(R.id.drawer_main_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = view.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    gotoFragmentCallback.gotoAddFragment();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

        snackProgressBar = new DisappearingSnackCircularProgressBar(rootView, this,
                getString(R.string.backPressedCancelSelectionToastText),
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

    private void gotoDetailFragment() {
        if (FilmViewWrapper.getInstance().containsID(getSelectedFilmTag())) {
            try{
                gotoFragmentCallback.gotoDetailFragment(getSelectedFilmTag());
            }
            catch (NullPointerException e)
            {
                e.printStackTrace();
            }
        }
        else
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
                        public void onClick(DialogInterface dialog, int which)
                        {
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
                filmItemAdapter.setMultiselectMode(false);
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
