package com.silicium.otusfilmcatalog.logic.model;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.silicium.otusfilmcatalog.RootActivity;

public class FragmentWithCallback extends Fragment {
    @NonNull
    protected IGotoFragmentCallback gotoFragmentCallback = new RootActivity(); // будет уставновлен позже в setGotoFragmentCallback

    public void setGotoFragmentCallback(@NonNull IGotoFragmentCallback callback) {
        gotoFragmentCallback = callback;
    }
}
