
package com.silicium.otusfilmcatalog.logic.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaceGeometry {

    @SerializedName("location")
    @Expose
    public PlaceLocation location;
    @SerializedName("viewport")
    @Expose
    public PlaceViewport viewport;

}
