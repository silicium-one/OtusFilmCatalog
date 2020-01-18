package com.silicium.otusfilmcatalog.logic.model;

public class MetricView {
    public final String metricTag;
    public final String metricName;
    //public Boolean visibility; redundand

    public MetricView(String metricTag, String metricName) {
        this.metricTag = metricTag;
        this.metricName = metricName;
        //this.visibility = true;
    }
}
