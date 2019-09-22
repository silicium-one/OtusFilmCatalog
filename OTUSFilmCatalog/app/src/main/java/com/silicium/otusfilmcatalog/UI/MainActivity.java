package com.silicium.otusfilmcatalog.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.silicium.otusfilmcatalog.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout film1layout = findViewById(R.id.film1layout);
        film1layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(this.getClass().getName(), "film1 clicked!");
            }
        });

        LinearLayout film2layout = findViewById(R.id.film2layout);
        film2layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(this.getClass().getName(), "film2 clicked!");
            }
        });

        LinearLayout film3layout = findViewById(R.id.film3layout);
        film3layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(this.getClass().getName(), "film3 clicked!");
            }
        });

    }
}
