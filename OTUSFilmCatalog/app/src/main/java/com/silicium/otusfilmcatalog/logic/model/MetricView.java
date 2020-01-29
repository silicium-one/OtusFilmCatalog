package com.silicium.otusfilmcatalog.logic.model;

import androidx.annotation.NonNull;

public class MetricView {
    @NonNull
    public final String metricTag;
    @NonNull
    public final String metricName;
    //public Boolean visibility; redundant

    public MetricView(@NonNull String metricTag, @NonNull String metricName) {
        this.metricTag = metricTag;
        this.metricName = metricName;
        //this.visibility = true;
    }
}
