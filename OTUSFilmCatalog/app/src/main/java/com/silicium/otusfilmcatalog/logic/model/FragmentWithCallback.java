package com.silicium.otusfilmcatalog.logic.model;

import androidx.fragment.app.Fragment;

public class FragmentWithCallback extends Fragment {
    protected GotoFragmentCallbackInterface gotoFragmentCallback = null;
    public void setGotoFragmentCallback(GotoFragmentCallbackInterface callback)
    {
        gotoFragmentCallback = callback;
    }
}
