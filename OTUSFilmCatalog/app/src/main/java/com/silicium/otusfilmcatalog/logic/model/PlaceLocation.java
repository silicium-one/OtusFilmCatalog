
package com.silicium.otusfilmcatalog.logic.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaceLocation {

    @SerializedName("lat")
    @Expose
    public double lat;
    @SerializedName("lng")
    @Expose
    public double lng;

}
