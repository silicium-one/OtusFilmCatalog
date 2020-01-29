package com.silicium.otusfilmcatalog.logic.controller;

import androidx.annotation.NonNull;

import com.silicium.otusfilmcatalog.logic.model.IMetricNotifier;
import com.silicium.otusfilmcatalog.logic.model.Metric;
import com.silicium.otusfilmcatalog.ui.widget.MetricWidgetUtils;

import java.util.HashSet;

public class MetricsStorage implements IMetricNotifier {
    private static volatile IMetricNotifier instance = null;
    @NonNull
    public static IMetricNotifier getMetricNotifier()
    {
        if (instance == null)
            synchronized (MetricsStorage.class) {
                if (instance == null)
                    instance = new MetricsStorage();
            }
        return instance;
    }

    @NonNull
    private final HashSet<Metric> metrics;
    private MetricsStorage() {
        metrics = new HashSet<>();
        metrics.add(new Metric(TOTAL_TAG));
        metrics.add(new Metric(FAVORITES_TAG));
        metrics.add(new Metric(SHARED_TAG));
        metrics.add(new Metric(CARTOON_TAG));
    }

    private void updateWidgetView()
    {
        for (Metric metric : metrics)
            MetricWidgetUtils.notifyWidget(metric.metricTag, metric.value);
    }

    /**
     * Переслать наблюдателям все метрики
     */
    @Override
    public void notifyObserversDataChanged() {
        updateWidgetView();
    }

    /**
     * Увеличить метрику на 1 и оповестить наблюдателей
     *
     * @param tag ID метрики
     */
    @Override
    public void increment(@NonNull String tag) {
        for (Metric metric : metrics) {
            if (metric.metricTag.equals(tag)) {
                metric.value++;
                MetricWidgetUtils.notifyWidget(metric.metricTag, metric.value);
                return;
            }
        }
    }

    /**
     * Уменьшить метрику на 1 и оповестить наблюдателей
     *
     * @param tag ID метрики
     */
    @Override
    public void decrement(@NonNull String tag) {
        for (Metric metric : metrics) {
            if (metric.metricTag.equals(tag)) {
                metric.value--;
                MetricWidgetUtils.notifyWidget(metric.metricTag, metric.value);
                return;
            }
        }
    }
}
