package com.silicium.otusfilmcatalog.ui;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.controller.FilmDescriptionStorage;
import com.silicium.otusfilmcatalog.logic.model.FragmentWithCallback;
import com.silicium.otusfilmcatalog.logic.view.FilmItemAdapter;
import com.silicium.otusfilmcatalog.logic.view.FilmViewWrapper;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends FragmentWithCallback implements NavigationView.OnNavigationItemSelectedListener {

    private String selectedFilmTag = ""; // TODO: оптимизировать на доступ по индексу
    public final static String FRAGMENT_TAG = MainFragment.class.getSimpleName();
    private RecyclerView film_RecyclerView;
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

        film_RecyclerView = view.findViewById(R.id.film_RecyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false);
        film_RecyclerView.setLayoutManager(linearLayoutManager);
        List<String> items = new ArrayList<>(FilmDescriptionStorage.getInstance().getFilmsIDs());
        film_RecyclerView.setAdapter(new FilmItemAdapter(LayoutInflater.from(getContext()), items));
        film_RecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
//        film_RecyclerView.removeAllViews();
//        for(View v : FilmViewWrapper.getInstance().GetFilmViews(getContext(), new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setSelectedFilmTag(v.getTag().toString());
//                gotoDetailFragment();
//            }}))
//            film_RecyclerView.addView(v);

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

        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    gotoFragmentCallback.gotoAddFragment();
                }
                catch (NullPointerException e)
                {
                    e.printStackTrace();
                }
            }});
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

    private String getSelectedFilmTag() {
        return selectedFilmTag;
    }

    public void setSelectedFilmTag(String selectedFilmTag) { //todo: проверить, как работает для невидимых элментов
        View prev = film_RecyclerView.findViewWithTag(getSelectedFilmTag());
        if (prev != null)
            prev.setBackgroundColor(film_RecyclerView.getDrawingCacheBackgroundColor());
        this.selectedFilmTag = selectedFilmTag;
        View v = film_RecyclerView.findViewWithTag(getSelectedFilmTag());
        if (v != null) {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = v.getContext().getTheme();
            theme.resolveAttribute(R.attr.selectableItemBackgroundBorderless, typedValue, true);
            int color = typedValue.data;
            v.setBackgroundColor(color);
        }
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
}
