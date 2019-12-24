package com.silicium.otusfilmcatalog.ui.cuctomcomponents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.silicium.otusfilmcatalog.R;

public class AddMoreDialogFragment extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_add_bottom_dialog, container,
                false);

        // get the views and attach the listener

        return view;

    }
}
