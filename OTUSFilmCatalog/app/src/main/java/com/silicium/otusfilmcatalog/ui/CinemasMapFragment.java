package com.silicium.otusfilmcatalog.ui;


import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.model.FragmentWithCallback;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.mapview.MapView;

public class CinemasMapFragment extends FragmentWithCallback {
    public final static String FRAGMENT_TAG = AboutFragment.class.getSimpleName();

    private MapView mapview;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cinemas_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapview = view.findViewById(R.id.mapview);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MapKitFactory.initialize(context);
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @SuppressLint("SyntheticAccessor")
                    @Override
                    public void onSuccess(Location location) {
                        if (location == null) {
                            Toast.makeText(CinemasMapFragment.this.requireContext(), R.string.error_location, Toast.LENGTH_LONG).show();
                        } else {
                            float zoom = getResources().getInteger(R.integer.camera_zoom_size);
                            Point myLocation = new Point(location.getLatitude(), location.getLongitude());
                            mapview.getMap().move(new CameraPosition(myLocation, zoom, 0f, 0f),
                                    new Animation(Animation.Type.SMOOTH, getResources().getInteger(R.integer.camera_smooth_time_s)),
                                    null);
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();

        MapKitFactory.getInstance().onStart();
        mapview.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

        mapview.onStop();
        MapKitFactory.getInstance().onStop();
    }
}
