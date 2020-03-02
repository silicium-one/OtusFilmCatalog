
package com.silicium.otusfilmcatalog.logic.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlacePlusCode {

    @SerializedName("compound_code")
    @Expose
    public String compoundCode;
    @SerializedName("global_code")
    @Expose
    public String globalCode;

}
