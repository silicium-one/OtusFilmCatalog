package com.silicium.otusfilmcatalog.logic.model;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentWithCallback extends Fragment {
    @Nullable
    protected IGotoFragmentCallback gotoFragmentCallback = null;

    public void setGotoFragmentCallback(IGotoFragmentCallback callback) {
        gotoFragmentCallback = callback;
    }
}
