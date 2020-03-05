
package com.silicium.otusfilmcatalog.logic.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaceViewport {

    @SerializedName("northeast")
    @Expose
    public PlaceLocation northeast;
    @SerializedName("southwest")
    @Expose
    public PlaceLocation southwest;

}
