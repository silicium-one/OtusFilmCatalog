package com.silicium.otusfilmcatalog.logic.model;

import android.graphics.Bitmap;

public class FilmDescription {

    FilmDescription(String ID)
    {
        this.ID = ID;
    }

    public final String ID;
    public String Name;
    public String Description;
    public String Url;
    public Bitmap Cover;
}


