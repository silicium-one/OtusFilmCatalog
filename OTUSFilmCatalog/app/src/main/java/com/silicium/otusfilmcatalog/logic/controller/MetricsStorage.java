package com.silicium.otusfilmcatalog.logic.controller;

import com.silicium.otusfilmcatalog.logic.model.Metric;

import java.util.HashSet;

public class MetricsStorage {
    private HashSet<Metric> metrics;

    public MetricsStorage() {
        metrics = new HashSet<>();
        metrics.add(new Metric("total"));
        metrics.add(new Metric("favorites"));
        metrics.add(new Metric("shared"));
        metrics.add(new Metric("cartoon"));
    }
}
