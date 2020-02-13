package com.silicium.otusfilmcatalog.ui.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.silicium.otusfilmcatalog.R;
import com.silicium.otusfilmcatalog.logic.controller.MetricsViewsStorage;

public class MetricsWidgetConfigureActivity extends Activity {

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @SuppressLint("SyntheticAccessor")
        public void onClick(View v) {
            final Context context = MetricsWidgetConfigureActivity.this;

            // When the button is clicked, store the settings locally
            MetricsViewsStorage metricsViewsStorage = MetricsViewsStorage.getInstance();
            LinearLayout rootLayout = findViewById(R.id.metrics_widget_configure);

            for (String metricTag : metricsViewsStorage.getTags()) {
                CheckBox checkBox = rootLayout.findViewWithTag(metricTag);
                MetricWidgetUtils.saveBoolean(MetricsWidgetConfigureActivity.this, mAppWidgetId, metricTag + ".visibility", checkBox.isChecked());
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            Boolean isChecked = MetricWidgetUtils.loadBoolean(this, mAppWidgetId, metricTag + ".visibility");
            checkBox.setChecked(isChecked);
        }

        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);
    }
}

