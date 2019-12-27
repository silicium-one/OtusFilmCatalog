package com.silicium.otusfilmcatalog;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.silicium.otusfilmcatalog.ui.MainFragment;

public class RootActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.root_fragment, new MainFragment(), MainFragment.LOG_TAG).commit();

    }
}
