package com.silicium.otusfilmcatalog.ui.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.controller.MetricsViewsStorage;

/**
 * The configuration screen for the {@link MetricsWidget MetricsWidget} AppWidget.
 */
public class MetricsWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "com.silicium.otusfilmcatalog.ui.widget.MetricsWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = MetricsWidgetConfigureActivity.this;

            // When the button is clicked, store the settings locally
            MetricsViewsStorage metricsViewsStorage = MetricsViewsStorage.getInstance();
            LinearLayout rootLayout = findViewById(R.id.metrics_widget_configure);

            for (String metricTag : metricsViewsStorage.getTags()) {
                CheckBox checkBox = rootLayout.findViewWithTag(metricTag);
                saveBoolean(MetricsWidgetConfigureActivity.this, mAppWidgetId, metricTag + ".visibility", checkBox.isChecked());
            }

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            MetricsWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public MetricsWidgetConfigureActivity() {
        super();
    }

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

    static void saveInteger(@NonNull Context context, int appWidgetId, String key, Integer value) {
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

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.metrics_widget_configure);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        MetricsViewsStorage metricsViewsStorage = MetricsViewsStorage.getInstance();
        LinearLayout rootLayout = findViewById(R.id.metrics_widget_configure);

        for (String metricTag : metricsViewsStorage.getTags()) {
            CheckBox checkBox = rootLayout.findViewWithTag(metricTag);
            checkBox.setText(metricsViewsStorage.getNameByTag(metricTag));
            Boolean isChecked = loadBoolean(this, mAppWidgetId, metricTag + ".visibility");
            checkBox.setChecked(isChecked);
        }

        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);
    }
}

