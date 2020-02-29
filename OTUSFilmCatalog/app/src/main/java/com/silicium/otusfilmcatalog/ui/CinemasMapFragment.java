package com.silicium.otusfilmcatalog.ui;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.model.FragmentWithCallback;
import com.yandex.mapkit.MapKitFactory;

public class CinemasMapFragment extends FragmentWithCallback {
    public final static String FRAGMENT_TAG = AboutFragment.class.getSimpleName();

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cinemas_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MapKitFactory.initialize(context);
    }

    @Override
    public void onStart() {
        super.onStart();

        MapKitFactory.getInstance().onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

        MapKitFactory.getInstance().onStop();
    }
}
