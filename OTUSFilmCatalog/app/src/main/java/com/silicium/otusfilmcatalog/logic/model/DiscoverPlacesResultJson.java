package com.silicium.otusfilmcatalog.logic.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DiscoverPlacesResultJson {
    @NonNull
    @SerializedName("html_attributions")
    @Expose
    public List<Object> htmlAttributions = new ArrayList<Object>();
    @Nullable
    @SerializedName("next_page_token")
    @Expose
    public String nextPageToken;
    @NonNull
    @SerializedName("results")
    @Expose
    public List<PlaceDescriptionJson> results = new ArrayList<PlaceDescriptionJson>();
    @NonNull
    @SerializedName("status")
    @Expose
    public String status = "";
    @Nullable
    @SerializedName("error_message")
    @Expose
    public String errorMessage;
}
