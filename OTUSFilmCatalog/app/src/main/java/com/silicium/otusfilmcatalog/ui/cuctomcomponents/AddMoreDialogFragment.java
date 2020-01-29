package com.silicium.otusfilmcatalog.ui.cuctomcomponents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.silicium.otusfilmcatalog.R;

public class AddMoreDialogFragment extends BottomSheetDialogFragment {

    @Nullable
    private final View.OnClickListener itemOnClickListener;

    public AddMoreDialogFragment(@Nullable View.OnClickListener itemOnClickListener) {
        this.itemOnClickListener = itemOnClickListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_bottom_dialog, container,
                false);

        // get the views and attach the listener
        View btn_add_photo = view.findViewById(R.id.btn_add_photo);
        btn_add_photo.setOnClickListener(itemOnClickListener);

        View btn_pick_category = view.findViewById(R.id.btn_pick_category);
        btn_pick_category.setOnClickListener(itemOnClickListener);

        View btn_add_one_more = view.findViewById(R.id.btn_add_one_more);
        btn_add_one_more.setOnClickListener(itemOnClickListener);

        return view;

    }
}
