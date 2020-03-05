package com.silicium.otusfilmcatalog.logic.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DiscoverMoviesResultJson {
    @SerializedName("page")
    @Expose
    public int page;
    @SerializedName("total_results")
    @Expose
    public int totalResults;
    @SerializedName("total_pages")
    @Expose
    public int totalPages;
    @NonNull
    @SerializedName("results")
    @Expose
    public List<MovieDescriptionJson> results = new ArrayList<>();
}