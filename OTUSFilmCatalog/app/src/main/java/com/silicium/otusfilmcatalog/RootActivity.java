package com.silicium.otusfilmcatalog;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.silicium.otusfilmcatalog.ui.MainFragment;

public class RootActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);

        MainFragment fragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MainFragment.LOG_TAG);
        if (fragment == null)
            getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.root_fragment, new MainFragment(), MainFragment.LOG_TAG)
                .commit();
    }
}
