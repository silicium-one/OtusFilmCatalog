package com.silicium.otusfilmcatalog.logic.model;

import androidx.annotation.NonNull;

public class Metric {
    @NonNull
    public final String metricTag;
    public int value;

    public Metric(@NonNull String metricTag) {
        this.metricTag = metricTag;
    }
}
