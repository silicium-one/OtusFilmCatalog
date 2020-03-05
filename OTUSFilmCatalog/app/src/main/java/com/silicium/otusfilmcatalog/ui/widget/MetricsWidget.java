package com.silicium.otusfilmcatalog.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;

import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.RootActivity;
import com.silicium.otusfilmcatalog.logic.controller.MetricsViewsStorage;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class MetricsWidget extends AppWidgetProvider {

    private final static String WIDGET_CLICK = "MetricsWidget.widget_click";

    static void updateAppWidget(@NonNull Context context, @NonNull AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Boolean totalVisibility = MetricWidgetUtils.loadBoolean(context, appWidgetId, "total.visibility");
        Integer totalValue = MetricWidgetUtils.loadInteger(context, appWidgetId, "total.value");
        Boolean favoritesVisibility = MetricWidgetUtils.loadBoolean(context, appWidgetId, "favorites.visibility");
        Integer favoritesValue = MetricWidgetUtils.loadInteger(context, appWidgetId, "favorites.value");
        Boolean sharedVisibility = MetricWidgetUtils.loadBoolean(context, appWidgetId, "shared.visibility");
        Integer sharedValue = MetricWidgetUtils.loadInteger(context, appWidgetId, "shared.value");
        Boolean cartoonVisibility = MetricWidgetUtils.loadBoolean(context, appWidgetId, "cartoon.visibility");
        Integer cartoonValue = MetricWidgetUtils.loadInteger(context, appWidgetId, "cartoon.value");

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.metrics_widget);
        views.setViewVisibility(R.id.appwidget_total_header, totalVisibility ? View.VISIBLE : View.INVISIBLE);
        views.setViewVisibility(R.id.appwidget_total_value, totalVisibility ? View.VISIBLE : View.INVISIBLE);
        views.setViewVisibility(R.id.appwidget_favorites_header, favoritesVisibility ? View.VISIBLE : View.INVISIBLE);
        views.setViewVisibility(R.id.appwidget_favorites_value, favoritesVisibility ? View.VISIBLE : View.INVISIBLE);
        views.setViewVisibility(R.id.appwidget_shared_header, sharedVisibility ? View.VISIBLE : View.INVISIBLE);
        views.setViewVisibility(R.id.appwidget_shared_value, sharedVisibility ? View.VISIBLE : View.INVISIBLE);
        views.setViewVisibility(R.id.appwidget_cartoon_header, cartoonVisibility ? View.VISIBLE : View.INVISIBLE);
        views.setViewVisibility(R.id.appwidget_cartoon_value, cartoonVisibility ? View.VISIBLE : View.INVISIBLE);

        views.setTextViewText(R.id.appwidget_total_value, totalValue.toString());
        views.setTextViewText(R.id.appwidget_favorites_value, favoritesValue.toString());
        views.setTextViewText(R.id.appwidget_shared_value, sharedValue.toString());
        views.setTextViewText(R.id.appwidget_cartoon_value, cartoonValue.toString());


        Intent intent = new Intent(context, MetricsWidget.class);
        intent.setAction(WIDGET_CLICK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.metrics_widget, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(@NonNull Context context, @NonNull AppWidgetManager appWidgetManager, @NonNull int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(@NonNull Context context, @NonNull int[] appWidgetIds) {
        MetricsViewsStorage instance = MetricsViewsStorage.getInstance();
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            for (String tag : instance.getTags()) {
                MetricWidgetUtils.deleteKey(context, appWidgetId, tag + ".visibility");
                MetricWidgetUtils.deleteKey(context, appWidgetId, tag + ".value");
            }
        }
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        if (WIDGET_CLICK.equals(intent.getAction())) {
            Intent intentSending = new Intent(context, RootActivity.class);
            intentSending.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intentSending);
        }
        super.onReceive(context, intent);
    }
}

