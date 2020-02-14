package com.silicium.otusfilmcatalog.logic.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MovieDescriptionJson {

    @SerializedName("popularity")
    @Expose
    public float popularity;
    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("video")
    @Expose
    public boolean video;
    @SerializedName("vote_count")
    @Expose
    public int voteCount;
    @SerializedName("vote_average")
    @Expose
    public float voteAverage;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("release_date")
    @Expose
    public String releaseDate;
    @SerializedName("original_language")
    @Expose
    public String originalLanguage;
    @SerializedName("original_title")
    @Expose
    public String originalTitle;
    @SerializedName("genre_ids")
    @Expose
    public List<Integer> genreIds = new ArrayList<Integer>();
    @SerializedName("backdrop_path")
    @Expose
    public String backdropPath;
    @SerializedName("adult")
    @Expose
    public boolean adult;
    @SerializedName("overview")
    @Expose
    public String overview;
    @SerializedName("poster_path")
    @Expose
    public String posterPath;

}