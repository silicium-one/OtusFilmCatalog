package com.silicium.otusfilmcatalog;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.silicium.otusfilmcatalog.logic.model.FragmentWithCallback;
import com.silicium.otusfilmcatalog.logic.model.GotoFragmentCallbackInterface;
import com.silicium.otusfilmcatalog.ui.DetailFragment;
import com.silicium.otusfilmcatalog.ui.MainFragment;

public class RootActivity extends AppCompatActivity implements GotoFragmentCallbackInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);

        MainFragment fragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MainFragment.FRAGMENT_TAG);
        if (fragment == null)
            getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.root_fragment, new MainFragment(), MainFragment.FRAGMENT_TAG)
                .commit();
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);

        if (fragment instanceof FragmentWithCallback) {
            ((FragmentWithCallback) fragment).setGotoFragmentCallback(this);
        }
    }

    @Override
    public void GotoDetailFragment(String filmID) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.root_fragment, DetailFragment.newInstance(filmID), DetailFragment.FRAGMENT_TAG)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }
}
