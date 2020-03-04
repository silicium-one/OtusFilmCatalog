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
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.silicium.otusfilmcatalog.App;
import com.silicium.otusfilmcatalog.BuildConfig;
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
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CinemasMapFragment extends FragmentWithCallback implements CameraListener, MapObjectTapListener {
    public final static String FRAGMENT_TAG = CinemasMapFragment.class.getSimpleName();

    private MapView mapview;
    private PlacesClient placesClient;
    private ClusterizedPlacemarkCollection clusterizedCollection;
    private ImageProvider placemarkImg;
    private BottomSheetBehavior bShBehavior;
    private TextView cinema_name_text_view;
    private TextView cinema_address_text_view;
    private TextView cinema_phone_number_text_view;
    private TextView cinema_url_text_view;
    private ProgressBar content_loading_progress_bar;
    private ImageView cinema_photo_image_view;
    private ProgressBar cinema_photo_loading_progress_bar;

    private Collection<CinemaDescription> actualCinemas;

    public CinemasMapFragment() {
        Places.initialize(App.getApplication().getApplicationContext(), BuildConfig.GOOGLE_PLACES_API_KEY);
    }

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

        // get the bottom sheet view
        LinearLayout bottomSheet = view.findViewById(R.id.cinemas_map_bottom_sheet);
        // init the bottom sheet behavior
        bShBehavior = BottomSheetBehavior.from(bottomSheet);

        bShBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        cinema_name_text_view = view.findViewById(R.id.cinema_name_text_view);
        cinema_address_text_view = view.findViewById(R.id.cinema_address_text_view);
        cinema_phone_number_text_view = view.findViewById(R.id.cinema_phone_number_text_view);
        cinema_url_text_view = view.findViewById(R.id.cinema_url_text_view);

        cinema_url_text_view.setMovementMethod(LinkMovementMethod.getInstance());

        content_loading_progress_bar = view.findViewById(R.id.content_loading_progress_bar);

        cinema_photo_image_view = view.findViewById(R.id.cinema_photo_image_view);
        cinema_photo_loading_progress_bar = view.findViewById(R.id.cinema_photo_loading_progress_bar);
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

        placesClient = Places.createClient(context);

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
        content_loading_progress_bar.setVisibility(View.VISIBLE);
        if (finished) {
            CinemaDescriptionFromGooglePlacesFetcher.getInstance().getCinemasAsync(cameraPosition.getTarget().getLatitude(), cameraPosition.getTarget().getLongitude(),
                    new Consumer<Collection<CinemaDescription>>() {
                        @SuppressLint({"SyntheticAccessor", "LogConditional"})
                        @Override
                        public void accept(Collection<CinemaDescription> cinemaDescriptions) {
                            actualCinemas = cinemaDescriptions;
                            content_loading_progress_bar.setVisibility(View.GONE);
                            //Toast.makeText(App.getApplication().getApplicationContext(), "Кинотеатров в округе найдено: " + cinemaDescriptions.size(), Toast.LENGTH_LONG).show();
                            Log.i(FRAGMENT_TAG, "Кинотеатров в округе найдено: " + cinemaDescriptions.size());
                            clusterizedCollection.clear();
                            List<Point> points = new ArrayList<>();
                            for (CinemaDescription cinemaDescription : cinemaDescriptions) {
                                Point point = new Point(cinemaDescription.latitude, cinemaDescription.longitude);
                                points.add(point);
                            }

                            clusterizedCollection.addPlacemarks(points, placemarkImg, new IconStyle());
                            clusterizedCollection.addTapListener(CinemasMapFragment.this);

                            // Placemarks won't be displayed until this method is called. It must be also called
                            // to force clusters update after collection change
                            clusterizedCollection.clusterPlacemarks(App.getAppResources().getInteger(R.integer.placemark_cluster_radius),
                                    App.getAppResources().getInteger(R.integer.placemark_cluster_min_zoom));
                        }
                    }, new Consumer<ErrorResponse>() {
                        @SuppressLint("SyntheticAccessor")
                        @Override
                        public void accept(ErrorResponse errorResponse) {
                            content_loading_progress_bar.setVisibility(View.GONE);
                            Toast.makeText(App.getApplication().getApplicationContext(), errorResponse.message, Toast.LENGTH_SHORT).show();
                            Log.e(FRAGMENT_TAG, errorResponse.message);
                        }
                    });
        }
    }

    @Nullable
    private CinemaDescription getPlaceIDFromPoint(double latitude, double longitude) {
        if (actualCinemas != null)
            for (CinemaDescription cinemaDescription : actualCinemas)
                //if (cinemaDescription.latitude.compareTo(latitude) == 0 && cinemaDescription.longitude.compareTo(longitude) == 0)
                if (Math.abs(cinemaDescription.latitude - latitude) < 0.001 && Math.abs(cinemaDescription.longitude - longitude) < 0.001)
                    return cinemaDescription;

        return null;
    }

    @Override
    public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
        CinemaDescription pickedCinema = getPlaceIDFromPoint(point.getLatitude(), point.getLongitude());
        if (pickedCinema == null)
            return false;

        List<Place.Field> requiredFields = new ArrayList<>();
        requiredFields.add(Place.Field.NAME);
        requiredFields.add(Place.Field.ADDRESS);
        //requiredFields.add(Place.Field.ADDRESS_COMPONENTS);
        requiredFields.add(Place.Field.PHONE_NUMBER);
        requiredFields.add(Place.Field.WEBSITE_URI);
        requiredFields.add(Place.Field.PHOTO_METADATAS);

        content_loading_progress_bar.setVisibility(View.VISIBLE);
        final FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(pickedCinema.placeID, requiredFields).build();
        Task<FetchPlaceResponse> taskResponse = placesClient.fetchPlace(fetchPlaceRequest);
        taskResponse.addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @SuppressLint("SyntheticAccessor")
            @Override
            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                Place place = fetchPlaceResponse.getPlace();
                cinema_name_text_view.setText(place.getName());
                cinema_address_text_view.setText(place.getAddress());
                cinema_phone_number_text_view.setText(place.getPhoneNumber());

                if (place.getWebsiteUri() == null)
                    cinema_url_text_view.setText(null);
                else
                    cinema_url_text_view.setText(Html.fromHtml(String.format(getResources().getString(R.string.cinema_url_pattern_string), place.getWebsiteUri().toString())));

                List<PhotoMetadata> photoMetadatas = place.getPhotoMetadatas();
                if (photoMetadatas != null && !photoMetadatas.isEmpty()) {
                    cinema_photo_loading_progress_bar.setVisibility(View.VISIBLE);
                    cinema_photo_image_view.setImageBitmap(null);

                    Task<FetchPhotoResponse> photoTask = placesClient.fetchPhoto(FetchPhotoRequest.builder(photoMetadatas.get(0)).build());

                    photoTask.addOnSuccessListener(new OnSuccessListener<FetchPhotoResponse>() {
                        @Override
                        public void onSuccess(FetchPhotoResponse fetchPhotoResponse) {
                            Bitmap bitmap = fetchPhotoResponse.getBitmap();
                            cinema_photo_image_view.setImageBitmap(bitmap);
                            cinema_photo_image_view.setVisibility(View.VISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(App.getApplication().getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                            Log.e(FRAGMENT_TAG, "onFailure: " + e.toString());
                            cinema_photo_image_view.setVisibility(View.GONE);
                        }
                    }).addOnCompleteListener(new OnCompleteListener<FetchPhotoResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<FetchPhotoResponse> task) {
                            cinema_photo_loading_progress_bar.setVisibility(View.GONE);
                            bShBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                    });
                } else {
                    bShBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(App.getApplication().getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                Log.e(FRAGMENT_TAG, "onFailure: " + e.toString());
            }
        }).addOnCompleteListener(new OnCompleteListener<FetchPlaceResponse>() {
            @SuppressLint("SyntheticAccessor")
            @Override
            public void onComplete(@NonNull Task<FetchPlaceResponse> task) {
                content_loading_progress_bar.setVisibility(View.GONE);
            }
        });

        return true;
    }

    public class TextImageProvider extends ImageProvider {
        private final String text;

        TextImageProvider(String text) {
            this.text = text;
        }

        @NonNull
        @Override
        public String getId() {
            return "text_" + text;
        }

        @Nullable
        @Override
        public Bitmap getImage() {
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager manager = (WindowManager) requireActivity().getSystemService(Context.WINDOW_SERVICE);
            if (manager == null) //redundant
                return BitmapFactory.decodeResource(App.getAppResources(), R.drawable.cinema_placemark);
            manager.getDefaultDisplay().getMetrics(metrics);

            Paint textPaint = new Paint();
            textPaint.setTextSize(App.getAppResources().getInteger(R.integer.placemark_cluster_font_size) * metrics.density);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setAntiAlias(true);

            float widthF = textPaint.measureText(text);
            Paint.FontMetrics textMetrics = textPaint.getFontMetrics();
            float heightF = Math.abs(textMetrics.bottom) + Math.abs(textMetrics.top);
            float textRadius = (float) Math.sqrt(widthF * widthF + heightF * heightF) / 2;
            float internalRadius = textRadius + App.getAppResources().getInteger(R.integer.placemark_cluster_marign_size) * metrics.density;
            float externalRadius = internalRadius + App.getAppResources().getInteger(R.integer.placemark_cluster_stroke_size) * metrics.density;

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
    }
}
