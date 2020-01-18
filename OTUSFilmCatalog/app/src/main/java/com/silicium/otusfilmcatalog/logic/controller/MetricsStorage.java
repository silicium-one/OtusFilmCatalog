package com.silicium.otusfilmcatalog.logic.controller;

import com.silicium.otusfilmcatalog.logic.model.Metric;
import com.silicium.otusfilmcatalog.ui.widget.MetricWidgetUtils;

import java.util.HashSet;

class MetricsStorage {
    final static String TOTAL_TAG = "total"; // Фильмов всего
    final static String FAVORITES_TAG = "favorites"; // Фильмов в избранном
    final static String SHARED_TAG = "shared"; // количество фильмов, которыми поделились
    final static String CARTOON_TAG = "cartoon"; // мультфильмов

    private HashSet<Metric> metrics;

    MetricsStorage() {
        metrics = new HashSet<>();
        metrics.add(new Metric(TOTAL_TAG));
        metrics.add(new Metric(FAVORITES_TAG));
        metrics.add(new Metric(SHARED_TAG));
        metrics.add(new Metric(CARTOON_TAG));
    }

    /**
     * Увеличить метрику на 1
     * @param tag метрика, которую надо увеличить
     * @return false, если нет такого тэга, инача true
     */
    Boolean Increment(String tag) {
        for (Metric metric : metrics) {
            if (metric.metricTag.equals(tag)) {
                metric.value++;
                MetricWidgetUtils.notifyWidget(metric.metricTag, metric.value);
                return true;
            }
        }
        return false;
    }
}
