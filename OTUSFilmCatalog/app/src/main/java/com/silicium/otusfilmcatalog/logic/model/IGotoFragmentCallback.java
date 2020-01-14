package com.silicium.otusfilmcatalog.logic.model;

public interface IGotoFragmentCallback {
    void GotoDetailFragment (String filmID);
    void GotoAddFragment ();
    void GotoMainFragment ();
    void GotoAboutFragment ();
}
