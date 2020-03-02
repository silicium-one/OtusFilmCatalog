package com.silicium.otusfilmcatalog.ui;


import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.silicium.otusfilmcatalog.App;
import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.controller.CinemaDescriptionFromGooglePlacesFetcher;
import com.silicium.otusfilmcatalog.logic.model.CinemaDescription;
import com.silicium.otusfilmcatalog.logic.model.ErrorResponse;
import com.silicium.otusfilmcatalog.logic.model.FragmentWithCallback;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateSource;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.mapview.MapView;

import java.util.Collection;

public class CinemasMapFragment extends FragmentWithCallback implements CameraListener {
    public final static String FRAGMENT_TAG = CinemasMapFragment.class.getSimpleName();

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
        mapview.getMap().addCameraListener(this);
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

    @Override
    public void onCameraPositionChanged(@NonNull Map map, @NonNull CameraPosition cameraPosition, @NonNull CameraUpdateSource cameraUpdateSource, boolean finished) {
        if (finished) {
            CinemaDescriptionFromGooglePlacesFetcher.getInstance().getCinemasAsync(cameraPosition.getTarget().getLatitude(), cameraPosition.getTarget().getLongitude(),
                    new Consumer<Collection<CinemaDescription>>() {
                        @Override
                        public void accept(Collection<CinemaDescription> cinemaDescriptions) {
                            Toast.makeText(App.getApplication().getApplicationContext(), "Кинотеатров в округе найдено: " + cinemaDescriptions.size(), Toast.LENGTH_LONG).show();
                        }
                    }, new Consumer<ErrorResponse>() {
                        @Override
                        public void accept(ErrorResponse errorResponse) {
                            Toast.makeText(App.getApplication().getApplicationContext(), errorResponse.message, Toast.LENGTH_LONG).show();
                            Log.e(FRAGMENT_TAG, errorResponse.message);
                        }
                    });
        }
    }
}
