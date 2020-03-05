
package com.silicium.otusfilmcatalog.logic.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PlaceDescriptionJson {

    @SerializedName("geometry")
    @Expose
    public PlaceGeometry geometry;
    @SerializedName("icon")
    @Expose
    public String icon;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("photos")
    @Expose
    public List<PlacePhoto> photos = new ArrayList<PlacePhoto>();
    @SerializedName("place_id")
    @Expose
    public String placeId;
    @SerializedName("plus_code")
    @Expose
    public PlacePlusCode plusCode;
    @SerializedName("rating")
    @Expose
    public float rating;
    @SerializedName("reference")
    @Expose
    public String reference;
    @SerializedName("scope")
    @Expose
    public String scope;
    @SerializedName("types")
    @Expose
    public List<String> types = new ArrayList<String>();
    @SerializedName("user_ratings_total")
    @Expose
    public int userRatingsTotal;
    @SerializedName("vicinity")
    @Expose
    public String vicinity;
    @SerializedName("opening_hours")
    @Expose
    public PlaceOpeningHours openingHours;

}
