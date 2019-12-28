package com.silicium.otusfilmcatalog;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.silicium.otusfilmcatalog.logic.model.FragmentWithCallback;
import com.silicium.otusfilmcatalog.logic.model.GotoFragmentCallbackInterface;
import com.silicium.otusfilmcatalog.ui.MainFragment;

import static com.silicium.otusfilmcatalog.ui.MainFragment.FRAGMENT_TAG;

public class RootActivity extends AppCompatActivity implements GotoFragmentCallbackInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);

        MainFragment fragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null)
            getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.root_fragment, new MainFragment(), FRAGMENT_TAG)
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
        Toast.makeText(this, "GotoDetailFragment: " + filmID, Toast.LENGTH_LONG).show();
    }
}
