
package com.silicium.otusfilmcatalog.logic.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PlacePhoto {

    @SerializedName("height")
    @Expose
    public int height;
    @SerializedName("html_attributions")
    @Expose
    public List<String> htmlAttributions = new ArrayList<String>();
    @SerializedName("photo_reference")
    @Expose
    public String photoReference;
    @SerializedName("width")
    @Expose
    public int width;

}
