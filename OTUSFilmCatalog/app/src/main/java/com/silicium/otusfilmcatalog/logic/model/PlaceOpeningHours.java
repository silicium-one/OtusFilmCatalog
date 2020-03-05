
package com.silicium.otusfilmcatalog.logic.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaceOpeningHours {
    @SerializedName("open_now")
    @Expose
    public boolean openNow;
}
