package com.silicium.otusfilmcatalog.logic.controller;

import androidx.annotation.NonNull;

import com.silicium.otusfilmcatalog.logic.model.MetricView;

import java.util.HashSet;

public class MetricsViewsStorage {
    private HashSet<MetricView> metricsViews;

    private static volatile MetricsViewsStorage instance = null;

    public static MetricsViewsStorage getInstance() {
        if (instance == null)
            synchronized (MetricsViewsStorage.class) {
                if (instance == null)
                    instance = new MetricsViewsStorage();
            }
        return instance;
    }

    private MetricsViewsStorage() {
        metricsViews = new HashSet<>();
        metricsViews.add(new MetricView("total", "Всего"));
        metricsViews.add(new MetricView("favorites", "Избранные"));
        metricsViews.add(new MetricView("shared", "Поделились"));
        metricsViews.add(new MetricView("cartoon", "Мультфильмы"));
    }

    @NonNull
    public HashSet<String> getTags() {
        HashSet<String> ret = new HashSet<>();
        for (MetricView element : metricsViews)
            ret.add(element.metricTag);

        return ret;
    }

    @NonNull
    public String getNameByTag(String tag)
    {
        for (MetricView element : metricsViews)
            if (element.metricTag == tag)
                return element.metricName;

        return "";
    }


}
