package com.silicium.otusfilmcatalog.logic.model;

public interface IGotoFragmentCallback {
    void gotoDetailFragment(String filmID);

    void gotoAddFragment();

    void gotoMainFragment();

    void gotoAboutFragment();
}
