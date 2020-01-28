package com.silicium.otusfilmcatalog.ui.cuctomcomponents;

import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import com.silicium.otusfilmcatalog.R;

public class UiComponents {
    private UiComponents() {
    }

    public static void showUnderConstructionSnackBar(@NonNull View v)
    {
        Snackbar.make(v, R.string.under_construction_string, Snackbar.LENGTH_LONG)
                .setAction(v.getResources().getString(R.string.under_construction_action_string), new View.OnClickListener() {
                    @Override
                    public void onClick(@NonNull View view) {
                        Toast.makeText(view.getContext(),R.string.under_construction_answer_string,Toast.LENGTH_LONG).show();
                    }
                }).show();
    }
}
