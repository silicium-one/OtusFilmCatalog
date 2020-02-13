package com.silicium.otusfilmcatalog.logic.model;

import androidx.annotation.NonNull;

public interface IGotoFragmentCallback {
    void gotoDetailFragment(@NonNull String filmID);

    void gotoAddFragment();

    void gotoMainFragment();

    void gotoAboutFragment();
}
