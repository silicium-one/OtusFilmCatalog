package com.silicium.otusfilmcatalog.logic.model;

import androidx.fragment.app.Fragment;

public class FragmentWithCallback extends Fragment {
    protected IGotoFragmentCallback gotoFragmentCallback = null;
    public void setGotoFragmentCallback(IGotoFragmentCallback callback)
    {
        gotoFragmentCallback = callback;
    }
}
