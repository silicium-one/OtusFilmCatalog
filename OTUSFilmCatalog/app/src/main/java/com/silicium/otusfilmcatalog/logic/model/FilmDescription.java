package com.silicium.otusfilmcatalog.logic.model;

import android.graphics.Bitmap;

import java.util.HashSet;
import java.util.Set;

public class FilmDescription {

    public enum FilmGenre {
        comedy,
        series,
        cartoon,
    }

    FilmDescription(String ID)
    {
        this.ID = ID;
    }

    public final String ID;
    public String Name;
    public String Description;
    public String Url;
    public Bitmap Cover;
    public final Set<FilmGenre> Genre = new HashSet<>();
}


