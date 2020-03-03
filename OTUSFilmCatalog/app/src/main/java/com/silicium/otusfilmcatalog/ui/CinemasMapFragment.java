package com.silicium.otusfilmcatalog.ui;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.yandex.mapkit.map.Cluster;
import com.yandex.mapkit.map.ClusterListener;
import com.yandex.mapkit.map.ClusterizedPlacemarkCollection;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class CinemasMapFragment extends FragmentWithCallback implements CameraListener {
    public final static String FRAGMENT_TAG = CinemasMapFragment.class.getSimpleName();

    private MapView mapview;
    ClusterizedPlacemarkCollection clusterizedCollection;
    ImageProvider placemarkImg;

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

        clusterizedCollection = mapview.getMap().getMapObjects().addClusterizedPlacemarkCollection(new ClusterListener() {
            @Override
            public void onClusterAdded(@NonNull Cluster cluster) {
                cluster.getAppearance().setIcon(
                        new TextImageProvider(Integer.toString(cluster.getSize())));
                //cluster.addClusterTapListener(this); //todo: сделать приближение, если нажимаешь на кластер
            }
        });
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

        placemarkImg = ImageProvider.fromResource(context, R.drawable.cinema_placemark);
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
            Log.d(FRAGMENT_TAG, "onCameraPositionChanged: " + String.format(Locale.ENGLISH, "%.3f,%.3f %.3f", cameraPosition.getTarget().getLatitude(), cameraPosition.getTarget().getLongitude(), cameraPosition.getTarget().getLongitude()));
            CinemaDescriptionFromGooglePlacesFetcher.getInstance().getCinemasAsync(cameraPosition.getTarget().getLatitude(), cameraPosition.getTarget().getLongitude(),
                    new Consumer<Collection<CinemaDescription>>() {
                        @Override
                        public void accept(Collection<CinemaDescription> cinemaDescriptions) {
                            Toast.makeText(App.getApplication().getApplicationContext(), "Кинотеатров в округе найдено: " + cinemaDescriptions.size(), Toast.LENGTH_LONG).show();
                            clusterizedCollection.clear();
                            List<Point> points = new ArrayList<>();
                            for (CinemaDescription cinemaDescription: cinemaDescriptions) {
                                Point point = new Point(cinemaDescription.latitude,cinemaDescription.longitude);
                                points.add(point);
                            }
                            clusterizedCollection.addPlacemarks(points, placemarkImg, new IconStyle());

                            // Placemarks won't be displayed until this method is called. It must be also called
                            // to force clusters update after collection change
                            clusterizedCollection.clusterPlacemarks(App.getAppResources().getInteger(R.integer.placemark_clyster_radius),
                                    App.getAppResources().getInteger(R.integer.placemark_clyster_min_zoom));
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

    public class TextImageProvider extends ImageProvider {
        @NonNull
        @Override
        public String getId() {
            return "text_" + text;
        }

        private final String text;

        @Nullable
        @Override
        public Bitmap getImage() {
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager manager = (WindowManager)requireActivity().getSystemService(Context.WINDOW_SERVICE);
            if (manager == null) //redundant
                return BitmapFactory.decodeResource(App.getAppResources(), R.drawable.cinema_placemark);
            manager.getDefaultDisplay().getMetrics(metrics);

            Paint textPaint = new Paint();
            textPaint.setTextSize(App.getAppResources().getInteger(R.integer.placemark_clyster_font_size) * metrics.density);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setAntiAlias(true);

            float widthF = textPaint.measureText(text);
            Paint.FontMetrics textMetrics = textPaint.getFontMetrics();
            float heightF = Math.abs(textMetrics.bottom) + Math.abs(textMetrics.top);
            float textRadius = (float)Math.sqrt(widthF * widthF + heightF * heightF) / 2;
            float internalRadius = textRadius + App.getAppResources().getInteger(R.integer.placemark_clyster_marign_size) * metrics.density;
            float externalRadius = internalRadius + App.getAppResources().getInteger(R.integer.placemark_clyster_stroke_size) * metrics.density;

            int width = (int) (2 * externalRadius + 0.5);

            @SuppressWarnings("SuspiciousNameCombination") Bitmap bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            Paint backgroundPaint = new Paint();
            backgroundPaint.setAntiAlias(true);
            backgroundPaint.setColor(Color.RED);
            canvas.drawCircle(width / 2f, width / 2f, externalRadius, backgroundPaint);

            backgroundPaint.setColor(Color.WHITE);
            canvas.drawCircle(width / 2f, width / 2f, internalRadius, backgroundPaint);

            canvas.drawText(
                    text,
                    width / 2f,
                    width / 2f - (textMetrics.ascent + textMetrics.descent) / 2,
                    textPaint);

            return bitmap;
        }

        TextImageProvider(String text) {
            this.text = text;
        }
    }
}
