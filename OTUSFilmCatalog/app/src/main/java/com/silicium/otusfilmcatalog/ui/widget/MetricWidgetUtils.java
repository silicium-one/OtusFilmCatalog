package com.silicium.otusfilmcatalog.ui.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import static com.silicium.otusfilmcatalog.App.getApplication;

public class MetricWidgetUtils {
    private static final String PREFS_NAME = "com.silicium.otusfilmcatalog.ui.widget.MetricsWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";

    //region settings storage API
    static void saveBoolean(@NonNull Context context, int appWidgetId, String key, Boolean value) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putBoolean(PREF_PREFIX_KEY + appWidgetId + key, value);
        prefs.apply();
    }

    @NonNull
    static Boolean loadBoolean(@NonNull Context context, int appWidgetId, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(PREF_PREFIX_KEY + appWidgetId + key, false);
    }

    private static void saveInteger(@NonNull Context context, int appWidgetId, String key, Integer value) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY + appWidgetId + key, value);
        prefs.apply();
    }

    @NonNull
    static Integer loadInteger(@NonNull Context context, int appWidgetId, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getInt(PREF_PREFIX_KEY + appWidgetId + key, -1);
    }

    static void deleteKey(@NonNull Context context, int appWidgetId, String key) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId + key);
        prefs.apply();
    }
    //endregion

    public static void notifyWidget(@NonNull String tag, int value) {
        Context context = getApplication().getApplicationContext();
        Intent intent = new Intent(context, MetricsWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), MetricsWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        for (int appWidgetId : ids)
            MetricWidgetUtils.saveInteger(context, appWidgetId, tag + ".value", value);
        getApplication().getApplicationContext().sendBroadcast(intent);
    }
}
