package com.silicium.otusfilmcatalog.logic.model;

import android.graphics.Bitmap;

import java.util.HashSet;
import java.util.Set;

public class FilmDescription {

    public final String ID;
    public final Set<FilmGenre> Genre = new HashSet<>();
    public String Name;
    public String Description;
    public String Url;
    public Bitmap Cover;
    FilmDescription(String ID) {
        this.ID = ID;
    }
    public enum FilmGenre {
        comedy,
        series,
        cartoon,
    }
}


