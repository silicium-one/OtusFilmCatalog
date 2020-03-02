package com.silicium.otusfilmcatalog.logic.model;

import androidx.annotation.NonNull;

public class CinemaDescriptionFactory {

    @NonNull
    public static CinemaDescription getCinemaDescriptionFromGooglePlacesJson(@NonNull PlaceDescriptionJson cinemaDescription) {
        CinemaDescription ret = new CinemaDescription();

        ret.latitude = cinemaDescription.geometry.location.lat;
        ret.longitude = cinemaDescription.geometry.location.lng;
        ret.name = cinemaDescription.name;

        return ret;
    }
}
