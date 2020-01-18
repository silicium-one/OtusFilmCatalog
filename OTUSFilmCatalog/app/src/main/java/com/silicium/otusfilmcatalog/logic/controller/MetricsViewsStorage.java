package com.silicium.otusfilmcatalog.logic.controller;

import com.silicium.otusfilmcatalog.logic.model.MetricView;

import java.util.HashSet;

public class MetricsViewsStorage {
    private HashSet<MetricView> metricsViews;

    public MetricsViewsStorage()
    {
        metricsViews = new HashSet<>();
        metricsViews.add(new MetricView( "total", "Всего"));
        metricsViews.add(new MetricView( "favorites", "Избранные"));
        metricsViews.add(new MetricView( "shared","Поделились"));
        metricsViews.add(new MetricView( "cartoon", "Мультфильмы"));
    }
}
