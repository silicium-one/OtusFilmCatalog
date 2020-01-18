package com.silicium.otusfilmcatalog.ui.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;

import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.controller.MetricsViewsStorage;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link MetricsWidgetConfigureActivity MetricsWidgetConfigureActivity}
 */
public class MetricsWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, @NonNull AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Boolean totalVisibility = MetricsWidgetConfigureActivity.loadBoolean(context, appWidgetId, "total.visibility");
        Integer totalValue = MetricsWidgetConfigureActivity.loadInteger(context, appWidgetId, "total.value");
        Boolean favoritesVisibility = MetricsWidgetConfigureActivity.loadBoolean(context, appWidgetId, "favorites.visibility");
        Integer favoritesValue = MetricsWidgetConfigureActivity.loadInteger(context, appWidgetId, "favorites.value");
        Boolean sharedVisibility = MetricsWidgetConfigureActivity.loadBoolean(context, appWidgetId, "shared.visibility");
        Integer sharedValue = MetricsWidgetConfigureActivity.loadInteger(context, appWidgetId, "shared.value");
        Boolean cartoonVisibility = MetricsWidgetConfigureActivity.loadBoolean(context, appWidgetId, "cartoon.visibility");
        Integer cartoonValue = MetricsWidgetConfigureActivity.loadInteger(context, appWidgetId, "cartoon.value");

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.metrics_widget);
        views.setViewVisibility(R.id.appwidget_total_header, totalVisibility ? View.VISIBLE : View.GONE);
        views.setViewVisibility(R.id.appwidget_total_value, totalVisibility ? View.VISIBLE : View.GONE);
        views.setViewVisibility(R.id.appwidget_favorites_header, favoritesVisibility ? View.VISIBLE : View.GONE);
        views.setViewVisibility(R.id.appwidget_favorites_value, favoritesVisibility ? View.VISIBLE : View.GONE);
        views.setViewVisibility(R.id.appwidget_shared_header, sharedVisibility ? View.VISIBLE : View.GONE);
        views.setViewVisibility(R.id.appwidget_shared_value, sharedVisibility ? View.VISIBLE : View.GONE);
        views.setViewVisibility(R.id.appwidget_cartoon_header, cartoonVisibility ? View.VISIBLE : View.GONE);
        views.setViewVisibility(R.id.appwidget_cartoon_value, cartoonVisibility ? View.VISIBLE : View.GONE);

        views.setTextViewText(R.id.appwidget_total_value, totalValue.toString());
        views.setTextViewText(R.id.appwidget_favorites_value, favoritesValue.toString());
        views.setTextViewText(R.id.appwidget_shared_value, sharedValue.toString());
        views.setTextViewText(R.id.appwidget_cartoon_value, cartoonValue.toString());

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, @NonNull int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, @NonNull int[] appWidgetIds) {
        MetricsViewsStorage instance = MetricsViewsStorage.getInstance();
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            for (String tag : instance.getTags()) {
                MetricsWidgetConfigureActivity.deleteKey(context, appWidgetId, tag + ".visibility");
                MetricsWidgetConfigureActivity.deleteKey(context, appWidgetId, tag + ".value");
            }
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

