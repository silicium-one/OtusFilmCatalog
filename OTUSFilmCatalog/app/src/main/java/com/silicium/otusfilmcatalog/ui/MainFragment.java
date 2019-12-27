package com.silicium.otusfilmcatalog.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.view.FilmViewWrapper;
import com.silicium.otusfilmcatalog.ui.cuctomcomponents.UiComponets;

import static android.app.Activity.RESULT_OK;


public class MainFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    private String selectedFilmTag = ""; // TODO: оптимизировать на доступ по индексу
    private boolean isAboutMode = false;
    private final int DETAIL_ACTIVITY_CODE = 1;
    public final static String LOG_TAG = "MainFragment";
    private LinearLayout film_root_layout;
    private ConstraintLayout film_about_layout;
    private FloatingActionButton fab;
    private View rootView;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootView = view;

        film_root_layout = view.findViewById(R.id.film_root_layout);
        film_about_layout = view.findViewById(R.id.film_about_layout);
        fab = view.findViewById(R.id.fab);

        film_root_layout.removeAllViews();
        for(View v : FilmViewWrapper.getInstance().GetFilmViews(getContext(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedFilmTag(v.getTag().toString());
                gotoDetailActivity();
            }}))
            film_root_layout.addView(v);
        setSelectedFilmTag(selectedFilmTag);

        Toolbar toolbar = view.findViewById(R.id.main_activity_toolbar);

        final BottomNavigationView bnv = view.findViewById(R.id.bottom_about_navigation);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                UiComponets.showUnderConstructionSnackBar(bnv);
                return false;
            }
        });

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
                Intent intent = new Intent(getContext(), AddActivity.class);
                startActivity(intent);
            }});

        if (isAboutMode)
            aboutMode();
        else
            filmSelectMode();
    }

    @SuppressLint("RestrictedApi")
    private void filmSelectMode() {
        isAboutMode = false;
        film_about_layout.setVisibility(View.GONE);
        film_root_layout.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);
    }

    @SuppressLint("RestrictedApi")
    private void aboutMode() {
        isAboutMode = true;
        film_about_layout.setVisibility(View.VISIBLE);
        film_root_layout.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);
    }

    private void gotoDetailActivity() {
        if (FilmViewWrapper.getInstance().containsID(getSelectedFilmTag())) {
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra("selectedFilmTag", getSelectedFilmTag());
            startActivityForResult(intent, DETAIL_ACTIVITY_CODE);
        }
        else
            Toast.makeText(getContext(), R.string.noFilmByIDErr, Toast.LENGTH_LONG).show();
    }

    private String getSelectedFilmTag() {
        return selectedFilmTag;
    }

    private void setSelectedFilmTag(String selectedFilmTag) {
        View prev = film_root_layout.findViewWithTag(getSelectedFilmTag());
        if (prev != null)
            prev.setBackgroundColor(film_root_layout.getDrawingCacheBackgroundColor());
        this.selectedFilmTag = selectedFilmTag;
        View v = film_root_layout.findViewWithTag(getSelectedFilmTag());
        if (v != null) {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = v.getContext().getTheme();
            theme.resolveAttribute(R.attr.selectableItemBackgroundBorderless, typedValue, true);
            int color = typedValue.data;
            v.setBackgroundColor(color);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == DETAIL_ACTIVITY_CODE && resultCode == RESULT_OK)
        {
            assert data != null;
            String comment = data.getStringExtra("film_comment");
            boolean isLiked = data.getBooleanExtra("film_is_liked", false);
            Log.d(LOG_TAG, String.format("isLiked={%s}, comment={%s}", Boolean.toString(isLiked), comment));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item_home)
            filmSelectMode();
        else if (id == R.id.item_about)
            aboutMode();
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

    @Override
    public void onResume() {
        super.onResume();

        if (isAboutMode)
            aboutMode();
        else
            filmSelectMode();
    }
}
