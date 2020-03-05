package com.silicium.otusfilmcatalog.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.model.FragmentWithCallback;
import com.silicium.otusfilmcatalog.ui.cuctomcomponents.UiComponents;

public class AboutFragment extends FragmentWithCallback {

    public final static String FRAGMENT_TAG = AboutFragment.class.getSimpleName();

    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final BottomNavigationView bnv = view.findViewById(R.id.bottom_about_navigation);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                UiComponents.showUnderConstructionSnackBar(bnv);
                return false;
            }
        });

        bnv.getMenu().getItem(0).setCheckable(false);
        bnv.getMenu().getItem(1).setCheckable(false);
        bnv.getMenu().getItem(2).setCheckable(false);    }
}
